package com.tarificompany.android_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComposeStudentActivity extends AppCompatActivity {

    private Spinner spinnerRecipient; // Only one spinner now
    private EditText etSubject, etMessage;
    private Button btnSend;
    private List<String> recipientIds;
    private List<String> recipientNames;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_compose_student);

        requestQueue = Volley.newRequestQueue(this);

        initViews();
        setupToolbar();
        loadTeacherRecipients();
        setupClickListeners();
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initViews() {
        spinnerRecipient = findViewById(R.id.spinner_recipient);
        etSubject = findViewById(R.id.et_subject);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        recipientIds = new ArrayList<>();
        recipientNames = new ArrayList<>();
    }

    private void loadTeacherRecipients() {
        String url = "http://10.0.2.2/AndroidProject/get_teachers.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray teachersArray = response.getJSONArray("teachers");
                        recipientIds.clear();
                        recipientNames.clear();

                        for (int i = 0; i < teachersArray.length(); i++) {
                            JSONObject teacher = teachersArray.getJSONObject(i);
                            recipientNames.add(teacher.getString("full_name"));
                            recipientIds.add(teacher.getString("id"));
                        }

                        ArrayAdapter<String> recipientAdapter = new ArrayAdapter<>(
                                ComposeStudentActivity.this,
                                android.R.layout.simple_spinner_item,
                                recipientNames);
                        recipientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerRecipient.setAdapter(recipientAdapter);
                    } catch (Exception e) {
                        Toast.makeText(ComposeStudentActivity.this, "Error loading teachers", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ComposeStudentActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
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
        String recipientId = recipientIds.get(spinnerRecipient.getSelectedItemPosition());
        String subject = etSubject.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String studentId = prefs.getString("student_id", "");

        JSONObject payload = new JSONObject();
        try {
            payload.put("sender_id", studentId);
            payload.put("sender_type", "Student");
            payload.put("recipient_id", recipientId);
            payload.put("recipient_type", "Teacher");
            payload.put("subject", subject);
            payload.put("content", message);
        } catch (Exception e) {
            Toast.makeText(this, "Error preparing message", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/AndroidProject/send_student_message.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                response -> {
                    String confirmation = "Message sent to " + spinnerRecipient.getSelectedItem().toString();
                    Snackbar.make(findViewById(android.R.id.content), confirmation, Snackbar.LENGTH_LONG)
                            .setAction("OK", v -> finish())
                            .show();
                },
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

}
