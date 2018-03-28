package com.jordanforsythe.repairloginsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private Button buttonLogout;
    private TextView textviewUsername;
    private Button buttonLoginRepair;
    private Button buttonCheckRepair;
    private Button buttonEngineerScreen;
    private Button buttonLogoutRepairScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, loginScreen.class));
        }

        FirebaseUser user = mAuth.getCurrentUser();

        textviewUsername = findViewById(R.id.textView_Username);
        textviewUsername.setText("Welcome " + user.getEmail());

        buttonLogout = findViewById(R.id.button_Logout);
        buttonLoginRepair = findViewById(R.id.button_RepairLoginScreen);
        buttonCheckRepair = findViewById(R.id.button_CheckRepairStatus);
        buttonEngineerScreen = findViewById(R.id.button_EngineerUpdateScreen);
        buttonLogoutRepairScreen = findViewById(R.id.button_LogoutRepairActivity);

        buttonLogout.setOnClickListener(this);
        buttonLoginRepair.setOnClickListener(this);
        buttonCheckRepair.setOnClickListener(this);
        buttonEngineerScreen.setOnClickListener(this);
        buttonLogoutRepairScreen.setOnClickListener(this);




    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogout){
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, loginScreen.class));
        }
        if (view == buttonLoginRepair){
            finish();
            startActivity(new Intent(this, LoginRepair.class));
        }

        if(view == buttonCheckRepair){
            finish();
            startActivity(new Intent(this, CheckRepairStatus.class));
        }

        if(view == buttonEngineerScreen){
            finish();
            startActivity(new Intent(this, EngineerScreen.class));
        }

        if(view == buttonLogoutRepairScreen){
            startActivity(new Intent(this, LogoutRepair.class));
        }
    }
}
