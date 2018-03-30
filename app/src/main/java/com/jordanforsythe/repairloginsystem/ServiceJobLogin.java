package com.jordanforsythe.repairloginsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jordanforsythe.repairloginsystem.ServiceJob.ServiceJob;

import java.sql.BatchUpdateException;

public class ServiceJobLogin extends AppCompatActivity implements View.OnClickListener, ChildEventListener {

    public static final String REPAIR_FIREBASE_KEY_SERVICE = "serviceJobs";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference serviceJobs = firebaseDatabase.getReference(REPAIR_FIREBASE_KEY_SERVICE);

    private EditText editTextServiceJobCustomerName;
    private EditText editTextServiceJobCustomerPhone;
    private EditText editTextServiceJobFaultNotes;
    private Button buttonServiceJobCreate;
    private int nextServiceJobNumber;
    private ServiceJob tempServiceJob = new ServiceJob();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_job_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextServiceJobCustomerName = findViewById(R.id.editText_ServiceJobCustomerName);
        editTextServiceJobCustomerPhone = findViewById(R.id.editText_ServiceJobCustomerPhone);
        editTextServiceJobFaultNotes = findViewById(R.id.editText_ServiceJobFault);
        buttonServiceJobCreate = findViewById(R.id.button_ServiceJobCreate);

        queryNextJobNo();

        buttonServiceJobCreate.setOnClickListener(this);

        serviceJobs.addChildEventListener(this);

    }

    private void pushServiceJobToFirebase(){

        int serviceJobNumber = tempServiceJob.getServiceJobNumber();

        String customerName = editTextServiceJobCustomerName.getText().toString();
        String customerPhone = editTextServiceJobCustomerPhone.getText().toString();
        String serviceFault = editTextServiceJobFaultNotes.getText().toString();
        long currentDate = System.currentTimeMillis();
        serviceJobNumber++;

        ServiceJob serviceJobToSend = new ServiceJob(customerName, customerPhone, serviceFault, serviceJobNumber, currentDate);

        serviceJobs.push().setValue(serviceJobToSend);
    }

    private void queryNextJobNo(){

        Query firebaseDatabaseQuery = serviceJobs.orderByChild("serviceJobNumber").limitToLast(1);

        firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot servicesnapshot: dataSnapshot.getChildren()) {

                    String serviceJobNumber = (String) servicesnapshot.child("serviceJobNumber").getValue().toString();
                    nextServiceJobNumber = (Integer.parseInt(serviceJobNumber));
                    tempServiceJob.setServiceJobNumber(nextServiceJobNumber);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });//Query for the job
    }//Query next job number method


    @Override
    public void onClick(View view) {
        if(view == buttonServiceJobCreate){
            pushServiceJobToFirebase();
        }
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
}
