package com.tarificompany.android_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class TeacherDashboardFragment extends Fragment {

    private TextView tvWelcome, tvTodayClasses, tvStudentsCount, tvTotalMessages;
    private String teacherId;
    private RequestQueue queue;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_dashboard, container, false);

        queue = Volley.newRequestQueue(requireContext());

        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvTodayClasses = view.findViewById(R.id.tv_today_classes);
        tvStudentsCount = view.findViewById(R.id.tv_students_count);
        tvTotalMessages = view.findViewById(R.id.tv_total_messages);

        SharedPreferences prefs = requireActivity().getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);
        teacherId = prefs.getString("teacher_id", "");
        if (teacherId.isEmpty()) {
            Toast.makeText(requireContext(), "Teacher ID not found", Toast.LENGTH_SHORT).show();
            return view;
        }

        fetchTeacherName(teacherId);
        fetchTotalClassesAndStudents(teacherId);
        fetchTotalMessages(teacherId);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showToast("Back press intercepted, staying on Dashboard");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return view;
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void fetchTeacherName(String teacherId) {
        String url = "http://10.0.2.2/AndroidProject/get_teachers.php?teacher_id=" + teacherId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray teachersArray = response.getJSONArray("teachers");
                            for (int i = 0; i < teachersArray.length(); i++) {
                                JSONObject teacherObj = teachersArray.getJSONObject(i);
                                if (teacherObj.getString("id").equals(teacherId)) {
                                    String fullName = teacherObj.getString("full_name");
                                    tvWelcome.setText("Welcome, " + fullName + "!");
                                    break;
                                }
                            }
                        } else {
                            tvWelcome.setText("Welcome, Teacher!");
                        }
                    } catch (Exception e) {
                        Log.e("fetchTeacherName", "Error parsing: " + e.getMessage());
                        tvWelcome.setText("Welcome, Teacher!");
                    }
                },
                error -> {
                    Log.e("fetchTeacherName", "Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    tvWelcome.setText("Welcome, Teacher!");
                    Toast.makeText(requireContext(), "Network error, using default name", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }

    private void fetchTotalClassesAndStudents(String teacherId) {
        String urlSchedule = "http://10.0.2.2/AndroidProject/get_teacher_schedule.php?teacher_id=" + teacherId;

        JsonArrayRequest scheduleRequest = new JsonArrayRequest(Request.Method.GET, urlSchedule, null,
                response -> {
                    try {
                        int totalClasses = response.length();
                        tvTodayClasses.setText(String.valueOf(totalClasses));
                        fetchTotalStudentsForClasses(response);
                    } catch (Exception e) {
                        Log.e("fetchTotalClasses", "Error parsing: " + e.getMessage());
                        tvTodayClasses.setText("0");
                        tvStudentsCount.setText("0");
                    }
                },
                error -> {
                    Log.e("fetchTotalClasses", "Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    Toast.makeText(getContext(), "Error loading classes: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                    tvTodayClasses.setText("0");
                    tvStudentsCount.setText("0");
                });

        queue.add(scheduleRequest);
    }

    private void fetchTotalStudentsForClasses(JSONArray scheduleArray) {
        int totalStudents = 0;
        try {
            for (int i = 0; i < scheduleArray.length(); i++) {
                JSONObject scheduleObj = scheduleArray.getJSONObject(i);
                totalStudents += scheduleObj.getInt("student_count");
            }
            tvStudentsCount.setText(String.valueOf(totalStudents));
        } catch (Exception e) {
            Log.e("fetchTotalStudents", "Error parsing: " + e.getMessage());
            tvStudentsCount.setText("0");
        }
    }

    private void fetchTotalMessages(String teacherId) {
        String url = "http://10.0.2.2/AndroidProject/get_teacher_messages.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        int totalMessages = response.length();
                        tvTotalMessages.setText(totalMessages + " messages");
                    } catch (Exception e) {
                        Log.e("fetchTotalMessages", "Error parsing: " + e.getMessage());
                        tvTotalMessages.setText("0 messages");
                    }
                },
                error -> {
                    Log.e("fetchTotalMessages", "Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    tvTotalMessages.setText("0 messages");
                    Toast.makeText(requireContext(), "Network error, defaulting to 0 messages", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }
}