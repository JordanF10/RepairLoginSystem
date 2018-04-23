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

    //declaring instances of firebase database and auth
    public static final String REPAIR_FIREBASE_KEY = "repairs";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference repairs = firebaseDatabase.getReference(REPAIR_FIREBASE_KEY);
    private FirebaseAuth mAuth;

    //declaring all of the visual elements
    private EditText editTextEngineerRepairNumber;
    private Spinner spinnerEngineerRepairOptions;
    private EditText editTextEngineerRepairNotes;
    private ImageButton imageButtonEngineerUpdateRepairSend;
    private Repair tempRepair = new Repair();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer_screen);
        //setting the back button and name of the activity in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Engineer Hub");
        mAuth = FirebaseAuth.getInstance();

        //checking to see if the current user is logged in, if not returning them to the login screen
        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }

        //setting all of the visual elements to their ID contained in the layout
        editTextEngineerRepairNumber = findViewById(R.id.editText_EngineerRepairNumber);
        spinnerEngineerRepairOptions = findViewById(R.id.spinner_EngineerRepairOption);
        editTextEngineerRepairNotes = findViewById(R.id.editText_EngineerNotes);
        imageButtonEngineerUpdateRepairSend = findViewById(R.id.imageButton_EngineerUpdateRepair);

        //setting an onclick listener on the update job button
        imageButtonEngineerUpdateRepairSend.setOnClickListener(this);
    }//on create

    //method to send the engineer update to firebase database
    private void sendEngineerUpdateToFirebase(){
        try {
            //setting the database key and username
            String repairJobKey = tempRepair.getDatabaseAutomaticKey();
            FirebaseUser user = mAuth.getCurrentUser();
            String username = user.getEmail();

            //if the repair job key isnt null then update the repair
            if (repairJobKey != null) {
                //getting the repair typed in and updating the repair status
                repairs.child(repairJobKey).child("repairStatus").setValue(spinnerEngineerRepairOptions.getSelectedItem().toString());

                //adding the current data and time to the engineer notes update
                long currentDate = System.currentTimeMillis();
                tempRepair.setTimeDateBookedIn(currentDate);
                String formattedDate = tempRepair.getFormattedTimestamp();

                //if the job notes are null then format them alone, if not then add the existing notes onto the engineer notes being added
                if (tempRepair.getJobNotes() != null) {
                    repairs.child(repairJobKey).child("engineerNotes").setValue("\n" + "Engineer: " + username + "\n" + formattedDate + "\nNotes: " +
                            editTextEngineerRepairNotes.getText().toString() + "\n" + tempRepair.getJobNotes());
                } else {
                    repairs.child(repairJobKey).child("engineerNotes").setValue("\n" + "Engineer: " + username + "\n" + formattedDate + "\nNotes: " + editTextEngineerRepairNotes.getText().toString());
                }

                //setting a dialog box to ipen telling the user the repair had been updated.
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
            }//end of if repair job key isnt null
        }//end of try
        catch (Exception e){
            Toast.makeText(this,"ERROR: Unable to update Repair Job",Toast.LENGTH_LONG).show();
        }
    }//send update to firebase

    //method to get the repair job typed in
    private void getRepairJob(){

        //setting the job number to an integer
        int jobNumberTyped = Integer.parseInt(editTextEngineerRepairNumber.getText().toString());

        //creating a query to search the database for the job number entered
        Query firebaseDatabaseQuery = repairs.orderByChild("jobNumber").equalTo(jobNumberTyped);

        try {
            firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
                //running the query
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //if the datasnapshot exsists then extract the data inside
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot repairsnapshot : dataSnapshot.getChildren()) {

                            //setting the key of a temp repair to the key of the job to be updated
                            tempRepair.setDatabaseAutomaticKey(repairsnapshot.getKey());

                            //getting the engineer note if they exsist already
                            if (repairsnapshot.child("engineerNotes").getValue() != null) {
                                tempRepair.setJobNotes(repairsnapshot.child("engineerNotes").getValue().toString());
                            }//if to check if notes have been added before
                        }
                    }//end of if
                    //if the datasnapshot does not exsist tell the user the job number is not found in a dialog box
                    else {
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
                    }//end of else
                }//datasnapshot on change

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });//query event listener
        }//end of try
        catch (Exception e){
            System.out.println("ERROR: Unable to get repair job");
            Toast.makeText(this,"ERROR: Unable to get repair job",Toast.LENGTH_LONG).show();
        }//end of catch
    }//get repair job number

    //method to override back button being pressed
    @Override
    public void onBackPressed(){

        //dialog asking the user if they really want to exit
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

    //method for onclick
    @Override
    public void onClick(View view) {
        //if the buttontoupdate the repaid is clicked
        if( view == imageButtonEngineerUpdateRepairSend) {
            //checking fields are not empty and setting the temp repair job notes to empty. Getting the repair job typed in
            if (checkFieldsAreNotEmpty()) {
                tempRepair.setJobNotes("");
                getRepairJob();

                // if temprepair isnt null then wait 1 second and send the repair to firebase
                if(tempRepair != null) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // Actions to do after 1 second
                            sendEngineerUpdateToFirebase();
                        }
                    }, 1000);
                }
            }
        }
    }//on click

    //method to check if the fields are not empty and return true if they are not empty
    public boolean checkFieldsAreNotEmpty(){

        boolean areallempty = false;
        String dialogMessage = "";

        //displaying a dialog box if a repair number or job notes are left empty
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

        //if still false then show the dialog box with the text detailing what is wrong
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

    //method for the back button in the action bar work
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeScreen.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;
    }

    //method to override the finish transition
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    //overriding the onrestart to logout the user if the activity is closed.
    @Override
    protected void onRestart() {
        super.onRestart();
        mAuth.signOut();
        startActivity(new Intent(this, LoginScreen.class));
        finish();
    }
}//class
