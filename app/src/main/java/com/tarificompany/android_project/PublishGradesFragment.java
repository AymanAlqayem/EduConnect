package com.tarificompany.android_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishGradesFragment extends Fragment {

    private Spinner spinnerClass, spinnerAssessmentType, spinnerSubject;
    private EditText etGradePercentage, etStudentGrade;
    private Button btnLoadStudents, btnPrevious, btnNext, btnSubmitAll;
    private TextView tvStudentName, tvStudentCounter;

    private List<String> studentList = new ArrayList<>();
    private List<String> studentIds = new ArrayList<>();
    private List<String> classList = new ArrayList<>();
    private List<String> classIds = new ArrayList<>();
    private List<String> subjectList = new ArrayList<>();
    private List<String> subjectIds = new ArrayList<>();
    private Map<String, String> studentGrades = new HashMap<>();
    private int currentStudentIndex = 0;
    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish_grades, container, false);
        initViews(view);
        setupSpinners();
        setupClickListeners();
        return view;
    }

    private void initViews(View view) {
        spinnerClass = view.findViewById(R.id.spinner_class);
        spinnerAssessmentType = view.findViewById(R.id.spinner_assessment_type);
        spinnerSubject = view.findViewById(R.id.spinner_subject);
        etGradePercentage = view.findViewById(R.id.et_grade_percentage);
        etStudentGrade = view.findViewById(R.id.et_student_grade);
        btnLoadStudents = view.findViewById(R.id.btn_load_students);
        btnPrevious = view.findViewById(R.id.btn_previous);
        btnNext = view.findViewById(R.id.btn_next);
        btnSubmitAll = view.findViewById(R.id.btn_submit_all);
        tvStudentName = view.findViewById(R.id.tv_student_name);
        tvStudentCounter = view.findViewById(R.id.tv_student_counter);
    }

    private void setupSpinners() {
        // Assessment type spinner
        List<String> assessments = new ArrayList<>();
        assessments.add("Quiz");
        assessments.add("Assignment");
        assessments.add("Midterm");
        assessments.add("Final");

        ArrayAdapter<String> assessmentAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, assessments);
        assessmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssessmentType.setAdapter(assessmentAdapter);

        SharedPreferences prefs = requireActivity().getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);
        String teacherId = prefs.getString("teacher_id", "");
        if (teacherId.isEmpty()) {
            showToast("Teacher ID not found");
            return;
        }
        fetchClasses(teacherId);
        fetchSubjects(teacherId);
    }

    private void fetchClasses(String teacherId) {
        String url = BASE_URL + "get_teacher_classes.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        classList.clear();
                        classIds.clear();
                        if (response.length() == 0) {
                            showToast("No classes found");
                        }
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            classList.add(obj.getString("class_name"));
                            classIds.add(obj.getString("class_id"));
                        }
                        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(
                                requireContext(), android.R.layout.simple_spinner_item, classList);
                        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerClass.setAdapter(classAdapter);
                    } catch (Exception e) {
                        showToast("Error loading classes: " + e.getMessage());
                    }
                },
                error -> showToast("Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error")));

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void fetchSubjects(String teacherId) {
        String url = BASE_URL + "get_teacher_subjects.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        subjectList.clear();
                        subjectIds.clear();
                        if (response.length() == 0) {
                            showToast("No subjects found");
                        }
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            subjectList.add(obj.getString("subject_name"));
                            subjectIds.add(obj.getString("subject_id"));
                        }
                        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
                                requireContext(), android.R.layout.simple_spinner_item, subjectList);
                        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSubject.setAdapter(subjectAdapter);
                    } catch (Exception e) {
                        showToast("Error loading subjects: " + e.getMessage());
                    }
                },
                error -> showToast("Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error")));

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void setupClickListeners() {
        btnLoadStudents.setOnClickListener(v -> loadStudents());
        btnPrevious.setOnClickListener(v -> navigateStudent(-1));
        btnNext.setOnClickListener(v -> navigateStudent(1));
        btnSubmitAll.setOnClickListener(v -> submitGrades());
    }

    private void loadStudents() {
        if (spinnerClass.getSelectedItem() == null) {
            showToast("Please select a class");
            return;
        }
        if (spinnerAssessmentType.getSelectedItem() == null) {
            showToast("Please select an assessment type");
            return;
        }
        if (spinnerSubject.getSelectedItem() == null) {
            showToast("Please select a subject");
            return;
        }
        String percentage = etGradePercentage.getText().toString();
        if (percentage.isEmpty()) {
            etGradePercentage.setError("Required");
            return;
        }

        try {
            float gradePercentage = Float.parseFloat(percentage);
            if (gradePercentage < 0 || gradePercentage > 100) {
                etGradePercentage.setError("Percentage must be between 0 and 100");
                return;
            }
        } catch (NumberFormatException e) {
            etGradePercentage.setError("Invalid percentage");
            return;
        }

        String classId = classIds.get(spinnerClass.getSelectedItemPosition());
        String url = BASE_URL + "get_class_students.php?class_id=" + classId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        studentList.clear();
                        studentIds.clear();
                        studentGrades.clear();
                        currentStudentIndex = 0;
                        if (response.length() == 0) {
                            showToast("No students found");
                            return;
                        }
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            studentList.add(obj.getString("name"));
                            studentIds.add(obj.getString("student_id"));
                            studentGrades.put(obj.getString("student_id"), "");
                        }
                        updateStudentDisplay();
                        btnSubmitAll.setVisibility(View.VISIBLE);
                        showToast("Loaded " + studentList.size() + " students");
                    } catch (Exception e) {
                        showToast("Error loading students: " + e.getMessage());
                    }
                },
                error -> showToast("Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error")));

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void navigateStudent(int direction) {
        if (!studentList.isEmpty()) {
            String currentStudentId = studentIds.get(currentStudentIndex);
            studentGrades.put(currentStudentId, etStudentGrade.getText().toString());
        }

        currentStudentIndex += direction;
        if (currentStudentIndex < 0) currentStudentIndex = 0;
        if (currentStudentIndex >= studentList.size()) currentStudentIndex = studentList.size() - 1;

        updateStudentDisplay();
    }

    private void updateStudentDisplay() {
        if (studentList.isEmpty()) {
            tvStudentName.setText("No Students");
            tvStudentCounter.setText("Student 0 of 0");
            etStudentGrade.setText("");
            btnPrevious.setEnabled(false);
            btnNext.setEnabled(false);
            return;
        }

        tvStudentName.setText(studentList.get(currentStudentIndex));
        tvStudentCounter.setText(String.format("Student %d of %d",
                currentStudentIndex + 1, studentList.size()));
        etStudentGrade.setText(studentGrades.get(studentIds.get(currentStudentIndex)));

        btnPrevious.setEnabled(currentStudentIndex > 0);
        btnNext.setEnabled(currentStudentIndex < studentList.size() - 1);
    }

    private void submitGrades() {
        if (!studentList.isEmpty()) {
            String currentStudentId = studentIds.get(currentStudentIndex);
            studentGrades.put(currentStudentId, etStudentGrade.getText().toString());
        }

        for (String studentId : studentGrades.keySet()) {
            String grade = studentGrades.get(studentId);
            if (grade.isEmpty()) {
                showToast("Please enter grade for all students");
                return;
            }
            try {
                float score = Float.parseFloat(grade);
                if (score < 0 || score > Float.parseFloat(etGradePercentage.getText().toString())) {
                    showToast("Invalid grade for student ID " + studentId);
                    return;
                }
            } catch (NumberFormatException e) {
                showToast("Invalid grade format for student ID " + studentId);
                return;
            }
        }

        String subjectId = subjectIds.get(spinnerSubject.getSelectedItemPosition());
        String assessmentType = spinnerAssessmentType.getSelectedItem().toString();
        SharedPreferences prefs = requireActivity().getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);
        String teacherId = prefs.getString("teacher_id", "");

        JSONArray gradesArray = new JSONArray();
        for (int i = 0; i < studentIds.size(); i++) {
            JSONObject grade = new JSONObject();
            try {
                grade.put("student_id", studentIds.get(i));
                grade.put("score", studentGrades.get(studentIds.get(i)));
                gradesArray.put(grade);
            } catch (Exception e) {
                showToast("Error preparing grades: " + e.getMessage());
                return;
            }
        }

        JSONObject payload = new JSONObject();
        try {
            payload.put("subject_id", subjectId);
            payload.put("teacher_id", teacherId);
            payload.put("exam_name", assessmentType);
            payload.put("grades", gradesArray);
        } catch (Exception e) {
            showToast("Error preparing submission: " + e.getMessage());
            return;
        }

        String url = BASE_URL + "insert_exam_grade.php";

        JsonObjectRequest submitRequest = new JsonObjectRequest(Request.Method.POST, url, payload,
                response -> {
                    showToast("Grades submitted successfully");
                    studentGrades.clear();
                    studentList.clear();
                    studentIds.clear();
                    updateStudentDisplay();
                },
                error -> showToast("Submission failed: " + (error.getMessage() != null ? error.getMessage() : "Unknown error")));

        VolleySingleton.getInstance(getContext()).addToRequestQueue(submitRequest);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
