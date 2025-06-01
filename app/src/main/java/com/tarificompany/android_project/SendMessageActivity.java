package com.tarificompany.android_project;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SendMessageActivity extends AppCompatActivity {

    private EditText etRecipient, etMessageContent;
    private Button btnSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Log.d("SendMessage", "onCreate: Setting content view");

        initViews();
        setupListeners();
    }

    private void initViews() {
        Log.d("SendMessage", "initViews: Initializing views");
        etRecipient = findViewById(R.id.et_recipient);
        etMessageContent = findViewById(R.id.et_message_content);
        btnSendMessage = findViewById(R.id.btn_send_message);
    }

    private void setupListeners() {
        Log.d("SendMessage", "setupListeners: Setting up button listener");
        btnSendMessage.setOnClickListener(v -> {
            String recipient = etRecipient.getText().toString().trim();
            String content = etMessageContent.getText().toString().trim();

            if (recipient.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Placeholder: Send message via API or backend
            Log.d("SendMessage", "Sending message to: " + recipient);
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}