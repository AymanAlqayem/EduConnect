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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Schedule_Student_Fragment extends Fragment {

    private RecyclerView rvSchedule;
    private UpcomingClassesAdapter adapter;
    private List<ClassSchedule> classScheduleList;
    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";

    public Schedule_Student_Fragment() {
        // Required empty public constructor
    }

    public static Schedule_Student_Fragment newInstance() {
        return new Schedule_Student_Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        rvSchedule = view.findViewById(R.id.rv_schedule);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext()));

        classScheduleList = new ArrayList<>();
        adapter = new UpcomingClassesAdapter(classScheduleList);
        rvSchedule.setAdapter(adapter);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String studentId = prefs.getString("student_id", "");

        fetchSchedule(studentId);

        return view;
    }

    private void fetchSchedule(String studentId) {
        if (studentId.isEmpty()) {
            Toast.makeText(getContext(), "Student ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + "get_student_schedule.php?student_id=" + studentId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String status = response.optString("status", "error");
                        if (!status.equals("success")) {
                            String message = response.optString("message", "Unknown error");
                            Toast.makeText(getContext(), "Server error: " + message, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        classScheduleList.clear();
                        JSONArray dataArray = response.getJSONArray("data");
                        if (dataArray.length() == 0) {
                            Toast.makeText(getContext(), "No schedules found", Toast.LENGTH_SHORT).show();
                        }

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);

                            String className = obj.optString("subject_name", "No subject");
                            String day = obj.optString("day", "Unknown");
                            String startTime = obj.optString("start_time", "");
                            String endTime = obj.optString("end_time", "");
                            String time = startTime + " - " + endTime;
                            String room = obj.optString("room", "N/A");
                            int studentCount = obj.optInt("student_count", 0);
                            String classGroup = obj.optString("class_name", "") + " (" + obj.optString("room", "N/A") + ")";
                            String sectionName = obj.optString("section_name", "");

                            ClassSchedule classSchedule = new ClassSchedule(className, time, classGroup, room, day, studentCount, sectionName);
                            classScheduleList.add(classSchedule);
                        }
                        adapter.updateData(classScheduleList);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(requireContext()).add(request);
    }
}