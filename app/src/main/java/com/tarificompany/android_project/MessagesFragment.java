package com.tarificompany.android_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private FloatingActionButton fabCompose;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fabCompose = view.findViewById(R.id.fab_compose);

        setupClickListeners();
    }




    private void loadMessages() {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("John Smith", "Assignment submitted", "2025-05-30 14:00",
                "Dear Teacher, I have submitted my assignment. Please review it when you get time."));
        messages.add(new Message("Sarah Johnson", "Question about project", "2025-05-29 10:30",
                "Hello, I have a question about the project requirements. Can we discuss it tomorrow?"));

        messageAdapter.updateMessages(messages);
    }

    private void setupClickListeners() {
        fabCompose.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ComposeMessageActivity.class);
            startActivity(intent);
        });
    }
}