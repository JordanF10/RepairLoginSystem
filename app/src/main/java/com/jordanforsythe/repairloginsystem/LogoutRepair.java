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

            getRepairJob();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 10 seconds
                    changeRepairToLoggedOut();
                }
            }, 1000);
        }
    }

    private void changeRepairToLoggedOut(){

        String repairJobKey = tempRepair.getDatabaseAutomaticKey();
        String repairStatusBefore = tempRepair.getRepairStatus();

        if(repairStatusBefore.equals("Logged Out")) {
            System.out.println("Repair already logged out");
        }
        else{
            if(repairStatusBefore.equals("Canceled")) {
                System.out.println("Repair Canceled, cannot logout");
            }
            else{
                repairs.child(repairJobKey).child("repairStatus").setValue("Logged Out");
            }
        }


    }

    private void getRepairJob(){

        Log.d("OHBABY", "It got to repairjobnumber");

        int jobNumberTyped = Integer.parseInt(editTextLogoutRepairNumber.getText().toString());

        Query firebaseDatabaseQuery = repairs.orderByChild("jobNumber").equalTo(jobNumberTyped);

        firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot repairsnapshot: dataSnapshot.getChildren()) {

                    tempRepair.setDatabaseAutomaticKey(repairsnapshot.getKey());
                    tempRepair.setRepairStatus(repairsnapshot.child("repairStatus").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }//get repair job number

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeScreen.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;
    }


}
