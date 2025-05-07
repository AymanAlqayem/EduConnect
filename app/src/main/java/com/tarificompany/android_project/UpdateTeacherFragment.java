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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdateTeacherFragment extends Fragment implements UpdateTeacherAdapter.OnTeacherUpdateListener {

    private RecyclerView rvTeachers;
    private SearchView searchView;
    private UpdateTeacherAdapter adapter;
    private List<Teacher> teacherList;
    private List<Teacher> filteredTeacherList;

    public static UpdateTeacherFragment newInstance() {
        return new UpdateTeacherFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_teacher, container, false);

        // Initialize RecyclerView
        rvTeachers = view.findViewById(R.id.rvTeachers);
        rvTeachers.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize SearchView
        searchView = view.findViewById(R.id.searchView);

        // Initialize teacher list with 10 different teachers (with IDs)
        teacherList = new ArrayList<>();
        teacherList.add(new Teacher("122", "Alice Johnson", "alice.j@example.com", "1234567890", "Female", "Mathematics", "2023-01-15", "Math expert"));
        teacherList.add(new Teacher("123", "Bob Smith", "bob.smith@example.com", "2345678901", "Male", "English", "2022-06-20", "Literature enthusiast"));
        teacherList.add(new Teacher("124", "Carol White", "carol.w@example.com", "3456789012", "Female", "Science", "2021-09-10", "Physics specialist"));
        teacherList.add(new Teacher("125", "David Brown", "david.b@example.com", "4567890123", "Male", "History", "2020-03-05", "World history buff"));
        teacherList.add(new Teacher("126", "Emma Davis", "emma.d@example.com", "5678901234", "Female", "Geography", "2023-04-22", "Map enthusiast"));
        teacherList.add(new Teacher("127", "Frank Wilson", "frank.w@example.com", "6789012345", "Male", "Computer Science", "2022-11-30", "Coding mentor"));
        teacherList.add(new Teacher("128", "Grace Lee", "grace.l@example.com", "7890123456", "Female", "Physical Education", "2021-07-15", "Sports coach"));
        teacherList.add(new Teacher("129", "Henry Moore", "henry.m@example.com", "8901234567", "Male", "Art", "2020-12-01", "Creative artist"));
        teacherList.add(new Teacher("130", "Isabella Taylor", "isabella.t@example.com", "9012345678", "Female", "Music", "2023-02-28", "Piano virtuoso"));
        teacherList.add(new Teacher("131", "James Anderson", "james.a@example.com", "0123456789", "Male", "Foreign Language", "2022-08-17", "Fluent in Spanish"));

        // Initialize filtered list (initially same as full list)
        filteredTeacherList = new ArrayList<>(teacherList);

        // Set up adapter with filtered list
        adapter = new UpdateTeacherAdapter(filteredTeacherList, this);
        rvTeachers.setAdapter(adapter);

        // Set up search functionality
        setupSearchView();

        return view;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTeachers(newText);
                return true;
            }
        });
    }

    private void filterTeachers(String query) {
        filteredTeacherList.clear();
        if (query.isEmpty()) {
            filteredTeacherList.addAll(teacherList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Teacher teacher : teacherList) {
                if (teacher.getFullName().toLowerCase().contains(lowerCaseQuery) || teacher.getId().equals(query)) {
                    filteredTeacherList.add(teacher);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTeacherUpdate(int position) {
        if (position >= 0 && position < filteredTeacherList.size()) {
            Teacher teacher = filteredTeacherList.get(position);
            showUpdateDialog(teacher, position);
        }
    }

    private void showUpdateDialog(Teacher teacher, int position) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_update_teacher);

        // Initialize dialog views
        EditText etId = dialog.findViewById(R.id.etTeacherId);
        EditText etName = dialog.findViewById(R.id.etTeacherName);
        EditText etEmail = dialog.findViewById(R.id.etTeacherEmail);
        EditText etPhone = dialog.findViewById(R.id.etTeacherPhone);
        EditText etGender = dialog.findViewById(R.id.etTeacherGender);
        Spinner spSubject = dialog.findViewById(R.id.spTeacherSubject);
        EditText etJoiningDate = dialog.findViewById(R.id.etTeacherJoiningDate);
        EditText etNotes = dialog.findViewById(R.id.etTeacherNotes);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        // Populate fields with current teacher data
        etId.setText(teacher.getId());
        etId.setEnabled(false); // Disable ID editing
        etName.setText(teacher.getFullName());
        etEmail.setText(teacher.getEmail());
        etPhone.setText(teacher.getPhone());
        etGender.setText(teacher.getGender());
        etGender.setEnabled(false); // Disable Gender editing
        etNotes.setText(teacher.getNotes());

        // Set up Spinner for subjects
        String[] subjects = {"Mathematics", "English", "Science", "History", "Geography", "Computer Science", "Physical Education", "Art", "Music", "Foreign Language"};
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubject.setAdapter(subjectAdapter);
        spSubject.setSelection(getSubjectIndex(teacher.getSubject(), subjects));

        // Set up DatePicker for Joining Date
        etJoiningDate.setText(teacher.getJoiningDate());
        etJoiningDate.setOnClickListener(v -> showCustomDatePickerDialog(etJoiningDate, teacher.getJoiningDate()));
        etJoiningDate.setFocusable(false); // Prevent manual typing

        // Save button click listener
        btnSave.setOnClickListener(v -> {
            // Update teacher object with new values
            teacher.setFullName(etName.getText().toString());
            teacher.setEmail(etEmail.getText().toString());
            teacher.setPhone(etPhone.getText().toString());
            teacher.setSubject(spSubject.getSelectedItem().toString());
            teacher.setJoiningDate(etJoiningDate.getText().toString());
            teacher.setNotes(etNotes.getText().toString());

            // Update the teacher in the main list
            for (int i = 0; i < teacherList.size(); i++) {
                if (teacherList.get(i).getId().equals(teacher.getId())) {
                    teacherList.set(i, teacher);
                    break;
                }
            }

            // Update the filtered list
            filteredTeacherList.set(position, teacher);
            if (position >= 0 && position < filteredTeacherList.size()) {
                adapter.notifyItemChanged(position);
            }
            Toast.makeText(requireContext(), teacher.getFullName() + " updated successfully.", Toast.LENGTH_SHORT).show();
            dialog.dismiss(); // Dismiss the dialog to return to the UpdateTeacherFragment
        });

        // Cancel button click listener
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private int getSubjectIndex(String subject, String[] subjects) {
        for (int i = 0; i < subjects.length; i++) {
            if (subjects[i].equals(subject)) {
                return i;
            }
        }
        return 0; // Default to first subject if not found
    }

    private void showCustomDatePickerDialog(EditText editText, String currentDate) {
        Dialog datePickerDialog = new Dialog(requireContext());
        datePickerDialog.setContentView(R.layout.dialog_date_picker);

        // Initialize views
        DatePicker datePicker = datePickerDialog.findViewById(R.id.datePicker);
        Button btnYes = datePickerDialog.findViewById(R.id.btnYes);
        Button btnNo = datePickerDialog.findViewById(R.id.btnNo);

        // Parse the current date to set the initial date in the DatePicker
        int year, month, day;
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
        } catch (ParseException e) {
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
}