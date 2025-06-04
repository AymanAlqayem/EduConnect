// File: UpcomingClassesAdapter.java
package com.tarificompany.android_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UpcomingClassesAdapter extends RecyclerView.Adapter<UpcomingClassesAdapter.ClassViewHolder> {

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

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCourseName;
        private final TextView tvTime;
        private final TextView tvRoom;
        private final TextView tvDay;
        private final TextView tvStudentsCount;
        private final ImageView ivCourseIcon;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tv_course_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvRoom = itemView.findViewById(R.id.tv_room);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvStudentsCount = itemView.findViewById(R.id.tv_students_count);
            ivCourseIcon = itemView.findViewById(R.id.iv_course_icon);
        }

        public void bind(ClassSchedule classSchedule) {
            tvCourseName.setText(classSchedule.getClassName());
            tvTime.setText(classSchedule.getTime());
            tvRoom.setText(classSchedule.getRoom());
            tvDay.setText(classSchedule.getDay());
            tvStudentsCount.setText(String.format("%d Students", classSchedule.getStudentCount()));

        }
    }
}