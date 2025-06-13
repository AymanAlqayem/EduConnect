package com.tarificompany.android_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardFragment extends Fragment {

    private String url = "http://10.0.2.2/AndroidProject/student_dashboard.php";
    private RequestQueue queue;
    private TextView tvWelcomeMessage;
    private RecyclerView rvAssignments, rvGrades, rvEvents;
    private StudentAssignmentAdapter assignmentAdapter;
    private StudentGradeAdapter gradeAdapter;
    private StudentEventAdapter eventAdapter;
    private List<JSONObject> assignmentList;
    private List<JSONObject> gradeList;
    private List<JSONObject> eventList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_dashboard, container, false);

        queue = Volley.newRequestQueue(requireContext());

        tvWelcomeMessage = view.findViewById(R.id.tv_welcome_message);
        rvAssignments = view.findViewById(R.id.rv_assignments);
        rvGrades = view.findViewById(R.id.rv_grades);
        rvEvents = view.findViewById(R.id.rv_events);

        assignmentList = new ArrayList<>();
        gradeList = new ArrayList<>();
        eventList = new ArrayList<>();
        assignmentAdapter = new StudentAssignmentAdapter(assignmentList);
        gradeAdapter = new StudentGradeAdapter(gradeList);
        eventAdapter = new StudentEventAdapter(eventList);

        rvAssignments.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAssignments.setAdapter(assignmentAdapter);
        rvGrades.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvGrades.setAdapter(gradeAdapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvEvents.setAdapter(eventAdapter);

        checkForBirthdays();
        fetchDashboardData();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showToast("Back press intercepted, staying on DashBoard");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return view;
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void fetchDashboardData() {
        // Retrieve student_id from UserPrefs SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String studentId = prefs.getString("student_id", null);

        if (studentId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_LONG).show();
            return;
        }

        String requestUrl = url + "?student_id=" + studentId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                JSONObject data = response.getJSONObject("data");
                                String firstName = data.getString("name");
                                tvWelcomeMessage.setText("Welcome " + firstName);

                                // Populate assignments
                                JSONArray assignmentsArray = data.getJSONArray("assignments");
                                List<JSONObject> newAssignments = new ArrayList<>();
                                for (int i = 0; i < assignmentsArray.length(); i++) {
                                    newAssignments.add(assignmentsArray.getJSONObject(i));
                                }
                                assignmentList.clear();
                                assignmentList.addAll(newAssignments);
                                assignmentAdapter.notifyDataSetChanged();

                                // Populate grades
                                JSONArray gradesArray = data.getJSONArray("grades");
                                List<JSONObject> newGrades = new ArrayList<>();
                                for (int i = 0; i < gradesArray.length(); i++) {
                                    newGrades.add(gradesArray.getJSONObject(i));
                                }
                                gradeList.clear();
                                gradeList.addAll(newGrades);
                                gradeAdapter.notifyDataSetChanged();

                                // Populate events
                                JSONArray eventsArray = data.getJSONArray("events");
                                List<JSONObject> newEvents = new ArrayList<>();
                                for (int i = 0; i < eventsArray.length(); i++) {
                                    newEvents.add(eventsArray.getJSONObject(i));
                                }
                                eventList.clear();
                                eventList.addAll(newEvents);
                                eventAdapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(requireContext(),
                                        response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(requireContext(),
                                    "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(),
                                "Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"),
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the queue
        queue.add(jsonObjectRequest);
    }
    private void checkForBirthdays() {
        String url = "http://10.0.2.2/AndroidProject/get_birthdays_today.php";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray birthdays = response.getJSONArray("birthdays");
                            if (birthdays.length() > 0) {
                                // Show PNG
                                ImageView birthdayIcon = requireView().findViewById(R.id.ivBirthdayIcon);
                                birthdayIcon.setVisibility(View.VISIBLE);
                                Toast.makeText(requireContext(), "Happy Birthday!", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel all pending requests to prevent memory leaks
        if (queue != null) {
            queue.cancelAll(this);
        }
    }
}