package com.tarificompany.android_project;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class AddTeacherFragment extends Fragment {

    private EditText etFullName, etEmail, etPhone, etNotes, etDOB, etPassword, etRoom;
    private RadioGroup rgGender;
    private Spinner spinnerSubject, spinnerClass, spinnerSection;
    private Button btnSave, btnDatePicker, btnGeneratePassword;
    private ListView listViewAssignedClasses;
    private TextView tvAssignedClassesLabel;

    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";

    private RequestQueue queue;

    private List<String> subjectList;
    private List<String> assignedClasses;
    private ArrayAdapter<String> assignedClassesAdapter;

    public static AddTeacherFragment newInstance() {
        return new AddTeacherFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_teacher, container, false);

        setUpViews(view);

        loadSubjects();
        loadClasses();

        btnDatePicker.setOnClickListener(v -> showDatePickerDialog());

        btnGeneratePassword.setOnClickListener(v -> {
            String randomPassword = generateRandomPassword(8);
            etPassword.setText(randomPassword);
        });


        btnSave.setOnClickListener(v -> saveTeacher());

        return view;
    }

    private void setUpViews(View view) {
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etDOB = view.findViewById(R.id.etDOB);
        etPassword = view.findViewById(R.id.etPassword);
        rgGender = view.findViewById(R.id.rgGender);
        spinnerSubject = view.findViewById(R.id.spinnerSubject);
        spinnerClass = view.findViewById(R.id.spinnerClass);
        spinnerSection = view.findViewById(R.id.spinnerSection);
        etNotes = view.findViewById(R.id.etNotes);
        etRoom = view.findViewById(R.id.etRoom);
        btnSave = view.findViewById(R.id.btnSave);
        btnDatePicker = view.findViewById(R.id.btnDatePicker);
        btnGeneratePassword = view.findViewById(R.id.btnGeneratePassword);
        listViewAssignedClasses = view.findViewById(R.id.listViewAssignedClasses);
        tvAssignedClassesLabel = view.findViewById(R.id.tvAssignedClassesLabel);

        subjectList = new ArrayList<>();
        assignedClasses = new ArrayList<String>(); // Fix: Specify List<String>

        queue = Volley.newRequestQueue(requireContext());

    }

    private void loadSubjects() {
        String url = BASE_URL + "get_subjects.php";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray subjectsArray = response.getJSONArray("subjects");

                            subjectList.clear();
                            for (int i = 0; i < subjectsArray.length(); i++) {
                                subjectList.add(subjectsArray.getString(i));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    subjectList
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSubject.setAdapter(adapter);

                        } else {
                            Toast.makeText(requireContext(), "Failed to load subjects", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Volley Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );

        queue.add(request);
    }

    private void loadClasses() {
        String url = BASE_URL + "get_classes_a.php";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray classesArray = response.getJSONArray("classes");
                            List<ClassItem> classList = new ArrayList<>();
                            for (int i = 0; i < classesArray.length(); i++) {
                                JSONObject classObj = classesArray.getJSONObject(i);
                                String id = classObj.getString("class_id");
                                String name = classObj.getString("class_name");
                                classList.add(new ClassItem(id, name));
                            }
                            ArrayAdapter<ClassItem> adapter = new ArrayAdapter<>(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    classList
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerClass.setAdapter(adapter);
                            spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    ClassItem selectedClass = (ClassItem) parent.getItemAtPosition(position);
                                    loadSections(selectedClass.getId());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    spinnerSection.setAdapter(null);
                                }
                            });
                        } else {
                            Toast.makeText(requireContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Volley Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );
        queue.add(request);
    }

    private void loadSections(String classId) {
        String url = BASE_URL + "get_sections.php?class_id=" + classId;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray sectionsArray = response.getJSONArray("sections");
                            List<String> sectionList = new ArrayList<>();
                            for (int i = 0; i < sectionsArray.length(); i++) {
                                JSONObject sectionObj = sectionsArray.getJSONObject(i);
                                String sectionName = sectionObj.getString("section_name");
                                sectionList.add(sectionName);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    sectionList
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSection.setAdapter(adapter);
                        } else {
                            Toast.makeText(requireContext(), "Failed to load sections", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Volley Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );
        queue.add(request);
    }

    private void showDatePickerDialog() {
        etDOB.setFocusable(false);
        etDOB.setClickable(true);
        showCustomDatePickerDialog();
    }

    private void showCustomDatePickerDialog() {
        Dialog datePickerDialog = new Dialog(requireContext());
        datePickerDialog.setContentView(R.layout.dialog_date_picker);

        DatePicker datePicker = datePickerDialog.findViewById(R.id.datePicker);
        Button btnYes = datePickerDialog.findViewById(R.id.btnYes);
        Button btnNo = datePickerDialog.findViewById(R.id.btnNo);

        int year, month, day;
        String currentDate = etDOB.getText().toString();
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
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        datePicker.init(year, month, day, null);

        btnYes.setOnClickListener(v -> {
            int selectedYear = datePicker.getYear();
            int selectedMonth = datePicker.getMonth() + 1;
            int selectedDay = datePicker.getDayOfMonth();
            String date = String.format(Locale.US, "%d-%02d-%02d", selectedYear, selectedMonth, selectedDay);
            etDOB.setText(date);
            datePickerDialog.dismiss();
        });

        btnNo.setOnClickListener(v -> datePickerDialog.dismiss());

        datePickerDialog.show();
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void updateAssignedClassesVisibility() {
        if (assignedClasses.isEmpty()) {
            tvAssignedClassesLabel.setVisibility(View.GONE);
            listViewAssignedClasses.setVisibility(View.GONE);
        } else {
            tvAssignedClassesLabel.setVisibility(View.VISIBLE);
            listViewAssignedClasses.setVisibility(View.VISIBLE);
            tvAssignedClassesLabel.setText("Current Assignments (" + assignedClasses.size() + "):");
        }
    }

    private void saveTeacher() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dob = etDOB.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || dob.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("\\d{10}")) {
            Toast.makeText(requireContext(), "Invalid phone number (10 digits required)", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        String gender;
        if (selectedGenderId == R.id.rbMale) {
            gender = "Male";
        } else if (selectedGenderId == R.id.rbFemale) {
            gender = "Female";
        } else {
            Toast.makeText(requireContext(), "Please select a gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerSubject.getSelectedItem() == null) {
            Toast.makeText(requireContext(), "Please select a subject", Toast.LENGTH_SHORT).show();
            return;
        }


        String subject = spinnerSubject.getSelectedItem().toString();

        Map<String, String> params = new HashMap<>();
        params.put("fullname", fullName);
        params.put("email", email);
        params.put("phone", phone);
        params.put("dob", dob);
        params.put("password", password);
        params.put("gender", gender);
        params.put("subject", subject);
        params.put("notes", notes);

        sendSaveRequest(params);
    }

    private void sendSaveRequest(Map<String, String> params) {
        String url = BASE_URL + "save_teacher.php";
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            Toast.makeText(requireContext(), "Teacher saved successfully", Toast.LENGTH_SHORT).show();
                            clearForm();
                        } else {
                            Toast.makeText(requireContext(), "Failed to save teacher: " + json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Response parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Save error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        queue.add(request);
    }

    private void clearForm() {
        etFullName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etDOB.setText("");
        etPassword.setText("");
        etNotes.setText("");
        etRoom.setText("");
        rgGender.clearCheck();
        spinnerSubject.setSelection(0);
        spinnerClass.setSelection(0);
        spinnerSection.setAdapter(null);
    }

    private static class ClassItem {
        private final String id;
        private final String name;

        public ClassItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}