package com.tarificompany.android_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class GenerateScheduleFragment extends Fragment {

    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";
    private static final String GENERATE_URL = BASE_URL + "schedule_generation.php";

    public GenerateScheduleFragment() {
        // Required empty public constructor
    }

    public static GenerateScheduleFragment newInstance() {
        return new GenerateScheduleFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_generate_schedule, container, false);

        Button generateBtn = view.findViewById(R.id.btn_generate_schedule);

        generateBtn.setOnClickListener(v -> generateSchedule());

        return view;
    }

    private void generateSchedule() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                GENERATE_URL,
                null,
                response -> {
                    String message = response.optString("message", "Schedule generated.");
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Failed to generate schedule.", Toast.LENGTH_LONG).show();
                }
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
