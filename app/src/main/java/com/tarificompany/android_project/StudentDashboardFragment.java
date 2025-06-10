package com.tarificompany.android_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StudentDashboardFragment extends Fragment {

    private TextView tvWelcome, tvStudentName, tvTodayClasses, tvPendingAssignments, tvUnreadMessages;
    private RecyclerView rvUpcomingClasses;
    private UpcomingClassesAdapter upcomingClassesAdapter;
    private List<ScheduleItem> upcomingClasses = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_dashboard, container, false);

        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvStudentName = view.findViewById(R.id.tv_student_name);
        tvTodayClasses = view.findViewById(R.id.tv_today_classes);
        tvPendingAssignments = view.findViewById(R.id.tv_pending_assignments);
        tvUnreadMessages = view.findViewById(R.id.tv_unread_messages);
        rvUpcomingClasses = view.findViewById(R.id.rv_upcoming_classes);

        rvUpcomingClasses.setLayoutManager(new LinearLayoutManager(getContext()));
        upcomingClassesAdapter = new UpcomingClassesAdapter(upcomingClasses);
        rvUpcomingClasses.setAdapter(upcomingClassesAdapter);

        // Load dashboard data
        new FetchDashboardDataTask().execute();

        return view;
    }

    private class FetchDashboardDataTask extends AsyncTask<Void, Void, String> {
        private String studentName = "";
        private int todayClassesCount = 0;
        private int pendingAssignments = 0;
        private int unreadMessages = 0;
        private boolean isSuccess = false;

        @Override
        protected String doInBackground(Void... voids) {
            SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String studentId = prefs.getString("student_id", "0");

            if (studentId.equals("0")) {
                return "Student ID not found";
            }

            try {
                // Fetch student name
                String nameResponse = fetchDataFromServer("http://10.0.2.2/AndroidProject/get_students.php");
                if (nameResponse == null || nameResponse.trim().isEmpty()) {
                    return "Empty response from server";
                }

                Log.d("DashboardDebug", "Students response: " + nameResponse);

                JSONArray nameArray = new JSONArray(nameResponse);
                for (int i = 0; i < nameArray.length(); i++) {
                    JSONObject student = nameArray.getJSONObject(i);
                    if (student.getString("student_id").equals(studentId)) {
                        studentName = student.getString("name");
                        break;
                    }
                }

                // Fetch today's classes count
                String scheduleResponse = fetchDataFromServer("http://10.0.2.2/AndroidProject/get_student_schedule.php?student_id=" + studentId);
                JSONObject scheduleJson = new JSONObject(scheduleResponse);
                if (scheduleJson.getString("status").equals("success")) {
                    JSONArray scheduleArray = scheduleJson.getJSONArray("data");
                    String today = getDayOfWeek();
                    for (int i = 0; i < scheduleArray.length(); i++) {
                        JSONObject schedule = scheduleArray.getJSONObject(i);
                        if (schedule.getString("day_of_week").equalsIgnoreCase(today)) {
                            todayClassesCount++;
                            upcomingClasses.add(new ScheduleItem(
                                    schedule.getString("subject_name"),
                                    schedule.getString("day_of_week"),
                                    schedule.getString("start_time"),
                                    schedule.getString("end_time")
                            ));
                        }
                    }
                }

                // Fetch pending assignments (placeholder - you'll need to implement this endpoint)
                pendingAssignments = 2; // Temporary value

                // Fetch unread messages count
                String messagesResponse = fetchDataFromServer("http://10.0.2.2/AndroidProject/get_student_messages.php?student_id=" + studentId);
                JSONArray messagesArray = new JSONArray(messagesResponse);
                unreadMessages = messagesArray.length();

                isSuccess = true;
                return "Dashboard data loaded successfully";

            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        private String fetchDataFromServer(String urlString) throws Exception {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(15000);
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    InputStream errorStream = urlConnection.getErrorStream();
                    if (errorStream != null) {
                        reader = new BufferedReader(new InputStreamReader(errorStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }
                    throw new Exception("HTTP error code: " + responseCode + " - " + response.toString());
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    throw new Exception("Input stream is null");
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                String responseStr = response.toString().trim();
                if (!responseStr.startsWith("{") && !responseStr.startsWith("[")) {
                    throw new Exception("Invalid JSON response: " + responseStr);
                }

                return responseStr;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private String getDayOfWeek() {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            switch (day) {
                case Calendar.SUNDAY:
                    return "Sunday";
                case Calendar.MONDAY:
                    return "Monday";
                case Calendar.TUESDAY:
                    return "Tuesday";
                case Calendar.WEDNESDAY:
                    return "Wednesday";
                case Calendar.THURSDAY:
                    return "Thursday";
                case Calendar.FRIDAY:
                    return "Friday";
                case Calendar.SATURDAY:
                    return "Saturday";
                default:
                    return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (getActivity() == null || isDetached()) {
                return;
            }

            if (isSuccess) {
                // Update UI with fetched data
                tvWelcome.setText("Welcome, " + studentName.split(" ")[0] + "!");
                tvStudentName.setText(studentName);
                tvTodayClasses.setText(String.valueOf(todayClassesCount));
                tvPendingAssignments.setText(String.valueOf(pendingAssignments));
                tvUnreadMessages.setText(unreadMessages + (unreadMessages == 1 ? " message" : " messages"));

                // Update upcoming classes list
                upcomingClassesAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Failed to load dashboard data: " + result, Toast.LENGTH_LONG).show();
            }
        }
    }

    // ScheduleItem class to hold schedule data
    private static class ScheduleItem {
        String subjectName;
        String dayOfWeek;
        String startTime;
        String endTime;

        public ScheduleItem(String subjectName, String dayOfWeek, String startTime, String endTime) {
            this.subjectName = subjectName;
            this.dayOfWeek = dayOfWeek;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    // Adapter for upcoming classes RecyclerView
    private static class UpcomingClassesAdapter extends RecyclerView.Adapter<UpcomingClassesAdapter.ClassViewHolder> {
        private List<ScheduleItem> classes;

        public UpcomingClassesAdapter(List<ScheduleItem> classes) {
            this.classes = classes;
        }

        @NonNull
        @Override
        public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_upcoming_class, parent, false);
            return new ClassViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
            ScheduleItem item = classes.get(position);
            holder.tvSubject.setText(item.subjectName);
            holder.tvTime.setText(item.startTime + " - " + item.endTime);
            holder.tvDay.setText(item.dayOfWeek);
        }

        @Override
        public int getItemCount() {
            return classes.size();
        }

        static class ClassViewHolder extends RecyclerView.ViewHolder {
            TextView tvSubject, tvTime, tvDay;

            public ClassViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSubject = itemView.findViewById(R.id.tv_subject);
                tvTime = itemView.findViewById(R.id.tv_time);
                tvDay = itemView.findViewById(R.id.tv_day);
            }
        }
    }
}