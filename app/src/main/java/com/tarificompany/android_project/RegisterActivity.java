package com.tarificompany.android_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import android.view.MenuItem;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("School Registration");
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            String message = "";

            if (id == R.id.nav_add_student) {
                message = "Add Student selected";
            } else if (id == R.id.nav_edit_student) {
                message = "Edit Student selected";
            } else if (id == R.id.nav_delete_student) {
                message = "Delete Student selected";
            } else if (id == R.id.nav_add_teacher) {
                message = "Add Teacher selected";
            } else if (id == R.id.nav_edit_teacher) {
                message = "Edit Teacher selected";
            } else if (id == R.id.nav_delete_teacher) {
                message = "Delete Teacher selected";
            }

            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawers();
            return true;
        });


        // Set default fragment or behavior here if desired
    }

    // Optional: if you use toolbar, override this
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }
}