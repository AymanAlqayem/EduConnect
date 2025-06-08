package com.tarificompany.android_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardFragment extends Fragment {

    private RecyclerView rvUpcomingClasses;
    private UpcomingClassesAdapter adapter;
    private List<ClassSchedule> classSchedules;

    private TextView tvWelcome, tvTodayClasses, tvStudentsCount, tvUnreadMessages;

    private String teacherId;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_dashboard, container, false);

        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvTodayClasses = view.findViewById(R.id.tv_today_classes);
        tvStudentsCount = view.findViewById(R.id.tv_students_count);
        tvUnreadMessages = view.findViewById(R.id.tv_unread_messages);

        rvUpcomingClasses = view.findViewById(R.id.rv_upcoming_classes);
        rvUpcomingClasses.setLayoutManager(new LinearLayoutManager(getContext()));

        classSchedules = new ArrayList<>();
        adapter = new UpcomingClassesAdapter(classSchedules);
        rvUpcomingClasses.setAdapter(adapter);

        SharedPreferences prefs = requireActivity().getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);
        teacherId = prefs.getString("teacher_id", "");

        fetchUpcomingClasses(teacherId);
        fetchTotalClassesAndStudents(teacherId);

        return view;
    }

    private void fetchUpcomingClasses(String teacherId) {
        String url = "http://10.0.2.2/AndroidProject/get_teacher_schedule.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        classSchedules.clear();
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
                        Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void fetchTotalClassesAndStudents(String teacherId) {
        String urlClasses = "http://10.0.2.2/AndroidProject/get_teacher_classes.php?teacher_id=" + teacherId;

        JsonArrayRequest classesRequest = new JsonArrayRequest(Request.Method.GET, urlClasses, null,
                response -> {
                    int totalClasses = response.length();
                    tvTodayClasses.setText(String.valueOf(totalClasses));
                    fetchTotalStudentsForClasses(response);
                },
                error -> Toast.makeText(getContext(), "Error loading classes", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(requireContext()).add(classesRequest);
    }

    private void fetchTotalStudentsForClasses(JSONArray classesArray) {
        final int totalClasses = classesArray.length();
        final int[] completedRequests = {0};
        final int[] totalStudents = {0};

        for (int i = 0; i < totalClasses; i++) {
            try {
                JSONObject classObj = classesArray.getJSONObject(i);
                String classId = classObj.getString("class_id");

                fetchStudentsCountForClass(classId, new StudentsCountCallback() {
                    @Override
                    public void onResult(int count) {
                        totalStudents[0] += count;
                        completedRequests[0]++;
                        if (completedRequests[0] == totalClasses) {
                            tvStudentsCount.setText(String.valueOf(totalStudents[0]));
                        }
                    }

                    @Override
                    public void onError() {
                        completedRequests[0]++;
                        if (completedRequests[0] == totalClasses) {
                            tvStudentsCount.setText(String.valueOf(totalStudents[0]));
                        }
                    }
                });
            } catch (Exception e) {
                completedRequests[0]++;
            }
        }

        if (totalClasses == 0) {
            tvStudentsCount.setText("0");
        }
    }

    private void fetchStudentsCountForClass(String classId, StudentsCountCallback callback) {
        String url = "http://10.0.2.2/AndroidProject/get_class_students.php?class_id=" + classId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> callback.onResult(response.length()),
                error -> callback.onError());

        Volley.newRequestQueue(requireContext()).add(request);
    }

    interface StudentsCountCallback {
        void onResult(int count);
        void onError();
    }
}
