package com.tarificompany.android_project;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PublishGradesFragment extends Fragment {

    public PublishGradesFragment() {
        // Required empty public constructor
    }

    public static PublishGradesFragment newInstance() {
        return new PublishGradesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_publish_grades, container, false);
    }
}
