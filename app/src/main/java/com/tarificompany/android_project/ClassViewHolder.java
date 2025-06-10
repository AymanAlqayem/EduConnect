package com.tarificompany.android_project;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ClassViewHolder extends RecyclerView.ViewHolder {
    public final TextView tvCourseName;
    public final TextView tvTime;
    public final TextView tvRoom;
    public final TextView tvDay;
    public final TextView tvStudentsCount;
    public final TextView tvSection;
    public final ImageView ivCourseIcon;

    public ClassViewHolder(@NonNull View itemView) {
        super(itemView);
        tvCourseName = itemView.findViewById(R.id.tv_course_name);
        tvTime = itemView.findViewById(R.id.tv_time);
        tvRoom = itemView.findViewById(R.id.tv_room);
        tvDay = itemView.findViewById(R.id.tv_day);
        tvStudentsCount = itemView.findViewById(R.id.tv_students_count);
        tvSection = itemView.findViewById(R.id.tv_section);
        ivCourseIcon = itemView.findViewById(R.id.iv_course_icon);
    }

    public void bind(ClassSchedule classSchedule) {
        if (classSchedule == null) return;

        tvCourseName.setText(classSchedule.getClassName());
        tvTime.setText(classSchedule.getTime());
        tvRoom.setText(classSchedule.getRoom());
        tvDay.setText(classSchedule.getDay());
        tvSection.setText(classSchedule.getSectionName());

        Integer studentCount = classSchedule.getStudentCount();
        if (studentCount == null) {
            tvStudentsCount.setText("0 Students");
        } else {
            tvStudentsCount.setText(String.format("%d Students", studentCount));
        }
    }
}