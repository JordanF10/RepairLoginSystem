package com.jordanforsythe.repairloginsystem;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.jordanforsythe.repairloginsystem.ServiceJob.ServiceJob;

public class ServiceJobUpdate extends AppCompatActivity implements View.OnClickListener {

    public static final String REPAIR_FIREBASE_KEY_SERVICE = "serviceJobs";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference serviceJobs = firebaseDatabase.getReference(REPAIR_FIREBASE_KEY_SERVICE);
    private FirebaseAuth mAuth;

    private ImageButton imageButtonServiceJobSearch;
    private ImageButton imageButtonServiceJobAddNotes;
    private EditText editTextServiceJobNumber;
    private TextView textViewServiceJobNumberReturned;
    private TextView textViewServiceJobCustomerName;
    private TextView textViewServiceJobCustomerPhone;
    private TextView textViewServiceJobNotes;
    private TextView textviewServiceJobAgentName;
    private ServiceJob tempServiceJob = new ServiceJob();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_job_update);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update Service Job");
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }

        imageButtonServiceJobSearch = findViewById(R.id.imageButton_ServiceJobSearch);
        imageButtonServiceJobAddNotes = findViewById(R.id.imageButton_ServiceJobAddNotes);
        editTextServiceJobNumber = findViewById(R.id.editText_ServiceJobSerach);
        textViewServiceJobCustomerName = findViewById(R.id.textView_ServiceJobCustomerName);
        textViewServiceJobCustomerPhone = findViewById(R.id.textView_ServiceJobCustomerPhone);
        textViewServiceJobNotes = findViewById(R.id.textView_ServiceJobNotes);
        textViewServiceJobNumberReturned = findViewById(R.id.textView_ServiceJobNumberReturned);
        textviewServiceJobAgentName = findViewById(R.id.textView_ServiceJobAgentName);


        imageButtonServiceJobSearch.setOnClickListener(this);
        imageButtonServiceJobAddNotes.setOnClickListener(this);

        imageButtonServiceJobAddNotes.setVisibility(View.GONE);

    }

    private void searchFirebaseServiceJob() {

        try {
            int serviceJobNumberTyped = Integer.parseInt(editTextServiceJobNumber.getText().toString());

            Query firebaseDatabaseQuery = serviceJobs.orderByChild("serviceJobNumber").equalTo(serviceJobNumberTyped);

            firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot servicesnapshot : dataSnapshot.getChildren()) {

                            String serviceJobNumber = (String) servicesnapshot.child("serviceJobNumber").getValue().toString();
                            String serviceJobCustomerName = (String) servicesnapshot.child("serviceJobCustomerName").getValue().toString();
                            String serviceJobCustomerPhone = (String) servicesnapshot.child("serviceJobCustomerPhone").getValue().toString();
                            String serviceJobNotes = (String) servicesnapshot.child("serviceJobFaultNotes").getValue().toString();
                            String serviceJobTimeBookedIn = (String) servicesnapshot.child("formattedTimestamp").getValue().toString();
                            String serviceJobAgentName = (String) servicesnapshot.child("serviceJobUsername").getValue().toString();
                            tempServiceJob.setserviceDatabaseAutomaticKey(servicesnapshot.getKey());
                            tempServiceJob.setServiceJobFaultNotes(servicesnapshot.child("serviceJobFaultNotes").getValue().toString());

                            textViewServiceJobNumberReturned.setText("Service Job Number: \n" + serviceJobNumber);
                            textviewServiceJobAgentName.setText("Booked in by: \n" + serviceJobAgentName);
                            textViewServiceJobCustomerName.setText("Customer Name: \n" + serviceJobCustomerName);
                            textViewServiceJobCustomerPhone.setText("Customer Phone: \n" + serviceJobCustomerPhone);
                            textViewServiceJobNotes.setText("Service Job Notes: \n" + serviceJobNotes);

                            imageButtonServiceJobAddNotes.setVisibility(View.VISIBLE);

                            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
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

                        AlertDialog.Builder fieldAlert = new AlertDialog.Builder(ServiceJobUpdate.this);
                        fieldAlert.setMessage("Service job not found").setPositiveButton("OK", dialogClickListener).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Database Error");
                }
            });
        }
        catch (Exception e){
            Toast.makeText(this,"ERROR: Unable to get service job",Toast.LENGTH_LONG).show();
        }
    }//search firebase for job

    private void addNotesAfterSearch(){


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_notes, null);

        final EditText editTextDialogNotes = mView.findViewById(R.id.editText_DialogBoxNotes);
        Button buttonDialogSubmitNotes = mView.findViewById(R.id.button_DialogBoxSubmitNotes);
        Button buttonDialogCancelNotes = mView.findViewById(R.id.button_DialogBoxCancelNotes);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        buttonDialogCancelNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        buttonDialogSubmitNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextDialogNotes.getText().toString().length() > 10){

                    try {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String username = user.getEmail();
                        String serviceJobKey = tempServiceJob.getserviceDatabaseAutomaticKey();
                        long currentDate = System.currentTimeMillis();
                        tempServiceJob.setServiceJobtimeDateBookedIn(currentDate);
                        String formattedDate = tempServiceJob.getFormattedTimestamp();
                        String newFaultNotes = editTextDialogNotes.getText().toString();
                        String oldFaultNotes = tempServiceJob.getServiceJobFaultNotes();

                        serviceJobs.child(serviceJobKey).child("serviceJobFaultNotes").setValue(oldFaultNotes + "\n\nAgent: " + username + "\n" + formattedDate + "\nNotes: " + newFaultNotes);

                        dialog.hide();
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),"ERROR: Unable to update notes",Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(), "Please add detailed notes", Toast.LENGTH_LONG).show();
                }
            }
        });//Onclick for submitting notes added

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

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeScreen.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;
    }//make menu back button work

    @Override
    public void onClick(View view) {
        if(view == imageButtonServiceJobSearch){
            if(checkFieldsAreNotEmpty()) {
                searchFirebaseServiceJob();
            }
        }
        if(view == imageButtonServiceJobAddNotes){
            addNotesAfterSearch();
        }
    }

    public boolean checkFieldsAreNotEmpty(){

        boolean areallempty = false;
        String dialogMessage = "";

        if(editTextServiceJobNumber.getText().toString().length() > 0){
            areallempty = true;
        }
        else{
            dialogMessage = "Please enter a service job number to search";
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

    @Override
    protected void onResume() {
        super.onResume();
    }


}
