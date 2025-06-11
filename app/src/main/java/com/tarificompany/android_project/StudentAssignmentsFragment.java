package com.tarificompany.android_project;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class StudentAssignmentsFragment extends Fragment {

    private RecyclerView rvAssignments;
    private ProgressBar progressBar;
    private Spinner spinnerFilter;
    private StudentAssignmentAdapter adapter;
    private List<JSONObject> assignments;
    private List<JSONObject> filteredAssignments;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_assignments, container, false);

        rvAssignments = view.findViewById(R.id.rv_assignments);
        progressBar = view.findViewById(R.id.progressBar);
        spinnerFilter = view.findViewById(R.id.spinner_assignment_filter);

        assignments = new ArrayList<>();
        filteredAssignments = new ArrayList<>();
        adapter = new StudentAssignmentAdapter(filteredAssignments);
        rvAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAssignments.setAdapter(adapter);

        setupFilterSpinner();
        fetchAssignments();

        adapter.setOnItemClickListener(position -> {
            try {
                JSONObject assignment = filteredAssignments.get(position);
                AssignmentDetailFragment detailFragment = new AssignmentDetailFragment();
                Bundle args = new Bundle();
                args.putString("assignment_id", assignment.getString("assignment_id"));
                args.putString("title", assignment.getString("title"));
                args.putString("subject", assignment.getString("subject_name"));
                args.putString("due_date", assignment.getString("due_date"));
                args.putString("description", assignment.getString("description"));
                args.putString("teacher_name", assignment.getString("teacher_name"));
                args.putString("class_name", assignment.getString("class_name"));
                detailFragment.setArguments(args);

                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, detailFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error opening assignment details", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setupFilterSpinner() {
        String[] filterOptions = {"All", "Pending", "Submitted", "Graded"};
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
                filterAssignments("All");
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
                    String submissionStatus = assignment.getString("submission_status");
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
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String studentId = prefs.getString("student_id", "1"); // احصل على ID الطالب من التفضيلات
        String url = "http://10.0.2.2/AndroidProject/get_assignments.php?student_id=" + studentId;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        progressBar.setVisibility(View.GONE);
                        if (response.getString("status").equals("success")) {
                            JSONArray data = response.getJSONArray("data");
                            assignments.clear();
                            for (int i = 0; i < data.length(); i++) {
                                assignments.add(data.getJSONObject(i));
                            }
                            filterAssignments(spinnerFilter.getSelectedItem().toString());
                        } else {
                            Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
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