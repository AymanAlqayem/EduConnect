package com.tarificompany.android_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private FloatingActionButton fabCompose;
    private List<Message> messages;
    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        rvMessages = view.findViewById(R.id.rv_messages);
        fabCompose = view.findViewById(R.id.fab_compose);

        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages, message -> {
            Intent intent = new Intent(requireContext(), MessageDetailActivity.class);
            intent.putExtra("message", message);
            startActivity(intent);
        }, false);
        rvMessages.setAdapter(messageAdapter);

        SharedPreferences prefs = requireActivity().getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);
        String teacherId = prefs.getString("teacher_id", "");

        loadMessages(teacherId);
        setupClickListeners();

        return view;
    }

    private void loadMessages(String teacherId) {
        if (teacherId.isEmpty()) {
            Toast.makeText(getContext(), "Teacher ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + "get_teacher_messages.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        messages.clear();
                        if (response.length() == 0) {
                            Toast.makeText(getContext(), "No messages found", Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                if (obj.has("error")) {
                                    Toast.makeText(getContext(), "Error: " + obj.getString("error"), Toast.LENGTH_LONG).show();
                                    continue;
                                }
                                String sender = obj.getString("sender_name");
                                String subject = obj.optString("subject", "No Subject");
                                String timestamp = obj.getString("sent_at");
                                String content = obj.getString("content");
                                messages.add(new Message(sender, subject, timestamp, content));
                            }
                            messageAdapter.updateMessages(messages);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error parsing messages: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        try {
                            String rawResponse = response.toString();
                            Toast.makeText(getContext(), "Raw response: " + rawResponse, Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), "Cannot display raw response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                    Toast.makeText(getContext(), "Network error: " + errorMsg, Toast.LENGTH_SHORT).show();
                    try {
                        if (error.networkResponse != null) {
                            String rawResponse = new String(error.networkResponse.data, "UTF-8");
                            Toast.makeText(getContext(), "Server response: " + rawResponse, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Cannot parse error response", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void setupClickListeners() {
        fabCompose.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ComposeMessageActivity.class);
            startActivity(intent);
        });
    }
}
