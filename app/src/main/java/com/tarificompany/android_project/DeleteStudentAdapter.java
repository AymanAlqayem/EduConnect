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

public class DeleteStudentAdapter extends RecyclerView.Adapter<DeleteStudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private OnStudentDeleteListener deleteListener;

    public interface OnStudentDeleteListener {
        void onStudentDelete(int position);
    }

    public DeleteStudentAdapter(List<Student> studentList, OnStudentDeleteListener deleteListener) {
        this.studentList = studentList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delete_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);

        holder.tvStudentName.setText(student.getName());
        holder.tvStudentClass.setText("Class: " + student.getStdClass());
        holder.tvPhone.setText("Phone: " + student.getParentPhone());

        holder.ivDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext(), android.R.style.Theme_Material_Dialog_Alert);
            builder.setTitle("Delete Student")
                    .setMessage("Are you sure you want to delete this student?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteListener.onStudentDelete(position))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvStudentClass, tvPhone;
        ImageView ivDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.svStudentName);
            tvStudentClass = itemView.findViewById(R.id.svClass);
            tvPhone = itemView.findViewById(R.id.svParentPhone);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
