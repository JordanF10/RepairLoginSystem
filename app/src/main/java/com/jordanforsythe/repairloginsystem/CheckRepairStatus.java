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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jordanforsythe.repairloginsystem.Repair.Repair;

public class CheckRepairStatus extends AppCompatActivity implements View.OnClickListener {

    public static final String REPAIR_FIREBASE_KEY = "repairs";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference repairs = firebaseDatabase.getReference(REPAIR_FIREBASE_KEY);


    private EditText editTextRepairQuery;
    private Button buttonRepairQuery;
    private Repair tempRepair = new Repair();
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_repair_status);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextRepairQuery = findViewById(R.id.editText_RepairQuery);
        buttonRepairQuery = findViewById(R.id.button_RepairQuery);
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


        buttonRepairQuery.setOnClickListener(this);
    }

    private void searchFirebase() {

        int jobNumberTyped = Integer.parseInt(editTextRepairQuery.getText().toString());

        Query firebaseDatabaseQuery = repairs.orderByChild("jobNumber").equalTo(jobNumberTyped);

        firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot repairsnapshot: dataSnapshot.getChildren()) {
                    String jobNumber = (String) repairsnapshot.child("jobNumber").getValue().toString();
                    String status = (String) repairsnapshot.child("repairStatus").getValue().toString();
                    String date = (String) repairsnapshot.child("formattedTimestamp").getValue().toString();
                    String name = (String) repairsnapshot.child("customerName").getValue().toString();
                    String phone = (String) repairsnapshot.child("customerPhoneNumber").getValue().toString();
                    String email = (String) repairsnapshot.child("customerEmailAddress").getValue().toString();
                    String imei = (String) repairsnapshot.child("imeiNumber").getValue().toString();
                    String fault = (String) repairsnapshot.child("faultDescription").getValue().toString();
                    String standbyImei = (String) repairsnapshot.child("standbyPhoneIMEI").getValue().toString();
                    String engineerNotes = (String) repairsnapshot.child("engineerNotes").getValue().toString();

                    textViewJobNumberReturned.setText("Job Number: \n"+jobNumber);
                    textViewJobStatusReturned.setText("Repair Status: \n"+status);
                    textViewDateBookedInReturned.setText("Created on: \n"+date);
                    textViewCustomerNameReturned.setText("Customer Name: \n"+name);
                    textViewCustomerPhoneReturned.setText("Customer Phone: \n"+phone);
                    textViewCustomerEmailReturned.setText("Customer Email: \n"+email);
                    textViewCustomerImeiReturned.setText("Handset IMEI: \n"+imei);
                    textViewCustomerFaultReturned.setText("Reported Fault: \n"+fault);
                    textViewCustomerStandbyImeiReturned.setText("Standby IMEI: \n"+standbyImei);

                    if(engineerNotes == null){
                        return;
                    }
                    else{
                        textViewEngineerNotesReturned.setText("Engineer Notes: \n" + engineerNotes);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database Error");
            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view == buttonRepairQuery) {
            searchFirebase();
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


    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeScreen.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;
    }

}
