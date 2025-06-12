package com.tarificompany.android_project;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MessageDetailActivity extends AppCompatActivity {

    private TextView tvSender, tvTimestamp, tvSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Message Details");
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        tvSender = findViewById(R.id.tv_detail_sender);
        tvTimestamp = findViewById(R.id.tv_detail_timestamp);
        tvSubject = findViewById(R.id.tv_detail_subject);

        // Get Message object from Intent
        Message message = (Message) getIntent().getSerializableExtra("message");

        // Display message details
        if (message != null) {
            tvSender.setText(message.getSender());
            tvTimestamp.setText(message.getFormattedTimestamp());
            tvSubject.setText(message.getContent());
        } else {
            tvSender.setText("Unknown Sender");
            tvTimestamp.setText("Unknown Time");
            tvSubject.setText("No content available");
        }
    }
}
