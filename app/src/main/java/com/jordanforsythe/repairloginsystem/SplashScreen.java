package com.jordanforsythe.repairloginsystem;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {
    //creating a variable to hold the screen time out
    private static int splashTimeOut = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        int colour = getResources().getColor(R.color.colorPrimary);
        getWindow().getDecorView().setBackgroundColor(colour);

        //moving to the next activity after specified time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginScreen.class));
                finish();
            }
        },splashTimeOut);
    }//oncreate 
}//class
