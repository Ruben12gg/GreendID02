package com.example.greenid_esmad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Timer;
import java.util.TimerTask;

public class Login extends AppCompatActivity {

    Timer timerToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button lgnBtn = findViewById(R.id.login_email_button);

        lgnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent logIntent = new Intent( getApplicationContext(), LoginCredentialsActivity.class);
                startActivity(logIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();


            }
        });
        

    }


}