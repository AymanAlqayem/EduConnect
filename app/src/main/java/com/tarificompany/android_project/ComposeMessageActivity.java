package com.tarificompany.android_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComposeMessageActivity extends AppCompatActivity {

    private Spinner spinnerRecipientType, spinnerRecipient;
    private EditText etSubject, etMessage;
    private Button btnSend;
    private List<String> recipientIds;
    private List<String> recipientNames;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.fragment_compose_message);

            requestQueue = Volley.newRequestQueue(this);

            initViews();

            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            setupSpinners();
            setupClickListeners();

        } catch (Exception e) {
            Toast.makeText(this, "Error loading page: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initViews() {
        spinnerRecipientType = findViewById(R.id.spinner_recipient_type);
        spinnerRecipient = findViewById(R.id.spinner_recipient);
        etSubject = findViewById(R.id.et_subject);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
    }

    private void setupSpinners() {
        try {
            List<String> recipientTypes = new ArrayList<>();
            recipientTypes.add("Individual Student");
            recipientTypes.add("Entire Class");

            ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, recipientTypes);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRecipientType.setAdapter(typeAdapter);

            recipientIds = new ArrayList<>();
            recipientNames = new ArrayList<>();
            updateRecipientsList();

            spinnerRecipientType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateRecipientsList();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing spinners: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void updateRecipientsList() {
        String selectedType = spinnerRecipientType.getSelectedItem().toString();
        recipientIds.clear();
        recipientNames.clear();

        String url;
        if (selectedType.equals("Individual Student")) {
            url = "http://10.0.2.2/AndroidProject/get_students.php";
        } else {
            url = "http://10.0.2.2/AndroidProject/get_classes.php";
        }

        if (selectedType.equals("Individual Student")) {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            recipientNames.clear();
                            recipientIds.clear();
                            JSONArray studentsArray = response.getJSONArray("students");
                            for (int i = 0; i < studentsArray.length(); i++) {
                                JSONObject student = studentsArray.getJSONObject(i);
                                recipientNames.add(student.getString("name"));
                                recipientIds.add(student.getString("student_id"));
                            }
                            ArrayAdapter<String> recipientAdapter = new ArrayAdapter<>(
                                    ComposeMessageActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    recipientNames);
                            recipientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerRecipient.setAdapter(recipientAdapter);
                        } catch (Exception e) {
                            Toast.makeText(ComposeMessageActivity.this, "Error loading recipients", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    },
                    error -> Toast.makeText(ComposeMessageActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

            requestQueue.add(request);

        } else {
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            recipientNames.clear();
                            recipientIds.clear();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject clazz = response.getJSONObject(i);
                                recipientNames.add(clazz.getString("class_name"));
                                recipientIds.add(clazz.getString("class_id"));
                            }

                            ArrayAdapter<String> recipientAdapter = new ArrayAdapter<>(
                                    ComposeMessageActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    recipientNames);
                            recipientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerRecipient.setAdapter(recipientAdapter);
                        } catch (Exception e) {
                            Toast.makeText(ComposeMessageActivity.this, "Error loading classes", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    },
                    error -> Toast.makeText(ComposeMessageActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

            requestQueue.add(request);
        }
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> {
            if (validateInput()) {
                sendMessage();
            }
        });
    }

    private boolean validateInput() {
        if (etSubject.getText().toString().trim().isEmpty()) {
            etSubject.setError("Subject cannot be empty");
            return false;
        }
        if (etMessage.getText().toString().trim().isEmpty()) {
            etMessage.setError("Message cannot be empty");
            return false;
        }
        if (spinnerRecipient.getSelectedItem() == null) {
            Toast.makeText(this, "Please select a recipient", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void sendMessage() {
        try {
            String recipientType = spinnerRecipientType.getSelectedItem().toString();
            String recipientId = recipientIds.get(spinnerRecipient.getSelectedItemPosition());
            String subject = etSubject.getText().toString().trim();
            String message = etMessage.getText().toString().trim();

            SharedPreferences prefs = getSharedPreferences("TeacherPrefs", MODE_PRIVATE);
            String teacherId = prefs.getString("teacher_id", "");

            JSONObject payload = new JSONObject();
            payload.put("sender_id", teacherId);
            payload.put("sender_type", "Teacher");
            payload.put("recipient_id", recipientId);
            payload.put("recipient_type", recipientType.equals("Individual Student") ? "Student" : "Class");
            payload.put("subject", subject);
            payload.put("content", message);

            String url = "http://10.0.2.2/AndroidProject/send_message.php";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                    response -> {
                        String confirmation = String.format("Message to %s (%s) sent!",
                                spinnerRecipient.getSelectedItem().toString(), recipientType);
                        Snackbar.make(findViewById(android.R.id.content), confirmation, Snackbar.LENGTH_LONG)
                                .setAction("OK", v -> finish())
                                .show();
                    },
                    error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

            requestQueue.add(request);
        } catch (Exception e) {
            Toast.makeText(this, "Error sending message: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
