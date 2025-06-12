package com.tarificompany.android_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class StudentActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);


        toolbar = findViewById(R.id.studentToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Student Dashboard");

        // Initialize drawer and navigation view
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, new StudentDashboardFragment());
            transaction.commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            String title = "Student";


            if (id == R.id.nav_dashboard) {
                fragment = new StudentDashboardFragment();
                title = "Student Dashboard";
            } else if (id == R.id.nav_schedule) {
                fragment = new Schedule_Student_Fragment();
                title = "My Schedule";
            } else if (id == R.id.nav_grades) {
                fragment = new StudentGradesFragment();
                title = "My Grades";
            } else if (id == R.id.nav_assignment) {
                fragment = new StudentAssignmentsFragment();
                title = "Assignment";
            } else if (id == R.id.nav_messages) {
                fragment = new MessagesStudentFragment();
                title = "Messages";

            } else if (id == R.id.nav_logout) {
                Intent intent = new Intent(StudentActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }


            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                drawerLayout.closeDrawers();
                toolbar.setTitle(title);
            }

            return true;
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}