package com.tarificompany.android_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class FragmentPublishGrades extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_publish_grades, container, false);

       // Button publishButton = view.findViewById(R.id.btn_publish);
       // publishButton.setOnClickListener(v -> {
            // كود نشر الدرجات هنا
       // });

        return view;
    }
}