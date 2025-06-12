package com.tarificompany.android_project;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeacherAssignmentsFragment extends Fragment {

    private RecyclerView rvAssignments;
    private ProgressBar progressBar;
    private Spinner spinnerFilter;
    private TeacherAssignmentAdapter adapter;
    private List<JSONObject> assignments;
    private List<JSONObject> filteredAssignments;
    private RequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assignments = new ArrayList<>();
        filteredAssignments = new ArrayList<>();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_assignments, container, false);

        rvAssignments = view.findViewById(R.id.teacher_assignments);
        progressBar = view.findViewById(R.id.progressBar);
        spinnerFilter = view.findViewById(R.id.spinner_assignment_filter);

        adapter = new TeacherAssignmentAdapter(filteredAssignments);
        rvAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAssignments.setAdapter(adapter);

        setupFilterSpinner();
        fetchAssignments();

        adapter.setOnItemClickListener(position -> {
            if (getContext() == null) {
                Toast.makeText(requireActivity(), "Fragment not attached", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject assignment = filteredAssignments.get(position);
                Log.d("TeacherAssignments", "Selected Assignment: " + assignment.toString());
                EvaluationAssignmentFragment evaluationFragment = new EvaluationAssignmentFragment();
                Bundle args = new Bundle();
                args.putString("assignment_id", assignment.optString("assignment_id", ""));
                args.putString("title", assignment.optString("title", "No Title"));
                args.putString("subject", assignment.optString("subject_name", "No Subject"));
                args.putString("due_date", assignment.optString("due_date", "No Date"));
                args.putString("description", assignment.optString("description", "No Description"));
                args.putString("student_name", assignment.optString("student_name", "Unknown Student"));
                args.putString("class_name", assignment.optString("class_name", "Unknown Class"));
                args.putString("submission_text", assignment.optString("submission_text", "No Submission"));
                args.putString("submission_file", assignment.optString("submission_file", ""));
                evaluationFragment.setArguments(args);

                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, evaluationFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TeacherAssignments", "Error opening assignment details", e);
                Toast.makeText(getContext(), "Error opening assignment details", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAssignments();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (requestQueue != null) {
            requestQueue.cancelAll(tag -> true);
        }
    }

    private void setupFilterSpinner() {
        String[] filterOptions = {"All", "Submitted", "Graded"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, filterOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAssignments(filterOptions[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterAssignments("Submitted");
            }
        });
    }

    private void filterAssignments(String status) {
        filteredAssignments.clear();
        if (status.equals("All")) {
            filteredAssignments.addAll(assignments);
        } else {
            for (JSONObject assignment : assignments) {
                try {
                    String submissionStatus = assignment.optString("submission_status", "Unknown");
                    if (submissionStatus.equals(status)) {
                        filteredAssignments.add(assignment);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateEmptyView();
    }

    private void fetchAssignments() {
        progressBar.setVisibility(View.VISIBLE);
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String teacherId = prefs.getString("teacher_id", "1");
        String url = "http://10.0.2.2/AndroidProject/get_teacher_assignments.php?teacher_id=" + teacherId;

        requestQueue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        progressBar.setVisibility(View.GONE);
                        Log.d("TeacherAssignments", "Response: " + response.toString());
                        if (response.getString("status").equals("success")) {
                            JSONArray data = response.getJSONArray("data");
                            assignments.clear();
                            for (int i = 0; i < data.length(); i++) {
                                assignments.add(data.getJSONObject(i));
                                Log.d("TeacherAssignments", "Assignment: " + data.getJSONObject(i).toString());
                            }
                            filterAssignments(spinnerFilter.getSelectedItem().toString());
                        } else {
                            Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("TeacherAssignments", "Error parsing response", e);
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("TeacherAssignments", "Network error", error);
                    Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }

    private void updateEmptyView() {
        View emptyView = getView().findViewById(R.id.empty_view_container);
        if (filteredAssignments.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            rvAssignments.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            rvAssignments.setVisibility(View.VISIBLE);
        }
    }
}