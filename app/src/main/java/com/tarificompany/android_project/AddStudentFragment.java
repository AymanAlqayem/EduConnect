package com.tarificompany.android_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddStudentFragment extends Fragment {
    Spinner gradeSpinner;
    private static final String[] GRADES = {
            "Class",  // Hint
            "10th grade", "11th Scientific grade", "11th Literature grade",
            "12th Scientific grade", "12th Literature grade"
    };

    public static AddStudentFragment newInstance() {
        return new AddStudentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_student, container, false);

        gradeSpinner = view.findViewById(R.id.addStudentSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, GRADES) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View dropView = super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    ((android.widget.TextView) dropView).setTextColor(android.graphics.Color.GRAY);
                } else {
                    ((android.widget.TextView) dropView).setTextColor(android.graphics.Color.BLACK);
                }
                return dropView;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(adapter);
        gradeSpinner.setSelection(0);

        return view;
    }
}
