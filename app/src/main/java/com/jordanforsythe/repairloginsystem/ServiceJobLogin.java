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
import com.jordanforsythe.repairloginsystem.ServiceJob.ServiceJob;

public class ServiceJobLogin extends AppCompatActivity implements View.OnClickListener, ChildEventListener {

    //initialising the firebase database and auth
    public static final String REPAIR_FIREBASE_KEY_SERVICE = "serviceJobs";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference serviceJobs = firebaseDatabase.getReference(REPAIR_FIREBASE_KEY_SERVICE);
    private FirebaseAuth mAuth;

    //initialising the visual elements
    private EditText editTextServiceJobCustomerName;
    private EditText editTextServiceJobCustomerPhone;
    private EditText editTextServiceJobFaultNotes;
    private ImageButton imageButtonServiceJobCreate;
    private int nextServiceJobNumber;
    private ServiceJob tempServiceJob = new ServiceJob();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_job_login);
        //setting the action abr to have back arrow and text
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login Service Job");
        mAuth = FirebaseAuth.getInstance();

        //checking to see if the user is logged in and if not returning to the login screen
        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }

        //setting the visual obkects to their layout ID
        editTextServiceJobCustomerName = findViewById(R.id.editText_ServiceJobCustomerName);
        editTextServiceJobCustomerPhone = findViewById(R.id.editText_ServiceJobCustomerPhone);
        editTextServiceJobFaultNotes = findViewById(R.id.editText_ServiceJobFault);
        imageButtonServiceJobCreate = findViewById(R.id.imageButton_ServiceJobCreate);

        imageButtonServiceJobCreate.setOnClickListener(this);

        serviceJobs.addChildEventListener(this);
    }//omcreate

    //method to query the next job mumber
    private void queryNextJobNo(){

        //query to find the last service job
        Query firebaseDatabaseQuery = serviceJobs.orderByChild("serviceJobNumber").limitToLast(1);

        try {
            firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
                //running query
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //setting the temp service job number to the last service job number
                        for (DataSnapshot servicesnapshot : dataSnapshot.getChildren()) {

                        String serviceJobNumber = (String) servicesnapshot.child("serviceJobNumber").getValue().toString();
                        nextServiceJobNumber = (Integer.parseInt(serviceJobNumber));
                        tempServiceJob.setServiceJobNumber(nextServiceJobNumber);
                    }
                }//datasnapshot

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });//Query for the job
        }//try
        catch (Exception e){
            Toast.makeText(this,"ERROR: Unable to generate service job number",Toast.LENGTH_LONG).show();
        }
    }//Query next job number method

    //method to show a dialog box after service job is submitted with the job number
    private void showDialogAfterSubmitted(String serviceJobNumber) {

        //printing out the service job number on the dialog
        String dialogText = "Service Job Logged in, the service job number is " + serviceJobNumber;
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_repair_logged_out, null);

        TextView textViewDialogText = mView.findViewById(R.id.textView_DialogRepairLoggedOut);
        ImageButton imageButtonDialogText = mView.findViewById(R.id.imageButton_DialogRepairLoggedOut);

        textViewDialogText.setText(dialogText);

        mBuilder.setView(mView);
        final AlertDialog dialogServiceJobLoggedIn = mBuilder.create();
        dialogServiceJobLoggedIn.show();

        imageButtonDialogText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogServiceJobLoggedIn.dismiss();
                startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                finish();
            }
        });
    }//dialog show

    //onclick method
    @Override
    public void onClick(View view) {
        if(view == imageButtonServiceJobCreate){
            //check if the fields are not empty
            if(checkFieldsAreNotEmpty()) {
                //try and send the service job to firebase
                queryNextJobNo();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //after 1 second
                        pushServiceJobToFirebase();
                    }
                }, 1000);
            }
        }
    }//onclick

    //method to push the service job to firebase
    private void pushServiceJobToFirebase(){
        try {
            //querying the next service job number

            int serviceJobNumber = tempServiceJob.getServiceJobNumber();
            System.out.println(serviceJobNumber);
            //setting the variables needed for a service job from the text entry fields
            String customerName = editTextServiceJobCustomerName.getText().toString();
            String customerPhone = editTextServiceJobCustomerPhone.getText().toString();
            long currentDate = System.currentTimeMillis();
            serviceJobNumber++;
            FirebaseUser user = mAuth.getCurrentUser();
            String username = user.getEmail();
            tempServiceJob.setServiceJobtimeDateBookedIn(currentDate);
            String datetime = tempServiceJob.getFormattedTimestamp();
            String serviceFault = ("\nAgent: " + username + "\n" + datetime + "\nNotes: " + editTextServiceJobFaultNotes.getText().toString());

            //creating a service job and populating it with the data
            ServiceJob serviceJobToSend = new ServiceJob(customerName, customerPhone, serviceFault, serviceJobNumber, currentDate, username);

            //sending the service job to firebase
            serviceJobs.push().setValue(serviceJobToSend);

            //showing the dialog box with the job number
            showDialogAfterSubmitted(String.valueOf(serviceJobNumber));
        }//try
        catch (Exception e){
            Toast.makeText(this,"ERROR: Unable to send service job",Toast.LENGTH_LONG).show();
        }
    }//push repair to firebase

    //method to override back pressed and ask user to confirm
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


    }//on back pressed for dialog box

    //method to make the menu back button work
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeScreen.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;
    }//make menu back button work

    //method to check all fields are not emoty
    public boolean checkFieldsAreNotEmpty(){

        boolean areallempty = false;
        String dialogMessage = "";

        if(editTextServiceJobCustomerName.getText().toString().length() > 0){
            if(editTextServiceJobCustomerPhone.getText().toString().length() == 11) {
                if(editTextServiceJobFaultNotes.getText().toString().length() > 10) {
                    areallempty = true;
                }
                else{
                    dialogMessage="Please enter detailed service job notes";
                }
            }
            else{
                dialogMessage="Please enter 11 digit phone number";
            }
        }
        else{
            dialogMessage = "Please enter a Customer Name";
        }

        //if still false show the dialog box to the user with the issue
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

    //method to override the finish transition
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    //method to override onrestart and log user out
    @Override
    protected void onRestart() {
        super.onRestart();
        mAuth.signOut();
        startActivity(new Intent(this, LoginScreen.class));
        finish();
    }
}//class
