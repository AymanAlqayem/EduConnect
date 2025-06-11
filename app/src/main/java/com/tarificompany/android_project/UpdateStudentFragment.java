package com.tarificompany.android_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateStudentFragment extends Fragment implements UpdateStudentAdapter.OnStudentUpdateListener{
    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";
    private Spinner gradeSpinner;
    private RecyclerView rvStudents;
    private EditText etStudentSearch;
    private EditText etStudentName;
    private EditText etStudentPhone;
    private Button updateBtn;
    private UpdateStudentAdapter adapter;
    private List<Student> studentsList;
    private List<Student> filteredList;
    private Student selectedStudent = null;
    private RequestQueue queue;
    private static final String[] GRADES = {
            "Class",  // Hint
            "10th", "11th science", "11th literature",
            "12th science", "12th literature"
    };

    public static UpdateStudentFragment newInstance() {
        return new UpdateStudentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_student, container, false);

        setUpViews(view);
        fetchStudents();
        setUpSearch();
        onStudentUpdate(null);

        return view;
    }

    public void setUpViews(View view){
        rvStudents = view.findViewById(R.id.rvStudents);
        rvStudents.setLayoutManager(new LinearLayoutManager(requireContext()));
        etStudentSearch = view.findViewById(R.id.etStudentSearch_Update);
        etStudentName = view.findViewById(R.id.etStudentName_Update);
        etStudentPhone = view.findViewById(R.id.etStudentPhone_Update);
        gradeSpinner = view.findViewById(R.id.spinnerStudentClass_Update);
        updateBtn = view.findViewById(R.id.btnStudent_Update);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, GRADES) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View dropView = super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    ((android.widget.TextView) dropView).setTextColor(android.graphics.Color.GRAY);
                } else {
                    ((android.widget.TextView) dropView).setTextColor(android.graphics.Color.BLACK);
                }
                return dropView;
            }
        };

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(spinnerAdapter);
        gradeSpinner.setSelection(0);

        studentsList = new ArrayList<>();

        filteredList = new ArrayList<>(studentsList);

        adapter = new UpdateStudentAdapter(filteredList, this);
        rvStudents.setAdapter(adapter);
    }

    void setUpSearch() {
        etStudentSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudent(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // no need
            }
        });
    }


    private void filterStudent(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(studentsList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Student e : studentsList) {
                if (e.getName().toLowerCase().contains(lowerCaseQuery) || e.getStdId().equals(query)) {
                    filteredList.add(e);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void fetchStudents(){
        String url = BASE_URL + "get_students.php";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray studentsArray = response.getJSONArray("students");
                            studentsList.clear();
                            for (int i = 0; i < studentsArray.length(); i++) {
                                JSONObject obj = studentsArray.getJSONObject(i);
                                studentsList.add(new Student(
                                        obj.getString("student_id"),
                                        obj.getString("name"),
                                        obj.getString("email"),
                                        obj.optString("class_name", ""),
                                        obj.optString("parent_phone", ""),
                                        obj.getString("DOB")
                                ));
                            }
                            filteredList.clear();
                            filteredList.addAll(studentsList);
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
                    Toast.makeText(requireContext(), "Error fetching students", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onStudentSelected(Student student) {
        selectedStudent = student;

        etStudentName.setText(student.getName());
        etStudentPhone.setText(student.getParentPhone());

        switch (student.getStdClass()) {
            case "1":
                gradeSpinner.setSelection(1);
                break;
            case "2":
                gradeSpinner.setSelection(2);
                break;
            case "3":
                gradeSpinner.setSelection(3);
                break;
            case "4":
                gradeSpinner.setSelection(4);
                break;
            case "5":
                gradeSpinner.setSelection(5);
                break;
            default:
                gradeSpinner.setSelection(0);
                break;
        }
    }

    public void onStudentUpdate(Student student) {
        updateBtn.setOnClickListener(v -> {
            if (selectedStudent == null) {
                Toast.makeText(requireContext(), "Please select a student to update", Toast.LENGTH_SHORT).show();
                return;
            }

            String updatedName = etStudentName.getText().toString().trim();
            String updatedPhone = etStudentPhone.getText().toString().trim();
            String selectedGrade = gradeSpinner.getSelectedItem().toString();

            if (updatedName.isEmpty() || updatedPhone.isEmpty() || gradeSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedStudent.setName(updatedName);
            selectedStudent.setParentPhone(updatedPhone);

            switch (selectedGrade) {
                case "10th":
                    selectedStudent.setStdClass("1"); break;
                case "11th science":
                    selectedStudent.setStdClass("3"); break;
                case "11th literature":
                    selectedStudent.setStdClass("2"); break;
                case "12th science":
                    selectedStudent.setStdClass("5"); break;
                case "12th literature":
                    selectedStudent.setStdClass("4"); break;
            }


            final String studentId = selectedStudent.getStdId();
            final String name = updatedName;
            final String phone = updatedPhone;
            final String classId = selectedStudent.getStdClass();

            // Clear input & UI state
            etStudentName.setText("");
            etStudentPhone.setText("");
            gradeSpinner.setSelection(0);
            selectedStudent = null;

            // Prepare and send request
            String url = BASE_URL + "update_student.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("success")) {
                                Toast.makeText(requireContext(), "Updated in database", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Server error: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(requireContext(), "Response parse error", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Toast.makeText(requireContext(), "Failed to update student", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("student_id", studentId);
                    params.put("name", name);
                    params.put("parent_phone", phone);
                    params.put("class_id", classId);
                    return params;
                }
            };

            Volley.newRequestQueue(requireContext()).add(stringRequest);
            adapter.notifyDataSetChanged();

            Toast.makeText(requireContext(), "Student updated successfully", Toast.LENGTH_SHORT).show();
        });
    }

}
