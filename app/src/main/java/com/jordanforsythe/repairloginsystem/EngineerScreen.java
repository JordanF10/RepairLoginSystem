package com.jordanforsythe.repairloginsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

    private EditText editTextEngineerRepairNumber;
    private Spinner spinnerEngineerRepairOptions;
    private EditText editTextEngineerRepairNotes;
    private Button buttonEngineerUpdateRepairSend;
    private int jobNumberTyped;
    private String repairJobKey;
    private Repair tempRepair = new Repair();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextEngineerRepairNumber = findViewById(R.id.editText_EngineerRepairNumber);
        spinnerEngineerRepairOptions = findViewById(R.id.spinner_EngineerRepairOption);
        editTextEngineerRepairNotes = findViewById(R.id.editText_EngineerNotes);
        buttonEngineerUpdateRepairSend = findViewById(R.id.button_EngineerUpdateRepair);

        buttonEngineerUpdateRepairSend.setOnClickListener(this);

    }//on create

    private void sendEngineerUpdateToFirebase(){

            repairJobKey = tempRepair.getDatabaseAutomaticKey();
            System.out.println(tempRepair.getDatabaseAutomaticKey());
            System.out.println(repairJobKey);

            if(repairJobKey != null) {
                repairs.child(repairJobKey).child("repairStatus").setValue(spinnerEngineerRepairOptions.getSelectedItem().toString());

                long currentDate = System.currentTimeMillis();
                tempRepair.setTimeDateBookedIn(currentDate);
                String formattedDate = tempRepair.getFormattedTimestamp();

                if(tempRepair.getJobNotes() != null) {
                    repairs.child(repairJobKey).child("engineerNotes").setValue(formattedDate + ": " + editTextEngineerRepairNotes.getText().toString() +"\n"+ tempRepair.getJobNotes() );
                }

                else{
                    repairs.child(repairJobKey).child("engineerNotes").setValue(formattedDate + ": " + editTextEngineerRepairNotes.getText().toString());
                }
            }

            else{
                System.out.println("key was null");
            }



    }//send update to firebase

    private void getRepairJob(){

        jobNumberTyped = Integer.parseInt(editTextEngineerRepairNumber.getText().toString());

        Query firebaseDatabaseQuery = repairs.orderByChild("jobNumber").equalTo(jobNumberTyped);

        firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot repairsnapshot: dataSnapshot.getChildren()) {

                    tempRepair.setDatabaseAutomaticKey(repairsnapshot.getKey());

                    if(repairsnapshot.child("engineerNotes").getValue() != null) {
                        tempRepair.setJobNotes(repairsnapshot.child("engineerNotes").getValue().toString());
                    }//if to check if notes have been added before
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

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
        if( view == buttonEngineerUpdateRepairSend){
            tempRepair.setJobNotes("");
            getRepairJob();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 10 seconds
                    sendEngineerUpdateToFirebase();
                }
            }, 1000);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeScreen.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;
    }
}//class
