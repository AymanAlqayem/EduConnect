//package com.tarificompany.android_project;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//public class SendMessageActivity extends Fragment {
//
//    private EditText etRecipient, etMessageContent;
//    private Button btnSendMessage;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        Log.d("SendMessageFragment", "onCreateView: Inflating layout");
//
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.activity_send_message, container, false);
//
//        initViews(view);
//        setupListeners();
//
//        return view;
//    }
//
//    private void initViews(View view) {
//        Log.d("SendMessageFragment", "initViews: Initializing views");
//        etRecipient = view.findViewById(R.id.et_recipient);
//        etMessageContent = view.findViewById(R.id.et_message_content);
//        btnSendMessage = view.findViewById(R.id.btn_send_message);
//    }
//
//    private void setupListeners() {
//        Log.d("SendMessageFragment", "setupListeners: Setting up button listener");
//
//        btnSendMessage.setOnClickListener(v -> {
//            String recipient = etRecipient.getText().toString().trim();
//            String content = etMessageContent.getText().toString().trim();
//
//            if (recipient.isEmpty() || content.isEmpty()) {
//                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Placeholder: Send message via API or backend
//            Log.d("SendMessageFragment", "Sending message to: " + recipient);
//            Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
//        });
//    }
//}
