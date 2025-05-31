package com.tarificompany.android_project;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

public class StudentDashboardActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Using same layout as register activity

        setupToolbar();
        setupNavigationDrawer();
        updateHeaderInfo();
        loadDashboardFragment();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Student Dashboard");
    }

    private void setupNavigationDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.fragment_container);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void updateHeaderInfo() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        //View headerView = navigationView.getHeaderView(0);

        //TextView title = headerView.findViewById(R.id.nav_header_title);
        //TextView subtitle = headerView.findViewById(R.id.nav_header_subtitle);

        String userName = getIntent().getStringExtra("USER_NAME");
       // title.setText(userName);
        //subtitle.setText("Student Dashboard");
    }

    private void loadDashboardFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DashboardFragment())
                .commit();
    }
}