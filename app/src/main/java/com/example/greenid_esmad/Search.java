package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Search extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;
    EventsAdapter eventsAdapter;
    ArrayList<ContentSearch> contentSearch = new ArrayList<>();
    ArrayList<ContentEvents> contentEvents = new ArrayList<>();
    ImageButton btnSearch;
    ImageButton btnClear;
    ImageButton btnClearEvents;
    EditText query;
    TextView dateText;
    RelativeLayout eventsTitle;
    RelativeLayout noEventsView;
    Button btnDatePick;
    String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        query = findViewById(R.id.search_bar);
        btnSearch = findViewById(R.id.btnSearch);
        btnClear = findViewById(R.id.btnClear);
        btnClearEvents = findViewById(R.id.btnClearEvents);
        eventsTitle = findViewById(R.id.eventsTitleView);
        dateText = findViewById(R.id.dateInfo);
        btnDatePick = findViewById(R.id.btnDatePick);
        noEventsView = findViewById(R.id.noEventsView);

        btnClear.setVisibility(View.INVISIBLE);
        btnClearEvents.setVisibility(View.GONE);
        eventsTitle.setVisibility(View.VISIBLE);
        dateText.setVisibility(View.GONE);
        noEventsView.setVisibility(View.GONE);

        GetEvents();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set search Selected
        bottomNavigationView.setSelectedItemId(R.id.search);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.search:
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


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getUsers();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                query.setText("");
                btnClear.setVisibility(View.INVISIBLE);
                btnClearEvents.setVisibility(View.GONE);
                btnDatePick.setText("Filter Date");
                dateText.setVisibility(View.GONE);
                eventsTitle.setVisibility(View.VISIBLE);


                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        contentSearch.clear();
                        GetEvents();
                        RecyclerCall();
                    }
                }, 100);
            }
        });

        btnClearEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearEvents();
            }
        });

        btnDatePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();

            }
        });

    }

    private void ClearEvents() {

        contentEvents.clear();
        btnClearEvents.setVisibility(View.GONE);
        dateText.setVisibility(View.GONE);
        btnDatePick.setText("Filter Date");

        GetEvents();

    }

    private void GetEvents() {

        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .orderBy("eventDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("ACTIVITY FEED", document.getId() + " => " + document.getData());


                                String author = document.getString("author");
                                String authorId = document.getString("authorId");
                                String authorPfp = document.getString("authorPfp");
                                String date = document.getString("date");
                                String contentUrl = document.getString("contentUrl");
                                String likeVal = document.getString("likeVal");
                                String commentVal = document.getString("commentVal");
                                String location = document.getString("location");
                                String description = document.getString("description");
                                String postId = document.getId().toString();


                                contentEvents.add(new ContentEvents(authorPfp, author, contentUrl, likeVal, date, commentVal, location, description, postId, userId, authorId));


                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                        RecyclerCallEvents();
                    }
                });
    }

    private void getUsers() {

        btnClear.setVisibility(View.VISIBLE);
        eventsTitle.setVisibility(View.GONE);


        //clear user list before showing another search result
        contentSearch.clear();
        contentEvents.clear();

        //create firebase reference
        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        String queryTxt = query.getText().toString();

        Log.d("QUERY", queryTxt);

        //Get data from all users
        db.collection("users")
                .whereGreaterThanOrEqualTo("name", queryTxt)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("USERS DATA", document.getId() + " => " + document.getData());

                                String profileName = document.getString("name");
                                String pfp = document.getString("pfp");
                                String id = document.getString("id");
                                String bio = document.getString("bio");
                                String followers = document.getString("followersVal");
                                String following = document.getString("followingVal");


                                Log.d("PROFILE + PFP", profileName + " " + pfp);


                                contentSearch.add(new ContentSearch(profileName, pfp, id, bio, followers, following));


                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                        RecyclerCall();
                    }
                });
    }

    private void RecyclerCall() {

        recyclerView = findViewById(R.id.rvSearch);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(this, contentSearch);
        recyclerView.setAdapter(searchAdapter);

    }

    private void RecyclerCallEvents() {

        recyclerView = findViewById(R.id.rvSearch);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new EventsAdapter(this, contentEvents);
        recyclerView.setAdapter(eventsAdapter);

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
        date = day + "/" + monthAdded + "/" + year;
        btnDatePick.setText(date);

        FilterEvents();

    }

    private void FilterEvents() {

        contentEvents.clear();
        dateText.setVisibility(View.VISIBLE);
        btnClearEvents.setVisibility(View.VISIBLE);
        dateText.setText("Checking events for: " + date);

        GLOBALS globalUserId = (GLOBALS) getApplicationContext();
        String userId = globalUserId.getUserIdGlobal();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .whereEqualTo("eventDate", date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("ACTIVITY FEED", document.getId() + " => " + document.getData());

                                String author = document.getString("author");
                                String authorId = document.getString("authorId");
                                String authorPfp = document.getString("authorPfp");
                                String date = document.getString("date");
                                String contentUrl = document.getString("contentUrl");
                                String likeVal = document.getString("likeVal");
                                String commentVal = document.getString("commentVal");
                                String location = document.getString("location");
                                String description = document.getString("description");
                                String postId = document.getId().toString();


                                contentEvents.add(new ContentEvents(authorPfp, author, contentUrl, likeVal, date, commentVal, location, description, postId, userId, authorId));

                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                        RecyclerCallEvents();
                        /*CheckEvents();*/

                    }
                });
    }

    private void CheckEvents() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .whereEqualTo("eventDate", date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("FILTERED", document.getId() + " => " + document.getData());

                                String id = document.getString("postId");

                                Log.d("POST ID", id);

                                if (id.isEmpty()) {
                                    noEventsView.setVisibility(View.GONE);
                                } else {
                                    noEventsView.setVisibility(View.VISIBLE);
                                }

                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                        RecyclerCallEvents();

                    }
                });


    }


}