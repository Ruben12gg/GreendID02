package com.example.greenid_esmad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginCredentialsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_credentials);

        Button loginBtn = findViewById(R.id.btnLogin);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText email = findViewById(R.id.emailTxt);
                String emailTxt = email.getText().toString();

                EditText password = findViewById(R.id.passTxt);
                String passwordTxt = password.getText().toString();

                if (emailTxt.equals("Admin") && passwordTxt.equals("Admin")) {

                    String message = "Successfully Logged in!";
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, message, duration);
                    toast.show();

                    Intent homeIntent = new Intent(getApplicationContext(), Home.class);
                    startActivity(homeIntent);

                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();


                } else {

                    String message = "Wrong Credentials!";
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, message, duration);
                    toast.show();

                }

            }
        });


    }
}