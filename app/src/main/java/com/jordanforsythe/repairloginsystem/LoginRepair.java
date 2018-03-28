package com.jordanforsythe.repairloginsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.jordanforsythe.repairloginsystem.R;
import com.jordanforsythe.repairloginsystem.Repair.Repair;

public class LoginRepair extends AppCompatActivity implements View.OnClickListener, ChildEventListener {

    public static final String REPAIR_FIREBASE_KEY = "repairs";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference repairs = firebaseDatabase.getReference(REPAIR_FIREBASE_KEY);

    private Button buttonSendRepairData;
    private EditText editTextCustomerName;
    private EditText editTextImeiNumber;
    private EditText editTextFaultDescription;
    private EditText editTextCustomerPhone;
    private EditText editTextCustomerEmail;
    private EditText editTextStandbyPhoneImei;
    private int nextRepairJobNumber;
    private int jobNumber;
    private String repairStatusLoggedIn = "Logged In";
    private Repair tempRepair = new Repair();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_repair);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonSendRepairData = findViewById(R.id.button_SendRepairLogin);
        editTextCustomerName = findViewById(R.id.editText_CustomerName);
        editTextImeiNumber = findViewById(R.id.editText_ImeiNumber);
        editTextFaultDescription = findViewById(R.id.editText_FaultDescription);
        editTextCustomerEmail = findViewById(R.id.editText_CustomerEmailAddress);
        editTextCustomerPhone = findViewById(R.id.editText_CustomerPhoneNumber);
        editTextStandbyPhoneImei = findViewById(R.id.editText_standbyPhoneImei);

        System.out.println(String.valueOf(jobNumber) + "BeforeBUttonCLock");

        queryNextJobNo();

        buttonSendRepairData.setOnClickListener(this);

        repairs.addChildEventListener(this);

    }

    private void pushRepairToFirebase(){

            jobNumber = tempRepair.getJobNumber();

            String customerName = editTextCustomerName.getText().toString();
            String customerPhone = editTextCustomerPhone.getText().toString();
            String customerEmail = editTextCustomerEmail.getText().toString();
            String imeiNumber = editTextImeiNumber.getText().toString();
            String faultDescription = editTextFaultDescription.getText().toString();
            long currentDate = System.currentTimeMillis();
            String standbyPhoneIMEI;
            jobNumber++;


            if(editTextStandbyPhoneImei != null) {
                standbyPhoneIMEI = editTextStandbyPhoneImei.getText().toString();
            }
            else{
                standbyPhoneIMEI = "No standby given";
            }

            Repair repairToSend = new Repair(customerName, customerPhone, customerEmail, imeiNumber, faultDescription, jobNumber,
                    repairStatusLoggedIn, currentDate, standbyPhoneIMEI);

            System.out.println("REPAIR SENT, Job number was " + String.valueOf(jobNumber) + ", success!" + customerEmail + customerPhone);

            repairs.push().setValue(repairToSend);
        }//Push Repair to Firebase method

    @Override
    public void onClick(View view) {
        if(view == buttonSendRepairData){
            pushRepairToFirebase();
        }
    }//On click method

    private void queryNextJobNo(){

        Query firebaseDatabaseQuery = repairs.orderByChild("jobNumber").limitToLast(1);

        firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot repairsnapshot: dataSnapshot.getChildren()) {

                        String jobNumber = (String) repairsnapshot.child("jobNumber").getValue().toString();
                        nextRepairJobNumber = (Integer.parseInt(jobNumber));
                        tempRepair.setJobNumber(nextRepairJobNumber);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });//Query for the job
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


    }//on back pressed\

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeScreen.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;
    }
}
