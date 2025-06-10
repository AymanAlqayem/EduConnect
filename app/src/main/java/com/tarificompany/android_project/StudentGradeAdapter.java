package com.tarificompany.android_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

public class StudentGradeAdapter extends RecyclerView.Adapter<StudentGradeAdapter.ViewHolder> {
    private List<JSONObject> grades;

    public StudentGradeAdapter(List<JSONObject> grades) {
        this.grades = grades;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_grade, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject grade = grades.get(position);
            holder.tvTitle.setText(grade.getString("title"));
            holder.tvCourse.setText(grade.getString("course"));
            holder.tvScore.setText(String.valueOf(grade.getDouble("score")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return grades.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCourse, tvScore;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_grade_title);
            tvCourse = itemView.findViewById(R.id.tv_grade_course);
            tvScore = itemView.findViewById(R.id.tv_grade_score);
        }
    }
}