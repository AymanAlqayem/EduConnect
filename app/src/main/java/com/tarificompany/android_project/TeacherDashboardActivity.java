package com.tarificompany.android_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView tvTodayClasses, tvStudentsCount, tvUnreadMessages;
    private Button btnPublishGrades, btnAddAssignment, btnSendMessage;
    private RecyclerView rvUpcomingClasses;

    private UpcomingClassesAdapter upcomingClassesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);
        Log.d("TeacherDashboard", "onCreate: Setting content view");

        initViews();
        setupToolbar();
        setupNavigationDrawer();
        setupDashboard();
        setupRefreshLayout();

        loadDashboardData();
    }

    private void initViews() {
        Log.d("TeacherDashboard", "initViews: Initializing views");
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        tvTodayClasses = findViewById(R.id.tv_today_classes);
        tvStudentsCount = findViewById(R.id.tv_students_count);
        tvUnreadMessages = findViewById(R.id.tv_unread_messages);
        btnPublishGrades = findViewById(R.id.btn_publish_grades);
        btnAddAssignment = findViewById(R.id.btn_add_assignment);
        btnSendMessage = findViewById(R.id.btn_send_message);
        rvUpcomingClasses = findViewById(R.id.rv_upcoming_classes);
    }

    private void setupToolbar() {
        Log.d("TeacherDashboard", "setupToolbar: Setting up toolbar");
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.teacher_dashboard);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigationDrawer() {
        Log.d("TeacherDashboard", "setupNavigationDrawer: Setting up navigation drawer");
        navigationView.setNavigationItemSelectedListener(this);
        updateHeaderInfo();
    }

    private void setupDashboard() {
        Log.d("TeacherDashboard", "setupDashboard: Setting up dashboard");
        rvUpcomingClasses.setLayoutManager(new LinearLayoutManager(this));
        upcomingClassesAdapter = new UpcomingClassesAdapter(new ArrayList<>());
        rvUpcomingClasses.setAdapter(upcomingClassesAdapter);

        btnPublishGrades.setOnClickListener(v -> openPublishGrades());
        btnAddAssignment.setOnClickListener(v -> openAddAssignment());
        btnSendMessage.setOnClickListener(v -> openSendMessage());
    }

    private void setupRefreshLayout() {
        Log.d("TeacherDashboard", "setupRefreshLayout: Setting up refresh layout");
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primaryColor,
                R.color.accentColor,
                R.color.primaryDarkColor);
    }

    private void updateHeaderInfo() {
        Log.d("TeacherDashboard", "updateHeaderInfo: Updating header info");
        View headerView = navigationView.getHeaderView(0);
        if (headerView == null) {
            Log.e("TeacherDashboard", "updateHeaderInfo: Header view is null");
            return;
        }
        TextView title = headerView.findViewById(R.id.nav_header_title);
        TextView subtitle = headerView.findViewById(R.id.nav_header_subtitle);

        String userName = getIntent().getStringExtra("USER_NAME");
        title.setText(userName != null ? userName : getString(R.string.teacher));
        subtitle.setText(getString(R.string.teacher_dashboard));
    }

    private void loadDashboardData() {
        Log.d("TeacherDashboard", "loadDashboardData: Loading dashboard data");
        tvTodayClasses.setText("4 حصص");
        tvStudentsCount.setText("32 طالب");
        tvUnreadMessages.setText("5 رسائل جديدة");

        List<ClassSchedule> upcomingClasses = new ArrayList<>();
        upcomingClasses.add(new ClassSchedule("الرياضيات", "09:00 - 10:30", "الصف الأول أ"));
        upcomingClasses.add(new ClassSchedule("اللغة العربية", "11:00 - 12:30", "الصف الثاني ب"));
        upcomingClasses.add(new ClassSchedule("العلوم", "01:00 - 02:30", "الصف الثالث ج"));

        upcomingClassesAdapter.updateData(upcomingClasses);
    }

    private void openPublishGrades() {
        Log.d("TeacherDashboard", "openPublishGrades: Opening publish grades fragment");
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PublishGradesFragment())
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Log.e("TeacherDashboard", "Failed to load PublishGradesFragment: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading grades fragment", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAddAssignment() {
        Log.d("TeacherDashboard", "openAddAssignment: Opening add assignment activity");
        try {
            startActivity(new Intent(this, AddAssignmentActivity.class));
        } catch (Exception e) {
            Log.e("TeacherDashboard", "Failed to start AddAssignmentActivity: " + e.getMessage(), e);
            Toast.makeText(this, "شاشة إضافة الواجبات غير متاحة حالياً", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSendMessage() {
        Log.d("TeacherDashboard", "openSendMessage: Opening send message activity");
        try {
            startActivity(new Intent(this, SendMessageActivity.class));
        } catch (Exception e) {
            Log.e("TeacherDashboard", "Failed to start SendMessageActivity: " + e.getMessage(), e);
            Toast.makeText(this, "الشاشة غير متاحة حالياً", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        Log.d("TeacherDashboard", "onRefresh: Refreshing dashboard data");
        loadDashboardData();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d("TeacherDashboard", "onNavigationItemSelected: Item selected - " + item.getItemId());
        int id = item.getItemId();

        if (id == R.id.nav_schedule) {
            try {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new DayScheduleFragment())
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                Log.e("TeacherDashboard", "Failed to load DayScheduleFragment: " + e.getMessage(), e);
                Toast.makeText(this, "Error loading schedule fragment", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_grades) {
            openPublishGrades();
        } else if (id == R.id.nav_messages) {
            try {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MessagesFragment())
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                Log.e("TeacherDashboard", "Failed to load MessagesFragment: " + e.getMessage(), e);
                Toast.makeText(this, "Error loading messages fragment", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "الإعدادات غير متاحة حالياً", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            performLogout();
        }

        drawerLayout.closeDrawers();
        return true;
    }

    private void performLogout() {
        Log.d("TeacherDashboard", "performLogout: Logging out");
        try {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } catch (Exception e) {
            Log.e("TeacherDashboard", "Failed to start LoginActivity: " + e.getMessage(), e);
            Toast.makeText(this, "Error logging out", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}