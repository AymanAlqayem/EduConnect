package com.tarificompany.android_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UpdateTeacherAdapter extends RecyclerView.Adapter<UpdateTeacherAdapter.TeacherViewHolder> {

    private List<Teacher> teacherList;
    private OnTeacherUpdateListener listener;

    public UpdateTeacherAdapter(List<Teacher> teacherList, OnTeacherUpdateListener listener) {
        this.teacherList = teacherList;
        this.listener = listener;
    }

    public interface OnTeacherUpdateListener {
        void onTeacherUpdate(int position);
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_update_teacher, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher teacher = teacherList.get(position);
        holder.tvTeacherName.setText(teacher.getFullName());
        holder.tvEmail.setText(teacher.getEmail());
        holder.tvSubject.setText(teacher.getSubject());
        holder.tvGender.setText(teacher.getGender());
        holder.tvJoiningDate.setText(teacher.getJoiningDate());

        holder.ivUpdate.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTeacherUpdate(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teacherList != null ? teacherList.size() : 0;
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView tvTeacherName, tvEmail, tvSubject, tvGender, tvJoiningDate;
        ImageView ivUpdate;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTeacherName = itemView.findViewById(R.id.tvTeacherName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvJoiningDate = itemView.findViewById(R.id.tvJoiningDate);
            ivUpdate = itemView.findViewById(R.id.ivUpdate);
        }
    }
}