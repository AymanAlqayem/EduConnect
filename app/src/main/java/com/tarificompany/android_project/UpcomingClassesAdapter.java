package com.tarificompany.android_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UpcomingClassesAdapter extends RecyclerView.Adapter<UpcomingClassesAdapter.ClassViewHolder> {

    private List<ClassSchedule> classes;

    public UpcomingClassesAdapter(List<ClassSchedule> classes) {
        this.classes = classes;
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
        classes.clear();
        classes.addAll(newClasses);
        notifyDataSetChanged();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvClassName;
        private final TextView tvTime;
        private final TextView tvClassGroup;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tv_class_name);
            tvTime = itemView.findViewById(R.id.tv_class_time);
            tvClassGroup = itemView.findViewById(R.id.tv_class_group);
        }

        public void bind(ClassSchedule classSchedule) {
            tvClassName.setText(classSchedule.getClassName());
            tvTime.setText(classSchedule.getTime());
            tvClassGroup.setText(classSchedule.getClassGroup());
        }
    }
}