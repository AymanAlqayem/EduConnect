// File: ScheduleFragment.java
package com.tarificompany.android_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private RecyclerView rvSchedule;
    private UpcomingClassesAdapter adapter;
    private List<ClassSchedule> classSchedules;
    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        rvSchedule = view.findViewById(R.id.rv_schedule);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext()));

        classSchedules = new ArrayList<>();
        adapter = new UpcomingClassesAdapter(classSchedules);
        rvSchedule.setAdapter(adapter);

        // Get teacher ID from SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);
        String teacherId = prefs.getString("teacher_id", "");

        // Fetch schedule
        fetchSchedule(teacherId);

        return view;
    }

    private void fetchSchedule(String teacherId) {
        if (teacherId.isEmpty()) {
            Toast.makeText(getContext(), "Teacher ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + "get_teacher_schedule.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        classSchedules.clear();
                        if (response.length() == 0) {
                            Toast.makeText(getContext(), "No schedules found", Toast.LENGTH_SHORT).show();
                        }
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String className = obj.getString("subject_name");
                            String time = obj.getString("start_time") + " - " + obj.getString("end_time");
                            String classGroup = obj.getString("class_name") + " (" + obj.getString("room") + ")";
                            String room = obj.getString("room");
                            String day = obj.optString("day", "Unknown");
                            int studentCount = obj.optInt("student_count", 0);
                            classSchedules.add(new ClassSchedule(className, time, classGroup, room, day, studentCount));
                        }
                        adapter.updateData(classSchedules);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show());

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }
}