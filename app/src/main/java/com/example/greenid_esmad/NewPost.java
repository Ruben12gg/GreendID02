package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewPost extends AppCompatActivity {

    ImageView contentPic;
    Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    EditText description;
    EditText location;
    Button postBtn;
    ImageView addIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        addIcon = findViewById(R.id.addIcon);
        addIcon.setVisibility(View.VISIBLE);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        contentPic = findViewById(R.id.imageContent);

        //Click on the imageView
        contentPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        postBtn = findViewById(R.id.postBtn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPicture();
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Post Selected
        bottomNavigationView.setSelectedItemId(R.id.new_post);

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

            contentPic.setImageURI(imageUri);
            addIcon.setVisibility(View.INVISIBLE);

        }
    }

    //Upload the previously selected pic to database
    private void uploadPicture() {

        //create and show a progress upload bar
        final ProgressDialog progDiag = new ProgressDialog(this);
        progDiag.setTitle("Uploading Image...");
        progDiag.show();

        //Generate a random name for the file
        final String randomKey = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("images/" + randomKey);

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


                                //Access user Id from GLOBALS
                                GLOBALS globalUserId = (GLOBALS) getApplicationContext();
                                String userId = globalUserId.getUserIdGlobal();

                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {

                                                String name = document.getString("name");
                                                String authorId = document.getString("id");
                                                String pfpUrl = document.getString("pfp");

                                                location = findViewById(R.id.locationTxt);
                                                description = findViewById(R.id.descriptionTxt);

                                                String locationTxt = location.getText().toString();
                                                String descriptionTxt = description.getText().toString();
                                                String likeVal = "0";
                                                String commentVal = "0";

                                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                                                Date date = new Date();
                                                String dateTxt = formatter.format(date).toString();


                                                String postDate = new Date().toString();

                                                String downloadUrl3 = uri.toString();
                                                Log.d("IMAGE POST LINK", downloadUrl3);

                                                Map<String, Object> data = new HashMap<>();
                                                data.put("author", name);
                                                data.put("authorId", authorId);
                                                data.put("authorPfp", pfpUrl);
                                                data.put("location", locationTxt);
                                                data.put("description", descriptionTxt);
                                                data.put("contentUrl", downloadUrl3);
                                                data.put("likeVal", likeVal);
                                                data.put("commentVal", commentVal);
                                                data.put("date", dateTxt);


                                                db.collection("posts").add(data);
                                                db.collection("users").document(userId).collection("posts").add(data);



                                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                            } else {
                                                Log.d("TAG", "No such document");
                                            }
                                        } else {
                                            Log.d("TAG", "get failed with ", task.getException());
                                        }
                                    }
                                });



                            }
                        });

                        progDiag.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded!", Snackbar.LENGTH_LONG).show();
                        callHome();
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

    private void callHome() {
        final Handler handler = new Handler(Looper.getMainLooper());
        //call home activity after 500ms
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), Home.class));
                finish();
            }
        }, 500);

    }
}