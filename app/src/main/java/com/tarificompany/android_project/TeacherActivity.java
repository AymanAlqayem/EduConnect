package com.tarificompany.android_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class TeacherActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher); // هذا XML فيه الـ Toolbar و FrameLayout فقط

        // تهيئة الـ DrawerLayout و NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_dashboard); // تحديد العنصر الافتراضي

        // تهيئة الـ Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Teacher Dashboard");

        // ربط زر الهامبرجر (Toggle) بالـ DrawerLayout والـ Toolbar
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // تحميل Fragment لوحة التحكم افتراضياً عند فتح النشاط لأول مرة
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TeacherDashboardFragment())
                    .commit();
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            String title = "Teacher Dashboard";

            if (id == R.id.nav_dashboard) {
                fragment = new TeacherDashboardFragment();
                title = "Teacher Dashboard";
            } else if (id == R.id.nav_schedule) {
                fragment = GenericFragment.newInstance(R.layout.fragment_schedule);
                title = "Class Schedule";
            } else if (id == R.id.nav_grades) {
                fragment = GenericFragment.newInstance(R.layout.fragment_publish_grades);
                title = "Publish Grades";
            } else if (id == R.id.nav_messages) {
                fragment = GenericFragment.newInstance(R.layout.fragment_messages);
                title = "Messages";
            } else if (id == R.id.nav_logout) {
                Intent intent = new Intent(TeacherActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
                drawerLayout.closeDrawers();
                toolbar.setTitle(title);
                navigationView.setCheckedItem(id);
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
