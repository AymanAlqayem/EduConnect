package com.tarificompany.android_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import android.view.MenuItem;

public class RegisterActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("School Dashboard");

        // Initialize drawer and navigation view
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Set up drawer toggle
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load default fragment (Dashboard)
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, new DashboardFragment());
            transaction.commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

        // Set up navigation item click listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            String title = "School Dashboard";

            if (id == R.id.nav_dashboard) {
                fragment = new DashboardFragment();
                title = "School Dashboard";
            } else if (id == R.id.nav_add_student) {
                fragment = GenericFragment.newInstance(R.layout.activity_add_student);
                title = "Add Student";
            } else if (id == R.id.nav_edit_student) {
                fragment = GenericFragment.newInstance(R.layout.activity_update_student);
                title = "Edit Student";
            } else if (id == R.id.nav_delete_student) {
                fragment = GenericFragment.newInstance(R.layout.activity_delete_student);
                title = "Delete Student";
            } else if (id == R.id.nav_add_teacher) {
                fragment = AddTeacherFragment.newInstance();
                title = "Add Teacher";
            } else if (id == R.id.nav_edit_teacher) {
//                fragment = GenericFragment.newInstance(R.layout.activity_edit_teacher);
                title = "Edit Teacher";
            } else if (id == R.id.nav_delete_teacher) {
                fragment = DeleteTeacherFragment.newInstance();
                title = "Delete Teacher";
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