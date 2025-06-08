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
import java.util.ArrayList;
import java.util.List;

public class UpdateStudentFragment extends Fragment implements UpdateStudentAdapter.OnStudentUpdateListener{
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


    private static final String[] GRADES = {
            "Class",  // Hint
            "10th grade", "11th Scientific grade", "11th Literature grade",
            "12th Scientific grade", "12th Literature grade"
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
        studentsList.add(new Student("101", "Adam Smith", 89, "11L", "0595237162", "29-7-2008"));
        studentsList.add(new Student("101", "Bob Smith", 89, "11L", "0595237162", "8-6-2008"));
        studentsList.add(new Student("102", "Chris Smith", 89, "12L", "0595237162", "7-7-2007"));
        studentsList.add(new Student("103", "Dan Smith", 89, "12S", "0595237162","8-3-2007"));
        studentsList.add(new Student("104", "Evans Smith", 89, "12S", "0595237162","17-9-2007"));
        studentsList.add(new Student("105", "Fadi Smith", 89, "10", "0595237162","19-12-2009"));
        studentsList.add(new Student("106", "Grayson Smith", 89, "11S", "0595237162","8-3-2008"));
        studentsList.add(new Student("107", "Hill Smith", 89, "11S", "0595237162","8-9-2008"));
        studentsList.add(new Student("108", "Ian Smith", 89, "11S", "0595237162","3-1-2008"));

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

    @Override
    public void onStudentSelected(Student student) {
        selectedStudent = student;

        etStudentName.setText(student.getName());
        etStudentPhone.setText(student.getParentPhone());

        switch (student.getStdClass()) {
            case "10":
                gradeSpinner.setSelection(1);
                break;
            case "11S":
                gradeSpinner.setSelection(2);
                break;
            case "11L":
                gradeSpinner.setSelection(3);
                break;
            case "12S":
                gradeSpinner.setSelection(4);
                break;
            case "12L":
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
                case "10th grade":
                    selectedStudent.setStdClass("10");
                    break;
                case "11th Scientific grade":
                    selectedStudent.setStdClass("11S");
                    break;
                case "11th Literature grade":
                    selectedStudent.setStdClass("11L");
                    break;
                case "12th Scientific grade":
                    selectedStudent.setStdClass("12S");
                    break;
                case "12th Literature grade":
                    selectedStudent.setStdClass("12L");
                    break;
                default:
                    break;
            }

            adapter.notifyDataSetChanged();

            etStudentName.setText("");
            etStudentPhone.setText("");
            gradeSpinner.setSelection(0);
            selectedStudent = null;

            Toast.makeText(requireContext(), "Student updated successfully", Toast.LENGTH_SHORT).show();
        });

    }
}
