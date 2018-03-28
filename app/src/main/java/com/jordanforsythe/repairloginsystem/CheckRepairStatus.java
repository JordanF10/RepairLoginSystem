package com.jordanforsythe.repairloginsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_repair_status);

        editTextRepairQuery = findViewById(R.id.editText_RepairQuery);
        buttonRepairQuery = findViewById(R.id.button_RepairQuery);

        buttonRepairQuery.setOnClickListener(this);
    }

    private void searchFirebase() {

        int jobNumberTyped = Integer.parseInt(editTextRepairQuery.getText().toString());

        Query firebaseDatabaseQuery = repairs.orderByChild("jobNumber").equalTo(jobNumberTyped);

        firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot repairsnapshot: dataSnapshot.getChildren()) {
                    String name = (String) repairsnapshot.child("customerName").getValue();
                    String fault = (String) repairsnapshot.child("faultDescription").getValue();
                    String imei = (String) repairsnapshot.child("imeiNumber").getValue();
                    String jobNumber = (String) repairsnapshot.child("jobNumber").getValue().toString();
                    long unformattedDate = (long) repairsnapshot.child("timeDateBookedIn").getValue();
                    tempRepair.setTimeDateBookedIn(unformattedDate);
                    System.out.println("NAME: " + name + ". FAULT FOUND: " + fault + ". IMEI NUMBER: " + imei + " JOB NUMBER: " + jobNumber + " Date Booked in:" + tempRepair.getFormattedTimestamp());
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

}
