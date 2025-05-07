package com.tarificompany.android_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class AddTeacherFragment extends Fragment {
    private EditText etFullName, etEmail, etPhone, etNotes;
    private RadioGroup rgGender;
    private Spinner spinnerSubject;
    private DatePicker datePickerJoining;
    private Button btnSave;

    private static final String[] SUBJECTS = {
            "Mathematics", "English", "Science", "History", "Geography",
            "Computer Science", "Physical Education", "Art", "Music", "Foreign Language"
    };

    public static AddTeacherFragment newInstance() {
        return new AddTeacherFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_teacher, container, false);

        // initialize views
        setUpViews(view);

        // Set up Spinner
        setUpSpinner();

        // Set up DatePicker
        setUpDatePicker();

        // Set up Save button
        btnSave.setOnClickListener(v -> saveTeacher());

        return view;
    }

    public void setUpViews(View view) {
        // Initialize views
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        rgGender = view.findViewById(R.id.rgGender);
        spinnerSubject = view.findViewById(R.id.spinnerSubject);
        datePickerJoining = view.findViewById(R.id.datePickerJoining);
        etNotes = view.findViewById(R.id.etNotes);
        btnSave = view.findViewById(R.id.btnSave);
    }

    public void setUpSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, SUBJECTS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapter);
    }

    public void setUpDatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerJoining.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                null
        );
    }

    private void saveTeacher() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String subject = spinnerSubject.getSelectedItem().toString();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender;
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == R.id.rbMale) {
            gender = "Male";
        } else if (selectedGenderId == R.id.rbFemale) {
            gender = "Female";
        } else {
            Toast.makeText(requireContext(), "Please select a gender", Toast.LENGTH_SHORT).show();
            return;
        }

        int day = datePickerJoining.getDayOfMonth();
        int month = datePickerJoining.getMonth() + 1;
        int year = datePickerJoining.getYear();
        String joiningDate = String.format("%d-%02d-%02d", year, month, day);

        // Save the teacher data (e.g., to a database or list)
        // For now, just show a success message
        Toast.makeText(requireContext(), "Teacher Added Successfully!", Toast.LENGTH_LONG).show();

        // Reset the form to allow adding another teacher
        etFullName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etNotes.setText("");
        rgGender.clearCheck();
        spinnerSubject.setSelection(0);
        setUpDatePicker(); // Reset DatePicker to current date
    }
}