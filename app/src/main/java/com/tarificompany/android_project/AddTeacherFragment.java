package com.tarificompany.android_project;

import android.app.Dialog;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTeacherFragment extends Fragment {
    private EditText etFullName, etEmail, etPhone, etNotes, etJoiningDate;
    private RadioGroup rgGender;
    private Spinner spinnerSubject;
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
        View view = inflater.inflate(R.layout.fragment_add_teacher, container, false);

        // Initialize views
        setUpViews(view);

        // Set up Spinner
        setUpSpinner();

        // Set up Joining Date EditText
        setUpJoiningDate();

        // Set up Save button
        btnSave.setOnClickListener(v -> saveTeacher());

        return view;
    }

    public void setUpViews(View view) {
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        rgGender = view.findViewById(R.id.rgGender);
        spinnerSubject = view.findViewById(R.id.spinnerSubject);
        etJoiningDate = view.findViewById(R.id.etJoiningDate);
        etNotes = view.findViewById(R.id.etNotes);
        btnSave = view.findViewById(R.id.btnSave);
    }

    public void setUpSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, SUBJECTS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapter);
    }

    public void setUpJoiningDate() {
        // Set the default date to the current date
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String defaultDate = String.format(Locale.US, "%d-%02d-%02d", year, month, day);
        etJoiningDate.setText(defaultDate);

        // Prevent manual typing in the EditText
        etJoiningDate.setFocusable(false);
        etJoiningDate.setOnClickListener(v -> showCustomDatePickerDialog(etJoiningDate));
    }

    private void showCustomDatePickerDialog(EditText editText) {
        Dialog datePickerDialog = new Dialog(requireContext());
        datePickerDialog.setContentView(R.layout.dialog_date_picker);

        // Initialize views
        DatePicker datePicker = datePickerDialog.findViewById(R.id.datePicker);
        Button btnYes = datePickerDialog.findViewById(R.id.btnYes);
        Button btnNo = datePickerDialog.findViewById(R.id.btnNo);

        // Parse the current date in the EditText to set the initial date in the DatePicker
        int year, month, day;
        String currentDate = editText.getText().toString();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = sdf.parse(currentDate);
            Calendar calendar = Calendar.getInstance();
            if (date != null) {
                calendar.setTime(date);
            }
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            // Fallback to current date if parsing fails
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        // Set the initial date in the DatePicker
        datePicker.init(year, month, day, null);

        // Yes button click listener
        btnYes.setOnClickListener(v -> {
            int selectedYear = datePicker.getYear();
            int selectedMonth = datePicker.getMonth() + 1; // Month is 0-based
            int selectedDay = datePicker.getDayOfMonth();
            String date = String.format(Locale.US, "%d-%02d-%02d", selectedYear, selectedMonth, selectedDay);
            editText.setText(date);
            datePickerDialog.dismiss();
        });

        // No button click listener
        btnNo.setOnClickListener(v -> {
            datePickerDialog.dismiss();
        });

        datePickerDialog.show();
    }

    private void saveTeacher() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String subject = spinnerSubject.getSelectedItem().toString();
        String joiningDate = etJoiningDate.getText().toString();

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
        setUpJoiningDate(); // Reset Joining Date to current date
    }
}