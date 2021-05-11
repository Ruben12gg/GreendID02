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
import com.google.android.gms.common.SignInButton;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    GoogleSignInClient mGoogleSignInClient;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SignInButton lgnBtn = findViewById(R.id.login_email_button);
        lgnBtn.setSize(SignInButton.SIZE_STANDARD);
       // Button lgnBtn = findViewById(R.id.login_email_button);


        // Call Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
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

    //prevent user from going back to app after logs out
    @Override
    public void onBackPressed() {
      finishAffinity();
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

                //try to get profile Data
                db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                Map<String, Object> user = new HashMap<>();

                                user.put("id", userId);


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

                                //Add userId to GLOBALS
                                GLOBALS globalUserId = (GLOBALS) getApplicationContext();
                                globalUserId.setUserIdGlobal(userId);


                                // Welcome Toast
                                String message = "Welcome " + displayName + "!";
                                Context context = getApplicationContext();
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, message, duration);
                                toast.show();


                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("NO USER DATA", "No such document");
                                Log.d("NEW USER", "Creating and sending new user data");

                                //If it's a new user, create a db document for it
                                Map<String, Object> newUserData = new HashMap<>();
                                newUserData.put("name", displayName);
                                newUserData.put("id", userId);
                                newUserData.put("followersVal", "0");
                                newUserData.put("followingVal", "0");
                                newUserData.put("impactful", "0");
                                newUserData.put("ecoIdea", "0");
                                newUserData.put("bio", "This user hasn't added a bio yet");
                                newUserData.put("pfp", pfp.toString());

                                db.collection("users").document(userId).set(newUserData);

                                // Welcome Toast
                                String message = "Welcome " + displayName + "!";
                                Context context = getApplicationContext();
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, message, duration);
                                toast.show();


                                //Add userId to GLOBALS
                                GLOBALS globalUserId = (GLOBALS) getApplicationContext();
                                globalUserId.setUserIdGlobal(userId);

                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });


                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);

                // Error Toast
                String message = "Error logging in. Please check your internet connection, restart the app and make sure you're using a valid Google account.";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();

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


}