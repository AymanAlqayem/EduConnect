package com.tarificompany.android_project;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
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

public class UpdateTeacherFragment extends Fragment implements UpdateTeacherAdapter.OnTeacherUpdateListener {

    private RecyclerView rvTeachers;
    private SearchView searchView;
    private UpdateTeacherAdapter adapter;
    private List<Teacher> teacherList;
    private List<Teacher> filteredTeacherList;
    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";

    public static UpdateTeacherFragment newInstance() {
        return new UpdateTeacherFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_teacher, container, false);

        rvTeachers = view.findViewById(R.id.rvTeachers);
        rvTeachers.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchView = view.findViewById(R.id.searchView);
        setupSearchView();

        teacherList = new ArrayList<>();
        filteredTeacherList = new ArrayList<>();
        adapter = new UpdateTeacherAdapter(filteredTeacherList, this);
        rvTeachers.setAdapter(adapter);

        fetchTeachers();

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
                if (teacher.getFullName().toLowerCase().contains(lowerCaseQuery) ||
                        teacher.getEmail().toLowerCase().contains(lowerCaseQuery) ||
                        teacher.getId().equals(query)) {
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

    private void fetchTeachers() {
        String url = BASE_URL + "get_teachers.php";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray teachersArray = response.getJSONArray("teachers");
                            teacherList.clear();
                            for (int i = 0; i < teachersArray.length(); i++) {
                                JSONObject obj = teachersArray.getJSONObject(i);
                                teacherList.add(new Teacher(
                                        obj.getString("id"),
                                        obj.getString("full_name"),
                                        obj.getString("email"),
                                        obj.optString("phone", ""),
                                        obj.optString("subject", ""),
                                        obj.getString("joining_date"),
                                        obj.optString("notes", "")
                                ));
                            }
                            filteredTeacherList.clear();
                            filteredTeacherList.addAll(teacherList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(requireContext(), "Error: " + response.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Error fetching teachers", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(jsonObjectRequest);
    }

    private void showUpdateDialog(Teacher teacher, int position) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_update_teacher);
        dialog.setCancelable(true);

        dialog.getWindow().setLayout(
                (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.99),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        dialog.show();

        // Initialize views
        EditText etFullName = dialog.findViewById(R.id.etFullName);
        EditText etEmail = dialog.findViewById(R.id.etEmail);
        EditText etPhone = dialog.findViewById(R.id.etPhone);
        EditText etDOB = dialog.findViewById(R.id.etDOB);
        Button btnDatePicker = dialog.findViewById(R.id.btnDatePicker);
        Spinner spinnerSubject = dialog.findViewById(R.id.spinnerSubject);
        Spinner spinnerClass = dialog.findViewById(R.id.spinnerClass);
        Spinner spinnerSection = dialog.findViewById(R.id.spinnerSection);
        Button btnAddAssignment = dialog.findViewById(R.id.btnAddAssignment);
        ListView listViewAssignedClasses = dialog.findViewById(R.id.listViewAssignedClasses);
        TextView tvAssignedClassesLabel = dialog.findViewById(R.id.tvAssignedClassesLabel);
        EditText etNotes = dialog.findViewById(R.id.etNotes);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        // Populate teacher data
        etFullName.setText(teacher.getFullName());
        etEmail.setText(teacher.getEmail());
        etPhone.setText(teacher.getPhone());
        etDOB.setText(teacher.getJoiningDate());
        etNotes.setText(teacher.getNotes());

        // Date picker setup
        btnDatePicker.setOnClickListener(v -> showCustomDatePickerDialog(etDOB));

        // Load spinners data
        loadSpinnerData(spinnerSubject, "get_subjects.php", "subjects");
        loadClasses(spinnerClass, spinnerSection);

        // Setup assigned classes list
        List<String> assignedClasses = new ArrayList<>();

        ArrayAdapter<String> assignedClassesAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                assignedClasses
        );
        listViewAssignedClasses.setAdapter(assignedClassesAdapter);

        // Long-click to remove assignments
        listViewAssignedClasses.setOnItemLongClickListener((parent, view, pos, id) -> {
            String removed = assignedClasses.remove(pos);
            assignedClassesAdapter.notifyDataSetChanged();
            updateAssignedClassesLabel(tvAssignedClassesLabel, assignedClasses.size());
            Toast.makeText(requireContext(), "Removed: " + removed, Toast.LENGTH_SHORT).show();
            return true;
        });

        // Fetch and populate assigned classes and sections
        fetchTeacherSections(teacher.getId(), assignedClasses, assignedClassesAdapter, tvAssignedClassesLabel);

        // Add assignment button
        btnAddAssignment.setOnClickListener(v -> {
            String selectedClass = spinnerClass.getSelectedItem() != null ? ((ClassItem) spinnerClass.getSelectedItem()).getName() : "";
            String selectedSection = spinnerSection.getSelectedItem() != null ? spinnerSection.getSelectedItem().toString() : "";

            if (!selectedClass.isEmpty() && !selectedSection.isEmpty()) {
                String newAssignment = selectedClass + " - " + selectedSection;
                if (!assignedClasses.contains(newAssignment)) {
                    assignedClasses.add(newAssignment);
                    assignedClassesAdapter.notifyDataSetChanged();
                    updateAssignedClassesLabel(tvAssignedClassesLabel, assignedClasses.size());
                    Toast.makeText(requireContext(), "Added: " + newAssignment, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "This assignment already exists", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Please select both class and section", Toast.LENGTH_SHORT).show();
            }
        });

        // Save button
        btnSave.setOnClickListener(v -> {
            // Validate inputs
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String dob = etDOB.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();
            String subject = spinnerSubject.getSelectedItem() != null ? spinnerSubject.getSelectedItem().toString() : "";

            if (fullName.isEmpty() || email.isEmpty() || subject.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ensure spinners are populated
            if (spinnerSubject.getAdapter() == null || spinnerSubject.getAdapter().getCount() == 0 ||
                    spinnerClass.getAdapter() == null || spinnerClass.getAdapter().getCount() == 0 ||
                    spinnerSection.getAdapter() == null || spinnerSection.getAdapter().getCount() == 0) {
                Toast.makeText(requireContext(), "Please wait for data to load or select valid options", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prepare JSON data
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("id", teacher.getId());
                jsonBody.put("full_name", fullName);
                jsonBody.put("email", email);
                jsonBody.put("phone", phone);
                jsonBody.put("subject", subject);
                jsonBody.put("joining_date", dob);
                jsonBody.put("notes", notes);

                // Add assigned classes
                JSONArray assignmentsArray = new JSONArray();
                for (String assignment : assignedClasses) {
                    String[] parts = assignment.split(" - ");
                    if (parts.length == 2) {
                        JSONObject assignmentObj = new JSONObject();
                        assignmentObj.put("class", parts[0]);
                        assignmentObj.put("section", parts[1]);
                        assignmentsArray.put(assignmentObj);
                    }
                }
                jsonBody.put("assignments", assignmentsArray);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error preparing data", Toast.LENGTH_SHORT).show();
                return;
            }

            // Fetch class_id and section_id for subject creation if needed
            ClassItem selectedClass = (ClassItem) spinnerClass.getSelectedItem();
            String selectedSection = spinnerSection.getSelectedItem() != null ? spinnerSection.getSelectedItem().toString() : "";

            if (selectedClass != null && !selectedSection.isEmpty()) {
                String url = BASE_URL + "get_sections.php?class_id=" + selectedClass.getId();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                        response -> {
                            try {
                                if (response.getString("status").equals("success")) {
                                    JSONArray sectionsArray = response.getJSONArray("sections");
                                    String sectionId = null;
                                    for (int i = 0; i < sectionsArray.length(); i++) {
                                        JSONObject sectionObj = sectionsArray.getJSONObject(i);
                                        if (sectionObj.getString("section_name").equals(selectedSection)) {
                                            sectionId = sectionObj.getString("section_id");
                                            break;
                                        }
                                    }
                                    if (sectionId != null) {
                                        try {
                                            jsonBody.put("class_id", selectedClass.getId());
                                            jsonBody.put("section_id", sectionId);
                                            updateTeacher(teacher.getId(), jsonBody, position, dialog);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(requireContext(), "Error adding class/section IDs", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(requireContext(), "Section not found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "Failed to load sections: " + response.optString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Error parsing sections", Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> {
                            error.printStackTrace();
                            String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown network error";
                            Toast.makeText(requireContext(), "Error fetching sections: " + errorMsg, Toast.LENGTH_SHORT).show();
                            Log.e("UpdateTeacher", "Volley error fetching sections: ", error);
                        }
                );
                request.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(requireContext()).add(request);
            } else {
                updateTeacher(teacher.getId(), jsonBody, position, dialog);
            }
        });
    }

    private void updateAssignedClassesLabel(TextView label, int count) {
        label.setText("Current Assignments (" + count + "):");
    }

    private void loadSpinnerData(Spinner spinner, String endpoint, String jsonArrayKey) {
        String url = BASE_URL + endpoint;
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray dataArray = response.getJSONArray(jsonArrayKey);
                            List<String> dataList = new ArrayList<>();
                            for (int i = 0; i < dataArray.length(); i++) {
                                dataList.add(dataArray.getString(i));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    dataList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Error loading " + jsonArrayKey, Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(request);
    }

    private void loadClasses(Spinner classSpinner, Spinner sectionSpinner) {
        String url = BASE_URL + "get_classes_a.php";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
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
                                    classList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            classSpinner.setAdapter(adapter);

                            classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    ClassItem selectedClass = (ClassItem) parent.getItemAtPosition(position);
                                    loadSections(sectionSpinner, selectedClass.getId());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    sectionSpinner.setAdapter(null);
                                }
                            });
                        } else {
                            Toast.makeText(requireContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing classes", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Error loading classes", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(request);
    }

    private void loadSections(Spinner spinner, String classId) {
        String url = BASE_URL + "get_sections.php?class_id=" + classId;
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
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
                                    sectionList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                        } else {
                            Toast.makeText(requireContext(), "Failed to load sections", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing sections", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Error loading sections", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(request);
    }

    private void fetchTeacherSections(String teacherId, List<String> assignedClasses, ArrayAdapter<String> adapter, TextView label) {
        String url = BASE_URL + "get_teacher_sections.php?teacher_id=" + teacherId;
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray assignmentsArray = response.getJSONArray("assignments");
                            assignedClasses.clear();
                            for (int i = 0; i < assignmentsArray.length(); i++) {
                                JSONObject assignmentObj = assignmentsArray.getJSONObject(i);
                                String assignment = assignmentObj.getString("class_name") + " - " + assignmentObj.getString("section_name");
                                assignedClasses.add(assignment);
                            }
                            adapter.notifyDataSetChanged();
                            updateAssignedClassesLabel(label, assignedClasses.size());
                        } else {
                            Toast.makeText(requireContext(), "Failed to load assignments: " + response.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing assignments", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Error loading assignments", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(request);
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

    private void updateTeacher(String teacherId, JSONObject jsonBody, int position, Dialog dialog) {
        String url = BASE_URL + "update_teacher.php";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            // Update the teacher in our lists
                            Teacher updatedTeacher = new Teacher(
                                    teacherId,
                                    jsonBody.optString("full_name"),
                                    jsonBody.optString("email"),
                                    jsonBody.optString("phone"),
                                    jsonBody.optString("subject"),
                                    jsonBody.optString("joining_date"),
                                    jsonBody.optString("notes")
                            );

                            // Update in the main list
                            for (int i = 0; i < teacherList.size(); i++) {
                                if (teacherList.get(i).getId().equals(teacherId)) {
                                    teacherList.set(i, updatedTeacher);
                                    break;
                                }
                            }

                            // Update in the filtered list
                            if (position >= 0 && position < filteredTeacherList.size()) {
                                filteredTeacherList.set(position, updatedTeacher);
                                adapter.notifyItemChanged(position);
                            } else {
                                filteredTeacherList.clear();
                                filteredTeacherList.addAll(teacherList);
                                adapter.notifyDataSetChanged();
                            }

                            Toast.makeText(requireContext(), "Teacher updated successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(requireContext(), "Update failed: " + response.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown network error";
                    Toast.makeText(requireContext(), "Update failed: " + errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("UpdateTeacher", "Volley error: ", error);
                }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    // Helper class for Spinner with ID and Name
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