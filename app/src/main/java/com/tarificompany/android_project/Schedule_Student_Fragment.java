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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Schedule_Student_Fragment extends Fragment {

    private RecyclerView rvSchedule;
    private UpcomingClassesAdapter adapter;
    private List<ClassSchedule> classScheduleList;
    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";

    public Schedule_Student_Fragment() {
        // Required empty public constructor
    }

    public static Schedule_Student_Fragment newInstance() {
        return new Schedule_Student_Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        rvSchedule = view.findViewById(R.id.rv_schedule);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext()));

        classScheduleList = new ArrayList<>();
        adapter = new UpcomingClassesAdapter(classScheduleList);
        rvSchedule.setAdapter(adapter);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String studentId = prefs.getString("student_id", "");

        fetchSchedule(studentId);

        return view;
    }

    private void fetchSchedule(String studentId) {
        if (studentId.isEmpty()) {
            Toast.makeText(getContext(), "Student ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + "get_section_schedule.php?student_id=" + studentId;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String section = response.getString("section");
                        JSONObject schedule = response.getJSONObject("schedule");

                        List<ClassSchedule> tempList = new ArrayList<>();

                        Iterator<String> days = schedule.keys();
                        while (days.hasNext()) {
                            String day = days.next();
                            JSONObject periods = schedule.getJSONObject(day);

                            Iterator<String> periodKeys = periods.keys();
                            while (periodKeys.hasNext()) {
                                String period = periodKeys.next();
                                String subjectTeacher = periods.getString(period);

                                if (!subjectTeacher.equals("—")) {
                                    tempList.add(new ClassSchedule(subjectTeacher, period, section, day));
                                }
                            }
                        }

                        classScheduleList.clear();
                        classScheduleList.addAll(tempList);
                        adapter.updateData(classScheduleList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load schedule", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

}