package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Login extends AppCompatActivity {

    Timer timerToLogin;

    private static final int RC_SIGN_IN = 9001;
    GoogleSignInClient mGoogleSignInClient;

    boolean loginStatus = false;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button lgnBtn = findViewById(R.id.login_email_button);


        // Iniciar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("846490331619-irg35pankf3m01bmohmg2t524nurqs65.apps.googleusercontent.com")
                .requestEmail()
                .build();


        lgnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

            }
        });


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }


    /*@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_email_button:
                signIn();
                break;

        }

    }*/

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.d("log", "Entrada em onactivity");

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());

                Uri pfp = account.getPhotoUrl();
                Log.d("PFP", pfp.toString());

                String displayName = account.getDisplayName();
                Log.d("Display Name", displayName);

                String userId = account.getId();
                Log.d("IDDDDDDDDDDD", "O id é " + userId);
                Intent intent = new Intent(this, Home.class);
                intent.putExtra("userId", userId);


                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> user = new HashMap<>();
                user.put("name", displayName);
                Log.d("NAME", displayName);
                user.put("id", userId);
                user.put("pfp", pfp.toString());


                db.collection("users").document(userId)
                        .update(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error writing document", e);
                            }
                        });


                //Adição do valor do userId para a Variavel global
                /*GlobalUserId globalUserId = (GlobalUserId) getApplicationContext();
                globalUserId.setUserIdGlobal(userId);
                globalUserId.setUserName(displayName);
                globalUserId.setUserPfp(pfp.toString());*/


                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            updateUI(null);
                            //Toast
                            String message = "There was an error signin in Error:" + task.getException();
                            Context context = getApplicationContext();
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, message, duration);
                            toast.show();
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            setContentView(R.layout.activity_home);
            startHomeAct();
            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

            setContentView(R.layout.activity_login);

        }
    }

    //mudança de UI
    private void updateUI(FirebaseUser user) {

        if (user != null) {

            //call home fragment and its activity
            startHomeAct();


        } else {

            setContentView(R.layout.activity_main);
        }
    }

    public void startHomeAct() {
        Intent intent = new Intent(this, Home.class);

        startActivity(intent);

    }

        /*lgnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent logIntent = new Intent( getApplicationContext(), LoginCredentialsActivity.class);
                startActivity(logIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();


            }
        });
        

    }*/


}