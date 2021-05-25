package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class NewPost extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ImageView contentPic;
    Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    EditText description;
    EditText location;
    TextView dateTv;
    Button btnDatePick;
    TextView timeTv;
    Button btnTimePick;
    Button postBtn;
    ImageButton btnImageTag;
    ImageButton btnEventTag;
    ImageButton btnProductTag;
    ImageView addIcon;
    ImageView notificationDot;


    String pickedTime;
    String pickedDate;

    int hour;
    int minute;

    String postType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        notificationDot = findViewById(R.id.notificationDot);
        notificationDot.setVisibility(View.GONE);

        addIcon = findViewById(R.id.addIcon);
        addIcon.setVisibility(View.VISIBLE);

        btnImageTag = findViewById(R.id.btnImageTag);
        btnProductTag = findViewById(R.id.btnProductTag);

        dateTv = findViewById(R.id.eventDateTv);
        btnDatePick = findViewById(R.id.btnDatePick);
        timeTv = findViewById(R.id.eventTimeTv);
        btnTimePick = findViewById(R.id.btnTimePick);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        contentPic = findViewById(R.id.imageContent);

        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        //Click on the imageView
        contentPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        btnEventTag = findViewById(R.id.btnEventTag);
        btnEventTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnImageTag.setBackgroundResource(R.drawable.postd);
                btnEventTag.setBackgroundResource(R.drawable.events);
                btnProductTag.setBackgroundResource(R.drawable.productd);

                dateTv.setVisibility(View.VISIBLE);
                btnDatePick.setVisibility(View.VISIBLE);
                timeTv.setVisibility(View.VISIBLE);
                btnTimePick.setVisibility(View.VISIBLE);

                postType = "event";


            }
        });

        btnImageTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnImageTag.setBackgroundResource(R.drawable.posts);
                btnEventTag.setBackgroundResource(R.drawable.eventd);
                btnProductTag.setBackgroundResource(R.drawable.productd);


                dateTv.setVisibility(View.GONE);
                btnDatePick.setVisibility(View.GONE);
                timeTv.setVisibility(View.GONE);
                btnTimePick.setVisibility(View.GONE);

                postType = "image";


            }
        });

        btnProductTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnImageTag.setBackgroundResource(R.drawable.postd);
                btnEventTag.setBackgroundResource(R.drawable.eventd);
                btnProductTag.setBackgroundResource(R.drawable.products);


                dateTv.setVisibility(View.GONE);
                btnDatePick.setVisibility(View.GONE);
                timeTv.setVisibility(View.GONE);
                btnTimePick.setVisibility(View.GONE);

                postType = "product";


            }
        });


        btnDatePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();

            }
        });

        btnTimePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(view);
            }
        });


        postBtn = findViewById(R.id.postBtn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pickedTime = btnTimePick.getText().toString();
                pickedDate = btnDatePick.getText().toString();

                //Verify if all the required content for each type of post is present
                if (postType == null) {
                    String message = "Please, select the type of post that you want to do.";
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, message, duration);
                    toast.show();

                } else if (postType.equals("event")) {
                    if (pickedTime.equals("Pick time") || pickedDate.equals("Pick date")) {

                        String message = "Don't forget to setup a date and time for your event.";
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, message, duration);
                        toast.show();

                    } else {
                        uploadPicture();
                    }
                } else if (postType.equals("image") || postType.equals("product")) {
                    uploadPicture();

                }

            }
        });

        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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


        //prevent user from making a post without a picture
        if (imageUri == null) {

            progDiag.dismiss();

            Log.d("POSTTYPE", postType);
            String message = "You need to select a picture to post.";
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, message, duration);
            toast.show();

            return;

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

                                                    String postId = UUID.randomUUID().toString();

                                                    if (postType.equals("event")) {

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
                                                        data.put("postType", postType);
                                                        data.put("eventDate", pickedDate);
                                                        data.put("eventTime", pickedTime);
                                                        data.put("impactful", "0");
                                                        data.put("ecoIdea", "0");
                                                        data.put("postId", postId);

                                                        db.collection("users").document(userId).collection("posts").document(postId).set(data);
                                                        db.collection("events").document(postId).set(data);


                                                    } else {

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
                                                        data.put("postType", postType);
                                                        data.put("impactful", "0");
                                                        data.put("ecoIdea", "0");
                                                        data.put("postId", postId);

                                                        db.collection("users").document(userId).collection("posts").document(postId).set(data);


                                                    }


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

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();


    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        Integer monthAdded = month + 1;
        String date = day + "/" + monthAdded + "/" + year;
        btnDatePick.setText(date);

    }

    public void showTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                int hour = selectedHour;
                int minute = selectedMinute;
                btnTimePick.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));

            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                onTimeSetListener,
                hour,
                minute,
                true
        );

        timePickerDialog.show();

    }

}