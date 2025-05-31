package com.tarificompany.android_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class DashboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_register_dashboard, container, false);

        // Update welcome message based on user type
        TextView dashboardTitle = view.findViewById(R.id.dashboard_title);
        String userType = requireActivity().getIntent().getStringExtra("USER_TYPE");

        if (userType != null) {
            String title = userType.equalsIgnoreCase("teacher") ? "Teacher Dashboard" :
                    userType.equalsIgnoreCase("student") ? "Student Dashboard" :
                            "Registrar Dashboard";
            dashboardTitle.setText(title);
        }

        return view;
    }
}