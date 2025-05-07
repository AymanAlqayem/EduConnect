package com.tarificompany.android_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeleteTeacherAdapter extends RecyclerView.Adapter<DeleteTeacherAdapter.TeacherViewHolder> {

    private List<Teacher> teacherList;
    private OnTeacherDeleteListener deleteListener;

    public interface OnTeacherDeleteListener {
        void onTeacherDelete(int position);
    }

    public DeleteTeacherAdapter(List<Teacher> teacherList, OnTeacherDeleteListener deleteListener) {
        this.teacherList = teacherList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delete_teacher, parent, false);
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

        holder.ivDelete.setOnClickListener(v -> {
            // Show confirmation dialog with Yes/No buttons using a specific theme
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext(), android.R.style.Theme_Material_Dialog_Alert);
            builder.setTitle("Delete Teacher")
                    .setMessage("Are you sure you want to delete this Teacher?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteListener.onTeacherDelete(position);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView tvTeacherName, tvEmail, tvSubject, tvGender, tvJoiningDate;
        ImageView ivDelete;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTeacherName = itemView.findViewById(R.id.tvTeacherName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvJoiningDate = itemView.findViewById(R.id.tvJoiningDate);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}