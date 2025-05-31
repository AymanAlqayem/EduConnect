package com.tarificompany.android_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class TeacherDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Setup toolbar
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.teacher_dashboard_title);

        // Setup navigation drawer
        navigationView.setNavigationItemSelectedListener(this);
        updateHeaderInfo();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new DayScheduleFragment(), getString(R.string.my_schedule));
            navigationView.setCheckedItem(R.id.nav_schedule);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void updateHeaderInfo() {
        View headerView = navigationView.getHeaderView(0);
        TextView title = headerView.findViewById(R.id.nav_header_title);
        TextView subtitle = headerView.findViewById(R.id.nav_header_subtitle);

        String userName = getIntent().getStringExtra("USER_NAME");
        title.setText(userName != null ? userName : getString(R.string.teacher));
        subtitle.setText(getString(R.string.teacher_dashboard));
    }

    private void loadFragment(Fragment fragment, String title) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        toolbar.setTitle(title);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        String title = getString(R.string.teacher_dashboard);

        if (id == R.id.nav_schedule) {
            fragment = new FragmentDaySchedule();
            title = "Class Schedule";
        } else if (id == R.id.nav_grades) {
            fragment = new FragmentPublishGrades();
            title = "Publish Grades";
        } else if (id == R.id.nav_messages) {
            fragment = new MessagesFragment();
            title = "Messages";
        } else if (id == R.id.nav_settings) {
            // Handle settings
            showSettings();
            drawerLayout.closeDrawers();
            return true;
        } else if (id == R.id.nav_logout) {
            // Handle logout
            performLogout();
            return true;

        } else {
            drawerLayout.closeDrawers();
            return false;
        }

        if (fragment != null) {
            loadFragment(fragment, title);
            drawerLayout.closeDrawers();
        }

        return true;
    }

    // دالة مساعدة للإعدادات
    private void showSettings() {
        // يمكنك استبدال هذا بفتح Fragment للإعدادات
        Toast.makeText(this, "Settings will be implemented soon", Toast.LENGTH_SHORT).show();
    }

    // دالة مساعدة لتسجيل الخروج
    private void performLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Clear session or preferences here
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}