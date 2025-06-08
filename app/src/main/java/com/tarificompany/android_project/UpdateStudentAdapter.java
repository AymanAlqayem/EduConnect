package com.tarificompany.android_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UpdateStudentAdapter extends RecyclerView.Adapter<UpdateStudentAdapter.StudentViewHolder> {
    private List<Student> studentList;
    private OnStudentUpdateListener listener;

    public UpdateStudentAdapter(List<Student> studentList, OnStudentUpdateListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    public interface OnStudentUpdateListener {
        void onStudentSelected(Student student);
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_update_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.tvName.setText(student.getName());
        holder.tvClass.setText(student.getStdClass());
        holder.tvPhone.setText(student.getParentPhone());

        holder.ivUpdate.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStudentSelected(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvClass, tvPhone;
        ImageView ivUpdate;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvClass = itemView.findViewById(R.id.tvClass);
            tvPhone = itemView.findViewById(R.id.tvParentPhone);
            ivUpdate = itemView.findViewById(R.id.ivUpdate);
        }
    }
}
