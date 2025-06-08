// File: UpcomingClassesAdapter.java
package com.tarificompany.android_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UpcomingClassesAdapter extends RecyclerView.Adapter<ClassViewHolder> {

    private List<ClassSchedule> classes;

    public UpcomingClassesAdapter(List<ClassSchedule> classes) {
        this.classes = classes != null ? classes : new ArrayList<>();
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upcoming_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassSchedule classSchedule = classes.get(position);
        holder.bind(classSchedule);
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public void updateData(List<ClassSchedule> newClasses) {
        this.classes = newClasses != null ? newClasses : new ArrayList<>();
        notifyDataSetChanged();
    }
}
