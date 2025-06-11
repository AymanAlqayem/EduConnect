package com.tarificompany.android_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;

public class AddStudentFragment extends Fragment {
    private EditText etEmail;
    private EditText etPass;
    private EditText etName;
    private EditText etDOB;
    private EditText etParentPhone;
    private Spinner gradeSpinner;
    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";
    private RequestQueue queue;
    private static final String[] GRADES = {
            "Class",  // Hint
            "10th", "11th science", "11th literature",
            "12th science", "12th literature"
    };

    public static AddStudentFragment newInstance() {
        return new AddStudentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_student, container, false);

       setupViews(view);

        return view;
    }

    private void setupViews(View view){
        etEmail = view.findViewById(R.id.etEmail_Add);
        etName = view.findViewById(R.id.etStudentName_Add);
        etPass = view.findViewById(R.id.etPassword_Add);
        etParentPhone = view.findViewById(R.id.etParentPhone_Add);
        etDOB = view.findViewById(R.id.etDOB_Add);
        gradeSpinner = view.findViewById(R.id.spinnerStudentClass_Add);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
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

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(adapter);
        gradeSpinner.setSelection(0);

        queue = Volley.newRequestQueue(requireContext());

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnAddStudent).setOnClickListener(v -> saveStudent());
    }

    private void saveStudent() {
        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String password = etPass.getText().toString().trim();
        String parentPhone = etParentPhone.getText().toString().trim();
        String dob = etDOB.getText().toString().trim();
        String grade = gradeSpinner.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || parentPhone.isEmpty() || dob.isEmpty() || gradeSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!parentPhone.matches("\\d{10}")) {
            Toast.makeText(requireContext(), "Invalid phone number (10 digits required)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dob.matches("\\d{4}-\\d{2}-\\d{2}")) {
            Toast.makeText(requireContext(), "Invalid DOB format (YYYY-MM-DD)", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("fullname", name);
        params.put("email", email);
        params.put("password", password);
        params.put("parentphone", parentPhone);
        params.put("birthDate", dob);
        params.put("class", grade);
        System.out.println(grade);

        sendStudentSaveRequest(params);
    }

    private void sendStudentSaveRequest(Map<String, String> params) {
        String url = BASE_URL + "save_student.php";
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            Toast.makeText(requireContext(), "Student registered successfully", Toast.LENGTH_SHORT).show();
                            clearForm();
                        } else {
                            Toast.makeText(requireContext(), "Failed: " + json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        queue.add(request);
    }

    private void clearForm() {
        etEmail.setText("");
        etName.setText("");
        etPass.setText("");
        etParentPhone.setText("");
        etDOB.setText("");
        gradeSpinner.setSelection(0);
    }
}
