package com.tarificompany.android_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Schedule_Teacher_Fragment extends Fragment {

    private RecyclerView rvSchedule;
    private UpcomingClassesAdapter adapter;
    private List<ClassSchedule> classSchedules;
    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";

    public Schedule_Teacher_Fragment() {
        // Required empty public constructor
    }

    public static Schedule_Teacher_Fragment newInstance() {
        return new Schedule_Teacher_Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        rvSchedule = view.findViewById(R.id.rv_schedule);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext()));

        classSchedules = new ArrayList<>();
        adapter = new UpcomingClassesAdapter(classSchedules);
        rvSchedule.setAdapter(adapter);

        SharedPreferences prefs = requireActivity().getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);
        String teacherId = prefs.getString("teacher_id", "");

        fetchSchedule(teacherId);

        return view;
    }

    private void fetchSchedule(String teacherId) {
        if (teacherId.isEmpty()) {
            Toast.makeText(getContext(), "Teacher ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + "get_teacher_schedule.php?teacher_id=" + teacherId;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String id = response.getString("teacher_id");  // Can be used for validation/logging
                        JSONObject schedule = response.getJSONObject("schedule");

                        List<ClassSchedule> tempList = new ArrayList<>();

                        Iterator<String> days = schedule.keys();
                        while (days.hasNext()) {
                            String day = days.next();
                            JSONObject periods = schedule.getJSONObject(day);

                            Iterator<String> periodKeys = periods.keys();
                            while (periodKeys.hasNext()) {
                                String period = periodKeys.next();
                                JSONObject entry = periods.getJSONObject(period);
                                String subject = entry.getString("subject");
                                String section = entry.getString("section");

                                tempList.add(new ClassSchedule(subject, period, section, day));
                            }
                        }

                        classSchedules.clear();
                        classSchedules.addAll(tempList);
                        adapter.updateData(classSchedules);  // Make sure your adapter has this method

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing schedule", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load schedule", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

}