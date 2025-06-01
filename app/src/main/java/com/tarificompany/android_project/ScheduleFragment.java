package com.tarificompany.android_project;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScheduleFragment extends Fragment {

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }
}

