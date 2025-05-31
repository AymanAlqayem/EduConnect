package com.tarificompany.android_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentDaySchedule extends Fragment {

    private RecyclerView scheduleRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_schedule, container, false);

        scheduleRecyclerView = view.findViewById(R.id.rv_schedule);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // هنا يمكنك إضافة Adapter للـ RecyclerView
        // scheduleRecyclerView.setAdapter(new ScheduleAdapter());

        return view;
    }
}