package com.tarificompany.android_project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComposeMessageActivity extends AppCompatActivity {

    private Spinner spinnerRecipientType, spinnerRecipient;
    private EditText etMessage;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_compose_message); // Reuse existing layout
        initViews();
        setupSpinners();
        setupClickListeners();
    }

    private void initViews() {
        spinnerRecipientType = findViewById(R.id.spinner_recipient_type);
        spinnerRecipient = findViewById(R.id.spinner_recipient);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
    }

    private void setupSpinners() {
        // Setup recipient types spinner
        List<String> recipientTypes = new ArrayList<>();
        recipientTypes.add("Individual Student");
        recipientTypes.add("Entire Class");
        recipientTypes.add("Group of Students");

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                recipientTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRecipientType.setAdapter(typeAdapter);

        // Setup recipients spinner based on type
        updateRecipientsList();

        // Listen for recipient type changes
        spinnerRecipientType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRecipientsList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateRecipientsList() {
        String selectedType = spinnerRecipientType.getSelectedItem().toString();
        List<String> recipients = new ArrayList<>();

        if (selectedType.equals("Individual Student")) {
            recipients.add("John Smith");
            recipients.add("Sarah Johnson");
            recipients.add("Michael Brown");
        } else if (selectedType.equals("Entire Class")) {
            recipients.add("Class A");
            recipients.add("Class B");
            recipients.add("Class C");
        } else if (selectedType.equals("Group of Students")) {
            recipients.add("Group 1 (5 students)");
            recipients.add("Group 2 (3 students)");
            recipients.add("Group 3 (7 students)");
        }

        ArrayAdapter<String> recipientAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                recipients);
        recipientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRecipient.setAdapter(recipientAdapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> {
            if (validateInput()) {
                sendMessage();
            }
        });
    }

    private boolean validateInput() {
        if (etMessage.getText().toString().trim().isEmpty()) {
            etMessage.setError("Message cannot be empty");
            return false;
        }
        return true;
    }

    private void sendMessage() {
        String recipientType = spinnerRecipientType.getSelectedItem().toString();
        String recipient = spinnerRecipient.getSelectedItem().toString();
        String message = etMessage.getText().toString().trim();

        // Show confirmation
        String confirmation = String.format("Message to %s (%s) sent!", recipient, recipientType);
        Snackbar.make(findViewById(android.R.id.content), confirmation, Snackbar.LENGTH_LONG)
                .setAction("OK", v -> finish())
                .show();

        // Add to message history
        addMessageToHistory(recipient, message);
    }

    private void addMessageToHistory(String recipient, String content) {
        // Get current date/time
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date());

        // Create a new sent message
        Message sentMessage = new Message(
                "You", // sender
                "To: " + recipient, // preview
                timestamp,
                content
        );

        // Here you would add it to your database or message list
        Toast.makeText(this, "Message added to history", Toast.LENGTH_SHORT).show();
    }
}