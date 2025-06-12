package com.tarificompany.android_project;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class AssignmentDetailFragment extends Fragment {

    private static final int PICK_FILE_REQUEST = 2;
    private TextView tvTitle, tvSubject, tvDueDate, tvDescription, tvTeacher, tvClass, tvSelectedFile;
    private EditText etSubmissionText;
    private Button btnUploadFile, btnSubmit;
    private ImageButton btnBack;
    private Uri selectedFileUri;
    private String assignmentId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignment_detail, container, false);

        tvTitle = view.findViewById(R.id.tv_detail_title);
        tvSubject = view.findViewById(R.id.tv_detail_subject);
        tvDueDate = view.findViewById(R.id.tv_detail_due_date);
        tvDescription = view.findViewById(R.id.tv_detail_description);
        tvTeacher = view.findViewById(R.id.tv_detail_teacher);
        tvClass = view.findViewById(R.id.tv_detail_class);
        etSubmissionText = view.findViewById(R.id.et_submission_text);
        btnUploadFile = view.findViewById(R.id.btn_upload_file);
        tvSelectedFile = view.findViewById(R.id.tv_selected_file);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnBack = view.findViewById(R.id.btn_back);

        Bundle args = getArguments();
        if (args != null) {
            assignmentId = args.getString("assignment_id");
            tvTitle.setText(args.getString("title"));
            tvSubject.setText("Subject: " + args.getString("subject"));
            tvDueDate.setText("Due: " + args.getString("due_date"));
            tvDescription.setText("Description: " + args.getString("description"));
            tvTeacher.setText("Teacher: " + args.getString("teacher_name"));
            tvClass.setText("Class: " + args.getString("class_name"));
        }

        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        btnUploadFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_FILE_REQUEST);
        });

        btnSubmit.setOnClickListener(v -> submitAssignment());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedFileUri = data.getData();
            String fileName = selectedFileUri.getLastPathSegment();
            tvSelectedFile.setText(fileName != null ? fileName : "File selected");
        }
    }

    private void submitAssignment() {
        String submissionText = etSubmissionText.getText().toString().trim();
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String studentId = prefs.getString("student_id", "1");
        String url = "http://10.0.2.2/AndroidProject/submit_assignment.php";

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("assignment_id", assignmentId);
            requestBody.put("student_id", studentId);
            requestBody.put("submission_text", submissionText);
            if (selectedFileUri != null) {
                requestBody.put("submission_file", selectedFileUri.toString());
            }

            RequestQueue queue = Volley.newRequestQueue(requireContext());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                    response -> {
                        try {
                            if (response.getString("status").equals("success")) {
                                Toast.makeText(getContext(), "Assignment submitted successfully", Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().popBackStack();
                            } else {
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

            queue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error preparing submission", Toast.LENGTH_SHORT).show();
        }
    }

}