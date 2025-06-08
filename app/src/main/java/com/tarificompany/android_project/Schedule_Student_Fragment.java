package com.tarificompany.android_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Schedule_Student_Fragment extends Fragment {

    private static final String TAG = "ScheduleFragment";
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private ArrayList<ScheduleItem> scheduleList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.rv_schedule);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.tv_empty_view);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScheduleAdapter(scheduleList);
        recyclerView.setAdapter(adapter);

        // Load data
        new LoadScheduleTask().execute();

        return view;
    }

    private class LoadScheduleTask extends AsyncTask<Void, Void, String> {
        private boolean isSuccess = false;
        private ArrayList<ScheduleItem> tempList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonResponse = null;

            try {
                // Get student ID from SharedPreferences
                // Get student ID from SharedPreferences
                SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                String studentId = prefs.getString("student_id", "0");

                if (studentId.equals("0")) {
                    return "Student ID not found in preferences";
                }

// Create URL
                URL url = new URL("http://10.0.2.2/AndroidProject/get_student_schedule.php?student_id=" + studentId);
                Log.d(TAG, "Request URL: " + url.toString());

                // Create connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(10000); // 10 seconds
                urlConnection.setReadTimeout(15000); // 15 seconds
                urlConnection.connect();

                // Check response code
                int responseCode = urlConnection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return "HTTP error code: " + responseCode;
                }

                // Read response
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return "Input stream is null";
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return "Response is empty";
                }

                jsonResponse = buffer.toString();
                Log.d(TAG, "Response: " + jsonResponse);

                // Parse JSON
                JSONObject response = new JSONObject(jsonResponse);
                if (response.getString("status").equals("success")) {
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        tempList.add(new ScheduleItem(
                                item.getString("subject_name"),
                                item.getString("day_of_week"),
                                item.getString("start_time"),
                                item.getString("end_time")
                        ));
                    }
                    isSuccess = true;
                    return "Successfully loaded " + tempList.size() + " schedule items";
                } else {
                    return "Server error: " + response.optString("message", "Unknown error");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching schedule", e);
                return "Error: " + e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

            if (isSuccess) {
                if (tempList.isEmpty()) {
                    emptyView.setText("No schedule items found");
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    scheduleList.clear();
                    scheduleList.addAll(tempList);
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            } else {
                emptyView.setText("Error: " + result);
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load schedule", Toast.LENGTH_SHORT).show();
            }

            Log.d(TAG, result);
        }
    }
}