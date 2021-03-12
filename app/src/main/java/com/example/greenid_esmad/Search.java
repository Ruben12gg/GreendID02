package com.example.greenid_esmad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.ListFragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    // Initialize variable
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.search);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch(menuItem.getItemId()){

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.search:
                        return true;

                    case R.id.new_post:
                        startActivity(new Intent(getApplicationContext(), NewPost.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.user:
                        startActivity(new Intent(getApplicationContext(), User.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.notifications:
                        startActivity(new Intent(getApplicationContext(), Notifications.class));
                        overridePendingTransition(0,0);
                        return true;


                }

                return false;
            }
        });

        // Assign variable
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        // Initialize array list
        ArrayList<String> arrayList = new ArrayList<>();

        // Add title in array list
        arrayList.add("This Week");
        arrayList.add("This Month");
        arrayList.add("Earlier");

        // Prepare view pager
        prepareViewPager(viewPager, arrayList);

        // Setup with view pager
        tabLayout.setupWithViewPager(viewPager);
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList) {
        // Initialize main adapter
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        // Initialize main fragment
        SearchFragment fragment = new SearchFragment();
        // Use for loop
        for (int i=0; i<arrayList.size(); i++) {
            // Initialize bundle
            Bundle bundle = new Bundle();
            // Put string
            bundle.putString("title", arrayList.get(i));
            // Set argument
            fragment.setArguments(bundle);
            // Add fragment
            adapter.addFragment(fragment, arrayList.get(i));
            // Define new fragment
            fragment = new SearchFragment();
        }
        // Set adapter
        viewPager.setAdapter(adapter);
    }

    private class MainAdapter extends FragmentPagerAdapter {
        // Initialize array list
        ArrayList<String> arrayList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        // Create constructor
        public void addFragment(Fragment fragment, String title) {
            // Add tittle
            arrayList.add(title);
            // Add fragment
            fragmentList.add(fragment);
        }

        public MainAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // Return fragment position
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Return fragment list size
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            // Return array list position
            return arrayList.get(position);
        }
    }
}