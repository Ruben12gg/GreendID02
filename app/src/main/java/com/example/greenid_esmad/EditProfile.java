package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    ImageView pfp;
    EditText username;
    EditText bio;
    Button saveBtn;
    ImageButton returnBtn;
    ImageButton btnImg;
    Uri imageUri;
    StorageReference storageReference;
    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        pfp = findViewById(R.id.pfp);
        username = findViewById(R.id.usernameSettings);
        bio = findViewById(R.id.bioSettings);
        saveBtn = findViewById(R.id.save);
        returnBtn = findViewById(R.id.backBtn);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        //Access user Id from GLOBALS
        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Get profile Data
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String pfpUrl = document.getString("pfp");
                        Picasso.get().load(pfpUrl).into(pfp);

                        String usernameTxt = document.getString("name");
                        username.setText(usernameTxt);

                        String bioTxt = document.getString("bio");
                        bio.setText(bioTxt);


                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        btnImg = findViewById(R.id.btnImg);
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        //Save new data on button click
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newUsername = username.getText().toString();
                String newBio = bio.getText().toString();


                Map<String, Object> data = new HashMap<>();
                data.put("name", newUsername);
                data.put("bio", newBio);


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(userId).update(data);

                // Success Toast
                String message = "Your profile was updated!";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();

                uploadPicture();
                /* finish();*/

            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    //Function to choose pic
    private void choosePicture() {

        //Launch intent with the gallery viewer
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    //When image is selected from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the file exists and is successfully 'obtained'
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Log.d("URI", imageUri.toString());

            pfp.setImageURI(imageUri);


        }
    }

    private void uploadPicture() {

        //Access user Id from GLOBALS
        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();

        //create and show a progress upload bar
        final ProgressDialog progDiag = new ProgressDialog(this);
        progDiag.setTitle("Uploading Image...");
        progDiag.show();

        //Generate a random name for the file
        final String randomKey = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("images/" + userId + "/" + randomKey);


        //prevent app from crashing if user doesn't select any image to be updated
        if (imageUri == null) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            String pfpUrl = document.getString("pfp");

                            Map<String, Object> data = new HashMap<>();
                            data.put("pfp", pfpUrl);

                            db.collection("users").document(userId).update(data);


                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });

        } else {

            //upload the file to the database and generate an usable url to display the picture
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get an URL to the uploaded content

                            //Generate img link
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String downloadUrl3 = uri.toString();

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("pfp", downloadUrl3);

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("users").document(userId).update(data);

                                }
                            });

                            progDiag.dismiss();
                            Snackbar.make(findViewById(android.R.id.content), "Image Uploaded!", Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            progDiag.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed to Upload File", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                //Progress bar percentage calculation
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercentage = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());

                    progDiag.setMessage("Loading " + (int) progressPercentage + "%");
                }
            });

        }

        finish();

    }
}