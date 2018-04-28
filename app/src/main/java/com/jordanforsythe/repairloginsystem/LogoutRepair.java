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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jordanforsythe.repairloginsystem.Repair.Repair;

public class LogoutRepair extends AppCompatActivity implements View.OnClickListener{

    //initialising the firebase auth and database references
    public static final String REPAIR_FIREBASE_KEY = "repairs";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference repairs = firebaseDatabase.getReference(REPAIR_FIREBASE_KEY);
    FirebaseAuth mAuth;
    //initialising the visual elements
    private ImageButton imageButtonLogoutRepair;
    private EditText editTextLogoutRepairNumber;
    private Repair tempRepair = new Repair();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_repair);
        //seting the action bar text and back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Logout Repair");

        mAuth = FirebaseAuth.getInstance();

        //if there is no current user logged in then return to login screen
        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }

        //setting the visual elements to the layout ID
        imageButtonLogoutRepair = findViewById(R.id.imageButton_LogoutRepair);
        editTextLogoutRepairNumber = findViewById(R.id.editText_LogoutRepairNumber);

        imageButtonLogoutRepair.setOnClickListener(this);
    }//oncreate

    //onclick to run if button is clicked
    @Override
    public void onClick(View view) {
        if(view == imageButtonLogoutRepair){
            //check if fields are not empty
            if(checkFieldsAreNotEmpty()) {
                getRepairJob();
            }
        }
    }//onclick

    //method to get the repair job typed in
    private void getRepairJob(){

        //change typed number to integer
        int jobNumberTyped = Integer.parseInt(editTextLogoutRepairNumber.getText().toString());

        //query to search the databasd for the job number
        Query firebaseDatabaseQuery = repairs.orderByChild("jobNumber").equalTo(jobNumberTyped);
        try {
            //running the query
            firebaseDatabaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //if the job exsists then take the repair key of it
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot repairsnapshot : dataSnapshot.getChildren()) {

                            tempRepair.setDatabaseAutomaticKey(repairsnapshot.getKey());
                            tempRepair.setRepairStatus(repairsnapshot.child("repairStatus").getValue().toString());
                        }

                        //after 1 second try to change the status to logged out
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                //after 1 second
                                changeRepairToLoggedOut();
                            }
                        }, 1000);
                    }
                    //if the repair does not exsist show dialog telling the user it does not exsist
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
                        AlertDialog.Builder fieldAlert = new AlertDialog.Builder(LogoutRepair.this);
                        fieldAlert.setMessage("Repair job not found").setPositiveButton("OK", dialogClickListener).show();
                    }
                }//query

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }//try
        catch (Exception e){
            Toast.makeText(this,"ERROR: Unable to get repair job",Toast.LENGTH_LONG).show();
        }//catch
    }//get repair job number

    //method to change the repair to logged out
    private void changeRepairToLoggedOut(){
        try {

            //setting the repair job key amd status to the temp repair key and status
            String repairJobKey = tempRepair.getDatabaseAutomaticKey();
            String repairStatusBefore = tempRepair.getRepairStatus();

            //if the repaur us already loged out then show dialog box telling user
            if (repairStatusBefore.equals("Logged Out")) {

                String dialogTextAlreadyLoggedOut = "Cannot logout, repair already logged out";
                alreadyLoggedOut(dialogTextAlreadyLoggedOut);

            } else {
                //if the repair is canceled then show dialog box telling the user
                if (repairStatusBefore.equals("Canceled")) {

                    String dialogTextCanceled = "Repair canceled, cannot logout.";
                    alreadyLoggedOut(dialogTextCanceled);

                } else {
                    //if the repaur is not canceled or logged out then update the repair status to logged out
                    repairs.child(repairJobKey).child("repairStatus").setValue("Logged Out");

                    //show a dialog box telling the user the job is now logged out
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_repair_logged_out, null);
                    String textForDialog = "Job logged out";

                    TextView textViewDialogText = mView.findViewById(R.id.textView_DialogRepairLoggedOut);
                    ImageButton imageButtonDialogText = mView.findViewById(R.id.imageButton_DialogRepairLoggedOut);

                    textViewDialogText.setText(textForDialog);

                    mBuilder.setView(mView);
                    final AlertDialog dialogLoggedOut = mBuilder.create();
                    dialogLoggedOut.show();

                    imageButtonDialogText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogLoggedOut.dismiss();
                            startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                            finish();
                        }
                    });
                }//else
            }//else
        }//try
        catch (Exception e){
            Toast.makeText(this,"ERROR: Unable to change repair status",Toast.LENGTH_LONG).show();
        }//catch
    }//change to logged out

    //method to override the back button pressed and show dialog box confirming with user
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

    //method to check the fields are not empty
    public boolean checkFieldsAreNotEmpty(){

        boolean areallempty = false;
        String dialogMessage = "";

        //check if edit text is not empty
        if(editTextLogoutRepairNumber.getText().toString().length() > 0){
            areallempty = true;
        }
        else{
            dialogMessage = "Please enter a Repair Number to logout";
        }

        //if still false then show dialog box telling user fields are empty
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

    //method to show the alert dialog custom layout if the repair cannot be logged out
    private void alreadyLoggedOut(String dialogText){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_repair_logged_out, null);

        TextView textViewDialogText = mView.findViewById(R.id.textView_DialogRepairLoggedOut);
        ImageButton imageButtonDialogText = mView.findViewById(R.id.imageButton_DialogRepairLoggedOut);

        textViewDialogText.setText(dialogText);

        mBuilder.setView(mView);
        final AlertDialog dialogAlreadyCanceled = mBuilder.create();
        dialogAlreadyCanceled.show();

        imageButtonDialogText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlreadyCanceled.dismiss();
            }
        });
    }//already logged out

    //method to make the action bar back button work
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

    //method to override the activity being restarted and log the user out
    @Override
    protected void onRestart() {
        super.onRestart();
        mAuth.signOut();
        startActivity(new Intent(this, LoginScreen.class));
        finish();
    }
}//logoutRepair
