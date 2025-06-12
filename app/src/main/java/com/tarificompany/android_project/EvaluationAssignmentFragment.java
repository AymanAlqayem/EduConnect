package com.tarificompany.android_project;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class EvaluationAssignmentFragment extends Fragment {

    private TextView tvTitle, tvSubject, tvDueDate, tvDescription, tvStudent, tvClass, tvSubmissionText, tvSubmissionFile;
    private ImageButton btnBack;
    private String assignmentId;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_evaluation, container, false);

        tvTitle = view.findViewById(R.id.tv_detail_title);
        tvSubject = view.findViewById(R.id.tv_detail_subject);
        tvDueDate = view.findViewById(R.id.tv_detail_due_date);
        tvDescription = view.findViewById(R.id.tv_detail_description);
        tvStudent = view.findViewById(R.id.tv_students);
        tvClass = view.findViewById(R.id.tv_detail_class);
        tvSubmissionText = view.findViewById(R.id.submission_texts);
        tvSubmissionFile = view.findViewById(R.id.submission_files);
        btnBack = view.findViewById(R.id.btn_back);

        Bundle args = getArguments();
        if (args != null) {
            assignmentId = args.getString("assignment_id");
            tvTitle.setText(args.getString("title"));
            tvSubject.setText("Subject: " + args.getString("subject"));
            tvDueDate.setText("Due: " + args.getString("due_date"));
            tvDescription.setText("Description: " + args.getString("description"));
            tvStudent.setText("Student: " + args.getString("student_name"));
            tvClass.setText("Class: " + args.getString("class_name"));
            tvSubmissionText.setText("Submission: " + args.getString("submission_text"));
            String fileUrl = args.getString("submission_file", ""); // Default to empty string if null
            Log.d("EvaluationFragment", "File URL: " + fileUrl); // Log the file URL
            if (fileUrl != null && !fileUrl.isEmpty() && Uri.parse(fileUrl).isAbsolute()) {
                tvSubmissionFile.setText("File: " + fileUrl.substring(fileUrl.lastIndexOf("/") + 1));
                tvSubmissionFile.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Log.e("EvaluationFragment", "No activity to handle file URL", e);
                        Toast.makeText(getContext(), "No app available to open this file", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                tvSubmissionFile.setText("File: None");
                tvSubmissionFile.setOnClickListener(null); // Disable click if no valid URL
            }
        }

        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());


        return view;
    }

}
