package com.tarificompany.android_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentGradesFragment extends Fragment {

    private static final String TAG = "StudentGradesFragment";
    private RecyclerView recyclerView;
    private GradesAdapter adapter;
    private List<GradeItem> gradeList = new ArrayList<>();
    private ProgressBar progressBar;
    private View emptyViewContainer;
    private TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.rv_grades);
        progressBar = view.findViewById(R.id.progressBar);
        emptyViewContainer = view.findViewById(R.id.empty_view_container);
        emptyView = view.findViewById(R.id.tv_empty_view);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
        adapter = new GradesAdapter(gradeList);
        recyclerView.setAdapter(adapter);

        // Load grades data
        fetchGrades();

        return view;
    }

    private void fetchGrades() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyViewContainer.setVisibility(View.GONE);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String studentId = prefs.getString("student_id", "");

        if (studentId.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            emptyView.setText("Student ID not found");
            emptyViewContainer.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Student ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/AndroidProject/get_student_grades.php?student_id=" + studentId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        String status = response.optString("status", "error");
                        if (!status.equals("success")) {
                            String message = response.optString("message", "Unknown error");
                            emptyView.setText("Server error: " + message);
                            emptyViewContainer.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Server error: " + message, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        gradeList.clear();
                        JSONArray data = response.getJSONArray("data");
                        if (data.length() == 0) {
                            emptyView.setText("No grades available");
                            emptyViewContainer.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject item = data.getJSONObject(i);
                                gradeList.add(new GradeItem(
                                        item.getString("subject_name"),
                                        item.getString("subject_code"),
                                        item.getString("exam_name"),
                                        item.getDouble("score"),
                                        item.getString("published_at")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyViewContainer.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        emptyView.setText("Error parsing data: " + e.getMessage());
                        emptyViewContainer.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    emptyView.setText("Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    emptyViewContainer.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }
}