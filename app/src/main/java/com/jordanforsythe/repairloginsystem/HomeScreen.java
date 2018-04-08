package com.jordanforsythe.repairloginsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private Button buttonLogout;
    private TextView textviewUsername;
    private ImageButton imageButtonLoginRepair;
    private ImageButton imageButtonCheckRepair;
    private ImageButton imageButtonEngineerScreen;
    private ImageButton imageButtonLogoutRepairScreen;
    private ImageButton imageButtonServiceJobLogin;
    private ImageButton imageButtonServiceJobUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }

        FirebaseUser user = mAuth.getCurrentUser();

        textviewUsername = findViewById(R.id.textView_Username);
        textviewUsername.setText("Welcome " + user.getEmail());

        buttonLogout = findViewById(R.id.button_Logout);
        imageButtonLoginRepair = findViewById(R.id.imageButton_RepairLoginScreen);
        imageButtonCheckRepair = findViewById(R.id.imageButton_CheckRepairStatus);
        imageButtonEngineerScreen = findViewById(R.id.imageButton_EngineerUpdateScreen);
        imageButtonLogoutRepairScreen = findViewById(R.id.imageButton_LogoutRepairActivity);
        imageButtonServiceJobLogin = findViewById(R.id.imageButton_ServiceJobLogin);
        imageButtonServiceJobUpdate = findViewById(R.id.imageButton_ServiceJobUpdate);

        buttonLogout.setOnClickListener(this);
        imageButtonLoginRepair.setOnClickListener(this);
        imageButtonCheckRepair.setOnClickListener(this);
        imageButtonEngineerScreen.setOnClickListener(this);
        imageButtonLogoutRepairScreen.setOnClickListener(this);
        imageButtonServiceJobLogin.setOnClickListener(this);
        imageButtonServiceJobUpdate.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogout){
            mAuth.signOut();
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        }
        if (view == imageButtonLoginRepair){
            startActivity(new Intent(this, LoginRepair.class));
        }

        if(view == imageButtonCheckRepair){
            startActivity(new Intent(this, CheckRepairStatus.class));
        }

        if(view == imageButtonEngineerScreen){
            startActivity(new Intent(this, EngineerScreen.class));
        }

        if(view == imageButtonLogoutRepairScreen){
            startActivity(new Intent(this, LogoutRepair.class));
        }
        if(view == imageButtonServiceJobLogin){
            startActivity(new Intent(this, ServiceJobLogin.class));
        }
        if(view == imageButtonServiceJobUpdate){
            startActivity(new Intent(this, ServiceJobUpdate.class));
        }
    }//onclick

    @Override
    public void onBackPressed(){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int option) {
                switch (option){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), LoginScreen.class));
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
