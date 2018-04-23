package com.jordanforsythe.repairloginsystem;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jordanforsythe.repairloginsystem.Repair.Repair;

public class CheckRepairStatus extends AppCompatActivity implements View.OnClickListener {

    //declaring instances of firebase database and auth
    public static final String REPAIR_FIREBASE_KEY = "repairs";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference repairs = firebaseDatabase.getReference(REPAIR_FIREBASE_KEY);
    FirebaseAuth mAuth;
    //declaring all of the visual elements
    private EditText editTextRepairQuery;
    private ImageButton imageButtonRepairQuery;
    private TextView textViewJobNumberReturned;
    private TextView textViewJobStatusReturned;
    private TextView textViewDateBookedInReturned;
    private TextView textViewCustomerNameReturned;
    private TextView textViewCustomerPhoneReturned;
    private TextView textViewCustomerEmailReturned;
    private TextView textViewCustomerImeiReturned;
    private TextView textViewCustomerFaultReturned;
    private TextView textViewCustomerStandbyImeiReturned;
    private TextView textViewEngineerNotesReturned;
    private TextView textViewStatusLoggedInBy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_repair_status);
        //setting the back button and name of the activity in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Check Repair");

        mAuth = FirebaseAuth.getInstance();

        //checking to see if the current user is logged in, if not returning them to the login screen
        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }

        //setting all of the visual elements to their ID contained in the layout
        editTextRepairQuery = findViewById(R.id.editText_RepairQuery);
        imageButtonRepairQuery = findViewById(R.id.imageButton_RepairQuery);
        textViewJobNumberReturned = findViewById(R.id.textView_StatusJobNumber);
        textViewJobStatusReturned = findViewById(R.id.textView_StatusCurrent);
        textViewDateBookedInReturned = findViewById(R.id.textView_StatusDateBookedIn);
        textViewCustomerNameReturned = findViewById(R.id.textView_StatusCustomerName);
        textViewCustomerPhoneReturned = findViewById(R.id.textView_StatusCustomerPhone);
        textViewCustomerEmailReturned = findViewById(R.id.textView_StatusCustomerEmail);
        textViewCustomerImeiReturned = findViewById(R.id.textView_StatusCustomerIMEI);
        textViewCustomerFaultReturned = findViewById(R.id.textView_StatusCustomerFault);
        textViewCustomerStandbyImeiReturned = findViewById(R.id.textView_StatusStandbyIMEI);
        textViewEngineerNotesReturned = findViewById(R.id.textView_StatusEngineerNotes);
        textViewStatusLoggedInBy = findViewById(R.id.textView_StatusLoggedInBy);

        //setting an onclick listener on the search button
        imageButtonRepairQuery.setOnClickListener(this);
    }

    //method to search firebase for the repair
    private void searchFirebase() {

        //taking the job number typed and  converting it to an integer
        int jobNumberTyped = Integer.parseInt(editTextRepairQuery.getText().toString());

        //firebase query to serach the repairs database by the jobnumber field for a job equal to the job number typed
        Query firebaseDatabaseQuery = repairs.orderByChild("jobNumber").equalTo(jobNumberTyped);

        try {
            //running the query
            firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //taking the datasnapshot returned from the query and running this method when it changes

                    //checking if the datasnapshot exsists before tyring to take any information from it
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot repairsnapshot : dataSnapshot.getChildren()) {

                            //making variables and setting them to the values in the repairsnapshot
                            String jobNumber = (String) repairsnapshot.child("jobNumber").getValue().toString();
                            String status = (String) repairsnapshot.child("repairStatus").getValue();
                            String date = (String) repairsnapshot.child("formattedTimestamp").getValue();
                            String loggedInBy = (String) repairsnapshot.child("loggedInBy").getValue();
                            String name = (String) repairsnapshot.child("customerName").getValue();
                            String phone = (String) repairsnapshot.child("customerPhoneNumber").getValue();
                            String email = (String) repairsnapshot.child("customerEmailAddress").getValue();
                            String imei = (String) repairsnapshot.child("imeiNumber").getValue();
                            String fault = (String) repairsnapshot.child("faultDescription").getValue();
                            String standbyImei = (String) repairsnapshot.child("standbyPhoneIMEI").getValue();
                            String engineerNotes = (String) repairsnapshot.child("engineerNotes").getValue();

                            //setting the visual elements to the contents of the strings but formatted.
                            textViewJobNumberReturned.setText("Job Number: \n" + jobNumber);
                            textViewJobStatusReturned.setText("Repair Status: \n" + status);
                            textViewDateBookedInReturned.setText("Created on: \n" + date);
                            textViewStatusLoggedInBy.setText("Logged in by: \n" + loggedInBy);
                            textViewCustomerNameReturned.setText("Customer Name: \n" + name);
                            textViewCustomerPhoneReturned.setText("Customer Phone: \n" + phone);
                            textViewCustomerEmailReturned.setText("Customer Email: \n" + email);
                            textViewCustomerImeiReturned.setText("Handset IMEI: \n" + imei);
                            textViewCustomerFaultReturned.setText("Reported Fault: \n" + fault);
                            textViewCustomerStandbyImeiReturned.setText("Standby IMEI: \n" + standbyImei);

                            //checking if engineer notes exsist and if not printing out no engineer notes yet
                            if (engineerNotes.isEmpty()) {
                                textViewEngineerNotesReturned.setText("Engineer Notes: \nNo Engineer notes yet!");
                            } else {
                                textViewEngineerNotesReturned.setText("Engineer Notes: \n" + engineerNotes);
                            }

                            //hiding the keyboard after this method is ran
                            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                        }//datasnaoshot for
                    } else {
                        //showing a dialog box when there is no repair job found telling the user
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
                        AlertDialog.Builder fieldAlert = new AlertDialog.Builder(CheckRepairStatus.this);
                        fieldAlert.setMessage("Repair job number not found").setPositiveButton("OK", dialogClickListener).show();
                    }//end of datasnapshot else
                }//end of data snapshot

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //printing out database error as a toast and system out
                    System.out.println("Database Error");
                    Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_LONG).show();
                }//database error
            });//query event listener
        }//try
        catch (Exception e){
            System.out.println("ERROR: Unable to search firebase");
            Toast.makeText(this,"ERROR: Unable to search firebase",Toast.LENGTH_LONG).show();
        }//catch
    }//method search firebase

    @Override
    public void onClick(View view) {

        //on click listener for button search
        if (view == imageButtonRepairQuery) {
            //checking the fields are not empty and only searching firebase if true
            if(checkFieldsAreNotEmpty()) {
                searchFirebase();
            }//end of checkfield if
        }//end of button if
    }//method onclick

    //method to check that fields are not empty and return true if they are
    public boolean checkFieldsAreNotEmpty(){

        //setting a boolean value to false at the start
        boolean areallempty = false;
        String dialogMessage = "";

        //checking if the tet entrys have values
        if(editTextRepairQuery.getText().toString().length() > 0){
            areallempty = true;
        }
        else{
            dialogMessage = "Please enter a Repair Number";
        }

        //displaying a dialog saying that the fields are empty and needs filled in
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


    }

    //method to manage the back button in the action bar
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

    //method to log the user out when the activity is closed and restarted
    @Override
    protected void onRestart() {
        super.onRestart();
        mAuth.signOut();
        startActivity(new Intent(this, LoginScreen.class));
        finish();
    }

}


