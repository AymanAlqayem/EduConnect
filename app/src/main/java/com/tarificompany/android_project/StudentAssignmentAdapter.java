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

    public StudentAssignmentAdapter(List<JSONObject> assignments) {
        this.assignments = assignments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject assignment = assignments.get(position);
            holder.tvTitle.setText(assignment.getString("title"));
            holder.tvCourse.setText(assignment.getString("course"));
            holder.tvDueDate.setText(assignment.getString("due_date"));

            // Apply animation
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
        TextView tvTitle, tvCourse, tvDueDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_assignment_title);
            tvCourse = itemView.findViewById(R.id.tv_assignment_course);
            tvDueDate = itemView.findViewById(R.id.tv_assignment_due_date);
        }
    }
}