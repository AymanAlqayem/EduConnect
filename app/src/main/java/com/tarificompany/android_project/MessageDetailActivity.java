package com.tarificompany.android_project;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MessageDetailActivity extends AppCompatActivity {

    private TextView tvSender, tvTimestamp, tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message_detail);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Message Details");
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        // Initialize views
        tvSender = findViewById(R.id.tv_detail_sender);
        tvTimestamp = findViewById(R.id.tv_detail_timestamp);
        tvContent = findViewById(R.id.tv_detail_content);

        // Get Message object from Intent
        Message message = (Message) getIntent().getSerializableExtra("message");

        // Display message details
        if (message != null) {
            tvSender.setText(message.getSender());
            tvTimestamp.setText(message.getFormattedTimestamp());
            tvContent.setText(message.getContent());
        } else {
            tvSender.setText("Unknown Sender");
            tvTimestamp.setText("Unknown Time");
            tvContent.setText("No content available");
        }
    }
}