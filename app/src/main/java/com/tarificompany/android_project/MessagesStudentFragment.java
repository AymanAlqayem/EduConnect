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

public class MessagesStudentFragment extends Fragment {

    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private final List<Message> messages = new ArrayList<>();
    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";
    private String studentId;
    private FloatingActionButton fabCompose;

    public static MessagesStudentFragment newInstance(String studentId) {
        MessagesStudentFragment fragment = new MessagesStudentFragment();
        Bundle args = new Bundle();
        args.putString("studentId", studentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            studentId = getArguments().getString("studentId");
        }

        if (studentId == null || studentId.isEmpty()) {
            SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            studentId = prefs.getString("student_id", null);
        }

        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(getContext(), "Student ID is null or empty!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_message, container, false);

        rvMessages = view.findViewById(R.id.rv_messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        messageAdapter = new MessageAdapter(messages, message -> {
            Intent intent = new Intent(requireContext(), MessageDetailActivity.class);
            intent.putExtra("message", message);
            startActivity(intent);
        }, false);

        rvMessages.setAdapter(messageAdapter);

        fabCompose = view.findViewById(R.id.fab_compose);
        fabCompose.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ComposeStudentActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
        });

        loadMessages();

        return view;
    }

    private void loadMessages() {
        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(getContext(), "Student ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + "get_student_messages.php?student_id=" + studentId;

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
                    }
                },
                error -> {
                    String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                    Toast.makeText(getContext(), "Network error: " + errorMsg, Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
