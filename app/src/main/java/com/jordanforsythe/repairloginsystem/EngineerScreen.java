package com.jordanforsythe.repairloginsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jordanforsythe.repairloginsystem.Repair.Repair;

public class EngineerScreen extends AppCompatActivity implements View.OnClickListener {

    public static final String REPAIR_FIREBASE_KEY = "repairs";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference repairs = firebaseDatabase.getReference(REPAIR_FIREBASE_KEY);
    private FirebaseAuth mAuth;

    private EditText editTextEngineerRepairNumber;
    private Spinner spinnerEngineerRepairOptions;
    private EditText editTextEngineerRepairNotes;
    private ImageButton imageButtonEngineerUpdateRepairSend;
    private Repair tempRepair = new Repair();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Engineer Hub");
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }

        editTextEngineerRepairNumber = findViewById(R.id.editText_EngineerRepairNumber);
        spinnerEngineerRepairOptions = findViewById(R.id.spinner_EngineerRepairOption);
        editTextEngineerRepairNotes = findViewById(R.id.editText_EngineerNotes);
        imageButtonEngineerUpdateRepairSend = findViewById(R.id.imageButton_EngineerUpdateRepair);

        imageButtonEngineerUpdateRepairSend.setOnClickListener(this);

    }//on create

    private void sendEngineerUpdateToFirebase(){
        try {
            String repairJobKey = tempRepair.getDatabaseAutomaticKey();
            FirebaseUser user = mAuth.getCurrentUser();
            String username = user.getEmail();

            if (repairJobKey != null) {
                repairs.child(repairJobKey).child("repairStatus").setValue(spinnerEngineerRepairOptions.getSelectedItem().toString());

                long currentDate = System.currentTimeMillis();
                tempRepair.setTimeDateBookedIn(currentDate);
                String formattedDate = tempRepair.getFormattedTimestamp();

                if (tempRepair.getJobNotes() != null) {
                    repairs.child(repairJobKey).child("engineerNotes").setValue("\n" + "Engineer: " + username + "\n" + formattedDate + "\nNotes: " +
                            editTextEngineerRepairNotes.getText().toString() + "\n" + tempRepair.getJobNotes());
                } else {
                    repairs.child(repairJobKey).child("engineerNotes").setValue("\n" + "Engineer: " + username + "\n" + formattedDate + "\nNotes: " + editTextEngineerRepairNotes.getText().toString());
                }

                String dialogText = "Repair Updated";
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_repair_logged_out, null);

                TextView textViewDialogText = mView.findViewById(R.id.textView_DialogRepairLoggedOut);
                ImageButton imageButtonDialogText = mView.findViewById(R.id.imageButton_DialogRepairLoggedOut);

                textViewDialogText.setText(dialogText);

                mBuilder.setView(mView);
                final AlertDialog dialogEngineerRepairUpdated = mBuilder.create();
                dialogEngineerRepairUpdated.show();

                imageButtonDialogText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogEngineerRepairUpdated.dismiss();
                        startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                        finish();
                    }
                });

            }
        }
        catch (Exception e){
            Toast.makeText(this,"ERROR: Unable to update Repair Job",Toast.LENGTH_LONG).show();
        }
    }//send update to firebase

    private void getRepairJob(){

        int jobNumberTyped = Integer.parseInt(editTextEngineerRepairNumber.getText().toString());

        Query firebaseDatabaseQuery = repairs.orderByChild("jobNumber").equalTo(jobNumberTyped);

        try {

            firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot repairsnapshot : dataSnapshot.getChildren()) {

                            tempRepair.setDatabaseAutomaticKey(repairsnapshot.getKey());

                            if (repairsnapshot.child("engineerNotes").getValue() != null) {
                                tempRepair.setJobNotes(repairsnapshot.child("engineerNotes").getValue().toString());
                            }//if to check if notes have been added before
                        }
                    } else {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int option) {
                                switch (option) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };//dialog listener

                        AlertDialog.Builder fieldAlert = new AlertDialog.Builder(EngineerScreen.this);
                        fieldAlert.setMessage("Repair job not found").setPositiveButton("OK", dialogClickListener).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
        }
        catch (Exception e){
            System.out.println("ERROR: Unable to get repair job");
            Toast.makeText(this,"ERROR: Unable to get repair job",Toast.LENGTH_LONG).show();
        }

    }//get repair job number


    @Override
    public void onBackPressed(){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int option) {
                switch (option){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };//dialog listener

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


    }//on back pressed

    @Override
    public void onClick(View view) {
        if( view == imageButtonEngineerUpdateRepairSend) {
            if (checkFieldsAreNotEmpty()) {
                tempRepair.setJobNotes("");
                getRepairJob();


                if(tempRepair != null) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // Actions to do after 10 seconds
                            sendEngineerUpdateToFirebase();
                        }
                    }, 1000);
                }
            }
        }
    }

    public boolean checkFieldsAreNotEmpty(){

        boolean areallempty = false;
        String dialogMessage = "";

        if(editTextEngineerRepairNumber.getText().toString().length() > 0){
            if(editTextEngineerRepairNotes.getText().toString().length() > 20) {
                areallempty = true;
            }
            else{
                dialogMessage="Please enter detailed repair notes";
            }
        }
        else{
            dialogMessage = "Please enter a Repair Number";
        }

        if(!areallempty) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int option) {
                    switch (option) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };//dialog listener

            AlertDialog.Builder fieldAlert = new AlertDialog.Builder(this);
            fieldAlert.setMessage(dialogMessage).setPositiveButton("OK", dialogClickListener).show();

        }//show dialog box if something is empty

        return areallempty;

    }//check fields are not empty

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeScreen.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mAuth.signOut();
        startActivity(new Intent(this, LoginScreen.class));
        finish();
    }
}//class
