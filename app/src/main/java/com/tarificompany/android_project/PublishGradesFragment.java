package com.tarificompany.android_project;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishGradesFragment extends Fragment {

    private Spinner spinnerClass, spinnerAssessmentType;
    private EditText etGradePercentage, etStudentGrade;
    private Button btnLoadStudents, btnPrevious, btnNext, btnSubmitAll;
    private TextView tvStudentName, tvStudentCounter;

    private final List<String> studentList = new ArrayList<>();
    private final Map<String, String> studentGrades = new HashMap<>();
    private int currentStudentIndex = 0;

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
        // داتا بدائية لحد ما نربطها بالداتا بيس
        List<String> classes = new ArrayList<>();
        classes.add("Class A");
        classes.add("Class B");
        classes.add("Class C");

        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                classes);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(classAdapter);

        // Assessment type spinner
        List<String> assessments = new ArrayList<>();
        assessments.add("Quiz");
        assessments.add("Assignment");
        assessments.add("Midterm");
        assessments.add("Final");

        ArrayAdapter<String> assessmentAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                assessments);
        assessmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssessmentType.setAdapter(assessmentAdapter);
    }

    private void setupClickListeners() {
        btnLoadStudents.setOnClickListener(v -> loadStudents());
        btnPrevious.setOnClickListener(v -> navigateStudent(-1));
        btnNext.setOnClickListener(v -> navigateStudent(1));
        btnSubmitAll.setOnClickListener(v -> submitGrades());
    }

    private void loadStudents() {
        // Validate inputs
        if (spinnerClass.getSelectedItem() == null) {
            showToast("Please select a class");
            return;
        }

        if (spinnerAssessmentType.getSelectedItem() == null) {
            showToast("Please select an assessment type");
            return;
        }

        String percentage = etGradePercentage.getText().toString();
        if (percentage.isEmpty()) {
            etGradePercentage.setError("Required");
            return;
        }

        // Clear previous data
        studentList.clear();
        studentGrades.clear();
        currentStudentIndex = 0;

        // Load sample students (replace with real data)
        String selectedClass = spinnerClass.getSelectedItem().toString();
        switch (selectedClass) {
            case "Class A":
                studentList.add("John Smith");
                studentList.add("Emily Johnson");
                studentList.add("Michael Brown");
                break;
            case "Class B":
                studentList.add("Sarah Williams");
                studentList.add("David Jones");
                studentList.add("Jessica Garcia");
                break;
            case "Class C":
                studentList.add("Robert Miller");
                studentList.add("Jennifer Davis");
                studentList.add("Thomas Wilson");
                break;
        }

        if (studentList.isEmpty()) {
            showToast("No students found");
            return;
        }

        // Initialize grades map
        for (String student : studentList) {
            studentGrades.put(student, "");
        }

        // Update UI
        updateStudentDisplay();
        btnSubmitAll.setVisibility(View.VISIBLE);
        showToast("Loaded " + studentList.size() + " students");
    }

    private void navigateStudent(int direction) {
        // Save current grade before navigating
        if (!studentList.isEmpty()) {
            String currentStudent = studentList.get(currentStudentIndex);
            studentGrades.put(currentStudent, etStudentGrade.getText().toString());
        }

        // Update index
        currentStudentIndex += direction;

        // Validate bounds
        if (currentStudentIndex < 0) currentStudentIndex = 0;
        if (currentStudentIndex >= studentList.size()) currentStudentIndex = studentList.size() - 1;

        updateStudentDisplay();
    }

    private void updateStudentDisplay() {
        if (studentList.isEmpty()) return;

        // Update student info
        tvStudentName.setText(studentList.get(currentStudentIndex));
        tvStudentCounter.setText(String.format("Student %d of %d",
                currentStudentIndex + 1, studentList.size()));

        // Update grade field
        etStudentGrade.setText(studentGrades.get(studentList.get(currentStudentIndex)));

        // Update button states
        btnPrevious.setEnabled(currentStudentIndex > 0);
        btnNext.setEnabled(currentStudentIndex < studentList.size() - 1);
    }

    private void submitGrades() {
        // Validate all grades
        for (String student : studentList) {
            if (studentGrades.get(student).isEmpty()) {
                showToast("Please enter grade for " + student);
                return;
            }
        }

        // Prepare submission data
        String classSelected = spinnerClass.getSelectedItem().toString();
        String assessmentType = spinnerAssessmentType.getSelectedItem().toString();
        String percentage = etGradePercentage.getText().toString();

        // Show summary (replace with actual submission)
        StringBuilder summary = new StringBuilder();
        summary.append("Grades submitted successfully!\n\n");
        summary.append("Class: ").append(classSelected).append("\n");
        summary.append("Assessment: ").append(assessmentType).append("\n");
        summary.append("Percentage: ").append(percentage).append("%\n\n");

        for (String student : studentList) {
            summary.append(student).append(": ").append(studentGrades.get(student)).append("\n");
        }

        showToast(summary.toString());
        resetForm();
    }

    private void resetForm() {
        studentList.clear();
        studentGrades.clear();
        currentStudentIndex = 0;
        etStudentGrade.setText("");
        tvStudentName.setText("Student Name");
        tvStudentCounter.setText("Student 0 of 0");
        btnSubmitAll.setVisibility(View.GONE);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}