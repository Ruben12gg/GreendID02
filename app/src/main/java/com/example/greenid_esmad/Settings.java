package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;

public class Settings extends AppCompatActivity {

    ImageButton backBtn;
    Button editBtn;
    Button logoutBtn;
    Button delBtn;
    Button clearBtn;
    ImageView notificationDot;

    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        notificationDot = findViewById(R.id.notificationDot);
        notificationDot.setVisibility(View.GONE);

        editBtn = findViewById(R.id.btn2);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), EditProfile.class));

            }
        });

        logoutBtn = findViewById(R.id.btn3);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LOGOUT", "Logout Btn");
                // get or create SharedPreferences
                sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE);
                // save your string in SharedPreferences
                sharedPreferences.edit().putString("userId", "").commit();
                logout();
            }
        });

        delBtn = findViewById(R.id.btn1);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModalDelete modal = new ModalDelete();
                modal.show(getSupportFragmentManager(), "Dialog");
            }
        });

        clearBtn = findViewById(R.id.btn0);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ModalClearData modal = new ModalClearData();
                modal.show(getSupportFragmentManager(), "Dialog");
            }
        });

        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Listen for new notifications
        db.collection("users").document(userId).collection("notifications")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@com.google.firebase.database.annotations.Nullable QuerySnapshot snapshots,
                                        @com.google.firebase.database.annotations.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("ERROR", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d("NOTIFY", "New notification: " + dc.getDocument().getData());

                                    notificationDot.setVisibility(View.VISIBLE);

                                    break;
                                case MODIFIED:
                                    Log.d("MODIFY", "Modified" + dc.getDocument().getData());

                                    break;
                                case REMOVED:
                                    Log.d("REMOVE", "Removed" + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set User Selected
        bottomNavigationView.setSelectedItemId(R.id.user);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(), Search.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.new_post:
                        startActivity(new Intent(getApplicationContext(), NewPost.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.user:
                        startActivity(new Intent(getApplicationContext(), User.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.notifications:
                        startActivity(new Intent(getApplicationContext(), Notifications.class));
                        overridePendingTransition(0, 0);
                        return true;


                }

                return false;
            }

        });

        backBtn = findViewById(R.id.backBtn);

        //navegação para o user
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                startActivity(new Intent(getApplicationContext(), User.class));
            }
        });
        

    }


    private void logout() {
        GoogleSignInClient mGoogleSignInClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(Settings.this,
                new OnCompleteListener<Void>() {  //signout Google
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut(); //signout firebase
                        Intent setupIntent = new Intent(getBaseContext(), Login.class);
                        Toast.makeText(getBaseContext(), "User logged out", Toast.LENGTH_LONG).show();
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                        finish();
                    }
                });
    }

}