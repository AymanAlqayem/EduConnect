package com.tarificompany.android_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddAssignmentFragment extends Fragment {

    private Spinner spinnerClass, spinnerSubject;
    private EditText etTitle, etDescription, etMaxScore;
    private DatePicker datePicker;
    private Button btnAddAssignment;

    private List<String> classList = new ArrayList<>();
    private List<String> classIds = new ArrayList<>();
    private List<String> subjectList = new ArrayList<>();
    private List<String> subjectIds = new ArrayList<>();

    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_assignment, container, false);
        requestQueue = Volley.newRequestQueue(requireContext());

        initViews(view);
        loadTeacherData();
        setupAddButton();
        return view;
    }

    private void initViews(View view) {
        spinnerClass = view.findViewById(R.id.spinner_class);
        spinnerSubject = view.findViewById(R.id.spinner_subject);
        etTitle = view.findViewById(R.id.et_assignment_title);
        etDescription = view.findViewById(R.id.et_assignment_desc);
        etMaxScore = view.findViewById(R.id.et_max_score);
        datePicker = view.findViewById(R.id.datePicker);
        btnAddAssignment = view.findViewById(R.id.btn_add);
    }

    private void loadTeacherData() {
        if (!isAdded()) return;

        SharedPreferences prefs = requireActivity().getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);
        String teacherId = prefs.getString("teacher_id", "");
        if (teacherId.isEmpty()) {
            if (isAdded()) {
                Toast.makeText(getContext(), "Teacher ID not found", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        fetchClasses(teacherId);
        fetchSubjects(teacherId);
    }

    private void fetchClasses(String teacherId) {
        String url = BASE_URL + "get_teacher_classes.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    if (!isAdded()) return;

                    classList.clear();
                    classIds.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            classList.add(obj.getString("class_name"));
                            classIds.add(obj.getString("class_id"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_spinner_item, classList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerClass.setAdapter(adapter);
                    } catch (Exception e) {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Error loading classes: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                error -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Network error loading classes", Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(request);
    }

    private void fetchSubjects(String teacherId) {
        String url = BASE_URL + "get_teacher_subjects.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    if (!isAdded()) return;

                    subjectList.clear();
                    subjectIds.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            subjectList.add(obj.getString("subject_name"));
                            subjectIds.add(obj.getString("subject_id"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_spinner_item, subjectList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSubject.setAdapter(adapter);
                    } catch (Exception e) {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Error loading subjects: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                error -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Network error loading subjects", Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(request);
    }

    private void setupAddButton() {
        btnAddAssignment.setOnClickListener(v -> {
            if (!isAdded()) return;

            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String maxScoreStr = etMaxScore.getText().toString().trim();

            if (spinnerSubject.getSelectedItem() == null || spinnerClass.getSelectedItem() == null) {
                Toast.makeText(getContext(), "Please select class and subject", Toast.LENGTH_SHORT).show();
                return;
            }

            if (title.isEmpty() || maxScoreStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int maxScore;
            try {
                maxScore = Integer.parseInt(maxScoreStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid max score", Toast.LENGTH_SHORT).show();
                return;
            }

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();
            String dueDate = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);

            String subjectId = subjectIds.get(spinnerSubject.getSelectedItemPosition());

            SharedPreferences prefs = requireActivity().getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);
            String teacherId = prefs.getString("teacher_id", "");

            JSONObject postData = new JSONObject();
            try {
                postData.put("subject_id", subjectId);
                postData.put("teacher_id", teacherId);
                postData.put("title", title);
                postData.put("description", description);
                postData.put("due_date", dueDate);
                postData.put("max_score", maxScore);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error preparing data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            String url = BASE_URL + "add_assignment.php";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                    response -> {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Assignment added successfully", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Failed to add assignment", Toast.LENGTH_LONG).show();
                        }
                    });

            requestQueue.add(request);
        });
    }
}
