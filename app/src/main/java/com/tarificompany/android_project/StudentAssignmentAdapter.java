package com.tarificompany.android_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

public class StudentAssignmentAdapter extends RecyclerView.Adapter<StudentAssignmentAdapter.ViewHolder> {
    private List<JSONObject> assignments;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public StudentAssignmentAdapter(List<JSONObject> assignments) {
        this.assignments = assignments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_assignment, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject assignment = assignments.get(position);
            holder.tvTitle.setText(assignment.getString("title"));
            holder.tvSubject.setText(assignment.getString("subject_name"));
            holder.tvDueDate.setText("Due: " + assignment.getString("due_date"));
            holder.tvStatus.setText(assignment.getString("submission_status"));
            holder.tvTeacher.setText("Teacher: " + assignment.getString("teacher_name"));
            holder.tvClass.setText("Class: " + assignment.getString("class_name"));

            String status = assignment.getString("submission_status");
            int color;
            switch (status) {
                case "Submitted":
                    color = holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark);
                    break;
                case "Graded":
                    color = holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark);
                    break;
                default: // Pending
                    color = holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark);
            }
            holder.tvStatus.setTextColor(color);

            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.card_scale_in);
            holder.itemView.startAnimation(animation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubject, tvDueDate, tvStatus, tvTeacher, tvClass;

        ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_assignment_title);
            tvSubject = itemView.findViewById(R.id.tv_assignment_subject);
            tvDueDate = itemView.findViewById(R.id.tv_assignment_due_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTeacher = itemView.findViewById(R.id.tv_teacher_name);
            tvClass = itemView.findViewById(R.id.tv_class_name);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}