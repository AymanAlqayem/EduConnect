package com.tarificompany.android_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class GenericFragment extends Fragment {

    private int layoutResId;

    public static GenericFragment newInstance(int layoutResId) {
        GenericFragment fragment = new GenericFragment();
        Bundle args = new Bundle();
        args.putInt("layoutResId", layoutResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            layoutResId = getArguments().getInt("layoutResId");
        }
        return inflater.inflate(layoutResId, container, false);
    }
}
