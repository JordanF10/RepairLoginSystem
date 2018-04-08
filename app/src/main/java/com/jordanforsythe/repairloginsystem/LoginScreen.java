package com.jordanforsythe.repairloginsystem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private ImageButton buttonLogin;
    private EditText editTextUsername;
    private EditText editTextPassword;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
// ...
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            //take user to home screen if already loged in
            startActivity(new Intent(getApplicationContext(), HomeScreen.class));
            finish();
        }

        buttonLogin = findViewById(R.id.imageButton_login);
        editTextUsername = findViewById(R.id.editText_Username);
        editTextPassword = findViewById(R.id.editText_Password);

        buttonLogin.setOnClickListener(this);
    }

    private void userLogin(){

        String email = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
        Toast.makeText(this, "Please enter your username", Toast.LENGTH_LONG).show();
        return;
        }//email empty

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_LONG).show();
            return;
        }//email empty

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //progress to the home screen
                            startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                            finish();
                            return;
                        }

                        else{
                            Toast.makeText(getApplicationContext(), "Username or password incorrect", Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }


    @Override
    public void onClick(View view) {
        if(view == buttonLogin){
            userLogin();
        }
    }
}
