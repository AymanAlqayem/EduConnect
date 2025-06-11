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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class TeacherDashboardFragment extends Fragment {

    private TextView tvWelcome, tvTodayClasses, tvStudentsCount, tvUnreadMessages;
    private String teacherId;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_dashboard, container, false);

        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvTodayClasses = view.findViewById(R.id.tv_today_classes);
        tvStudentsCount = view.findViewById(R.id.tv_students_count);
        tvUnreadMessages = view.findViewById(R.id.tv_total_messages);

        SharedPreferences prefs = requireActivity().getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);
        teacherId = prefs.getString("teacher_id", "");

        fetchTeacherName(teacherId);
        fetchTotalClassesAndStudents(teacherId);
        fetchUnreadMessagesCount(teacherId);

        return view;
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
                                    tvWelcome.setText(fullName);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("fetchTeacherName", "Error parsing", e);
                    }
                },
                error -> Log.e("fetchTeacherName", "Network error", error)
        );

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

    private void fetchUnreadMessagesCount(String teacherId) {
        String url = "http://10.0.2.2/AndroidProject/get_teacher_message.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("API_RESPONSE", response.toString());  // <== اطبع الرد كامل هنا

                        int unreadCount = 0;
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject message = response.getJSONObject(i);

                            int isReadValue = message.getInt("is_read");
                            Log.d("MESSAGE_STATUS", "Message id: " + message.getInt("id") + ", is_read: " + isReadValue);

                            boolean isRead = isReadValue == 1;
                            if (!isRead) unreadCount++;
                        }
                        tvUnreadMessages.setText(unreadCount + " unread messages");
                    } catch (Exception e) {
                        tvUnreadMessages.setText("0 unread messages");
                        e.printStackTrace();
                    }
                },
                error -> {
                    tvUnreadMessages.setText("0 unread messages");
                    error.printStackTrace();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void markMessageAsRead(int messageId) {
        String url = "http://10.0.2.2/AndroidProject/mark_as_read.php";

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("message_id", messageId);
            requestData.put("recipient_id", teacherId);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestData,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            int currentCount = getUnreadCountFromTextView();
                            int newCount = Math.max(0, currentCount - 1);
                            tvUnreadMessages.setText(newCount + " unread messages");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private int getUnreadCountFromTextView() {
        String text = tvUnreadMessages.getText().toString();
        try {
            return Integer.parseInt(text.split(" ")[0]);
        } catch (Exception e) {
            return 0;
        }
    }

    interface StudentsCountCallback {
        void onResult(int count);
        void onError();
    }
}