package com.jordanforsythe.repairloginsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jordanforsythe.repairloginsystem.Repair.Repair;

public class LogoutRepair extends AppCompatActivity implements View.OnClickListener{

    public static final String REPAIR_FIREBASE_KEY = "repairs";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference repairs = firebaseDatabase.getReference(REPAIR_FIREBASE_KEY);

    private Button buttonLogoutRepair;
    private EditText editTextLogoutRepairNumber;
    private Repair tempRepair = new Repair();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_repair);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonLogoutRepair = findViewById(R.id.button_LogoutRepair);
        editTextLogoutRepairNumber = findViewById(R.id.editText_LogoutRepairNumber);

        buttonLogoutRepair.setOnClickListener(this);

    }

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
        if(view == buttonLogoutRepair){

            if(checkFieldsAreNotEmpty()) {
                getRepairJob();
            }
        }
    }

    private void changeRepairToLoggedOut(){

        String repairJobKey = tempRepair.getDatabaseAutomaticKey();
        String repairStatusBefore = tempRepair.getRepairStatus();

        if(repairStatusBefore.equals("Logged Out")) {

            String dialogTextAlreadyLoggedOut = "Cannot logout, repair already logged out";
            alreadyLoggedOut(dialogTextAlreadyLoggedOut);

        }
        else{
            if(repairStatusBefore.equals("Canceled")) {

                String dialogTextCanceled = "Repair canceled, cannot logout.";
                alreadyLoggedOut(dialogTextCanceled);

            }
            else{
                repairs.child(repairJobKey).child("repairStatus").setValue("Logged Out");

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_repair_logged_out, null);
                String textForDialog = "Job logged out";

                TextView textViewDialogText = mView.findViewById(R.id.textView_DialogRepairLoggedOut);
                ImageButton imageButtonDialogText = mView.findViewById(R.id.imageButton_DialogRepairLoggedOut);

                textViewDialogText.setText(textForDialog);

                mBuilder.setView(mView);
                final AlertDialog dialogLoggedOut = mBuilder.create();
                dialogLoggedOut.show();

                imageButtonDialogText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogLoggedOut.dismiss();
                        startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                        finish();
                    }
                });

            }
        }


    }

    private void getRepairJob(){

        int jobNumberTyped = Integer.parseInt(editTextLogoutRepairNumber.getText().toString());

        Query firebaseDatabaseQuery = repairs.orderByChild("jobNumber").equalTo(jobNumberTyped);

        firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot repairsnapshot : dataSnapshot.getChildren()) {

                        tempRepair.setDatabaseAutomaticKey(repairsnapshot.getKey());
                        tempRepair.setRepairStatus(repairsnapshot.child("repairStatus").getValue().toString());
                    }

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //after 1 second
                            changeRepairToLoggedOut();
                        }
                    }, 1000);
                }
                else{
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

                    AlertDialog.Builder fieldAlert = new AlertDialog.Builder(LogoutRepair.this);
                    fieldAlert.setMessage("Repair job not found").setPositiveButton("OK", dialogClickListener).show();
                }
            }

                @Override
                public void onCancelled (DatabaseError databaseError){
                }
        });

    }//get repair job number

    public boolean checkFieldsAreNotEmpty(){

        boolean areallempty = false;
        String dialogMessage = "";

        if(editTextLogoutRepairNumber.getText().toString().length() > 0){
            areallempty = true;
        }
        else{
            dialogMessage = "Please enter a Repair Number to logout";
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

    private void alreadyLoggedOut(String dialogText){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_repair_logged_out, null);

        TextView textViewDialogText = mView.findViewById(R.id.textView_DialogRepairLoggedOut);
        ImageButton imageButtonDialogText = mView.findViewById(R.id.imageButton_DialogRepairLoggedOut);

        textViewDialogText.setText(dialogText);

        mBuilder.setView(mView);
        final AlertDialog dialogAlreadyCanceled = mBuilder.create();
        dialogAlreadyCanceled.show();

        imageButtonDialogText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlreadyCanceled.dismiss();
            }
        });

    }

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


}
