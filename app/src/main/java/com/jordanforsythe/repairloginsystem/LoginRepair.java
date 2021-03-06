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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jordanforsythe.repairloginsystem.Repair.Repair;

public class LoginRepair extends AppCompatActivity implements View.OnClickListener, ChildEventListener {

    //setting the isntance of firebase database and auth
    public static final String REPAIR_FIREBASE_KEY = "repairs";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference repairs = firebaseDatabase.getReference(REPAIR_FIREBASE_KEY);
    private FirebaseAuth mAuth;
    //initialising the visual elements used in the class
    private ImageButton imageButtonSendRepairData;
    private EditText editTextCustomerName;
    private EditText editTextImeiNumber;
    private EditText editTextFaultDescription;
    private EditText editTextCustomerPhone;
    private EditText editTextCustomerEmail;
    private EditText editTextStandbyPhoneImei;
    private int nextRepairJobNumber;
    private Repair tempRepair = new Repair();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_repair);
        //setting the action bar to have a back button and text
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login Repair");
        mAuth = FirebaseAuth.getInstance();

        //checking if the current user exsists and if not returning to the login screen
        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }

        //setting the visual elements to their layout ID
        imageButtonSendRepairData = findViewById(R.id.imageButton_SendRepairLogin);
        editTextCustomerName = findViewById(R.id.editText_CustomerName);
        editTextImeiNumber = findViewById(R.id.editText_ImeiNumber);
        editTextFaultDescription = findViewById(R.id.editText_FaultDescription);
        editTextCustomerEmail = findViewById(R.id.editText_CustomerEmailAddress);
        editTextCustomerPhone = findViewById(R.id.editText_CustomerPhoneNumber);
        editTextStandbyPhoneImei = findViewById(R.id.editText_standbyPhoneImei);

        //adding onclick listener for creating the repair
        imageButtonSendRepairData.setOnClickListener(this);

        repairs.addChildEventListener(this);
    }//on create

    //method to send the repair to the repair databse
    private void pushRepairToFirebase(){

        try {

            int jobNumber = tempRepair.getJobNumber();

            //setting the variables to the values of the edit texts
            String customerName = editTextCustomerName.getText().toString();
            String customerPhone = editTextCustomerPhone.getText().toString();
            String customerEmail = editTextCustomerEmail.getText().toString();
            String imeiNumber = editTextImeiNumber.getText().toString();
            String faultDescription = editTextFaultDescription.getText().toString();
            long currentDate = System.currentTimeMillis();
            jobNumber++;
            String engineerNotes = "";
            FirebaseUser user = mAuth.getCurrentUser();
            String username = user.getEmail();
            String repairStatusLoggedIn = "Logged In";

            String standbyPhoneIMEI = editTextStandbyPhoneImei.getText().toString();

            //if the standby field is empty then set it to no standby given
            if (standbyPhoneIMEI.isEmpty()) {
                standbyPhoneIMEI = "No standby given";
            }

            //setting a new repair to the value of all of the variables
            Repair repairToSend = new Repair(customerName, customerPhone, customerEmail, imeiNumber, faultDescription, jobNumber,
                    repairStatusLoggedIn, currentDate, standbyPhoneIMEI, engineerNotes, username);

            //sending the final repair to firebase
            repairs.push().setValue(repairToSend);

            //displaying a customer layout dialog giving the user the job number of the repair booked in
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_repair_logged_in, null);
            String textForDialog = "Your customers job number is " + jobNumber + ".\nPlease give this to the customer";

            TextView textViewDialogText = mView.findViewById(R.id.textView_RepairNumberSubmitted);
            ImageButton imageButtonDialogText = mView.findViewById(R.id.imageButton_RepairLoggedInDone);

            textViewDialogText.setText(textForDialog);

            mBuilder.setView(mView);
            final AlertDialog dialogRepairLoggedIn = mBuilder.create();
            dialogRepairLoggedIn.show();

            imageButtonDialogText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogRepairLoggedIn.dismiss();
                    startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                    finish();
                }
            });
        }//try
        catch (Exception e){
            Toast.makeText(this,"ERROR: Unable to send repair to firebase",Toast.LENGTH_LONG).show();
        }

    }//Push Repair to Firebase method

    //onclick method to run when button is pressed
    @Override
    public void onClick(View view) {
        if(view == imageButtonSendRepairData){
            //checking fields are not empty first
            if(checkFieldsAreNotEmpty()) {

                //running the method to query the next job number
                queryNextJobNo();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //after 1 second
                        //running the method to send the repair to firebase
                        pushRepairToFirebase();
                    }
                }, 1000);
            }
        }
    }//On click method

    //method to check that all of the required fields are not empty
    public boolean checkFieldsAreNotEmpty(){

        boolean areallempty = false;
        String dialogMessage = "";

        //if statements to check if the fields meet the requirements
        if(editTextCustomerName.getText().toString().length() > 0){

            if(editTextCustomerPhone.getText().toString().length() == 11){

                if(editTextCustomerEmail.getText().toString().length() > 0){

                    if(editTextImeiNumber.getText().toString().length() == 15){

                        if(editTextFaultDescription.getText().toString().length() > 20){
                            areallempty = true;
                        }
                        else{
                            //fault not long enough
                            dialogMessage = "Please enter a detailed fault note";
                        }
                    }//imei
                    else{
                        //imei not long enough
                        dialogMessage = "Please enter a 15 digit IMEI number";
                    }
                }//email
                else{
                    //email not long enough
                    dialogMessage = "Please enter a Customer Email";
                }
            }//phone
            else{
                //phone not long enough
                dialogMessage = "Please enter a Customer phone number of 11 digits";
            }
        }//name
        else{
            //name not long enough
            dialogMessage = "Please enter a Customer Name";
        }

        //if still set to false then tell the user what the issue is in a dialog box
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

    //method to get the next job number avaliable
    private void queryNextJobNo(){

        //query to get the last repair
        Query firebaseDatabaseQuery = repairs.orderByChild("jobNumber").limitToLast(1);

        try {

            firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //datasnapshot listener
                    for (DataSnapshot repairsnapshot : dataSnapshot.getChildren()) {

                        //setting the values of temp repair to the last job mnumber
                        String jobNumber = (String) repairsnapshot.child("jobNumber").getValue().toString();
                        nextRepairJobNumber = (Integer.parseInt(jobNumber));
                        tempRepair.setJobNumber(nextRepairJobNumber);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });//Query for the job
        }
        catch (Exception e){
            Toast.makeText(this,"ERROR: Unable to generate job number",Toast.LENGTH_LONG).show();
        }
    }//Query next job number method

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    //method to override the backm button pressed and show a dialog to ask if the user wanted to exit
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

        AlertDialog.Builder exitAlert = new AlertDialog.Builder(this);
        exitAlert.setMessage("Are you sure you want to exit?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


    }//on back pressed\

    //method to take the user back to the home screen is the action bar back is pressed
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeScreen.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;
    }//Make menu arrow button work

    // method to override the finish transition
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    //method to log the user out on restart of the activity
    @Override
    protected void onRestart() {
        super.onRestart();
        mAuth.signOut();
        startActivity(new Intent(this, LoginScreen.class));
        finish();
    }
}//loginrepair
