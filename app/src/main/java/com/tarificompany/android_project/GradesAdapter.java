package com.tarificompany.android_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.GradeViewHolder> {

    private List<GradeItem> gradeList;

    public GradesAdapter(List<GradeItem> gradeList) {
        this.gradeList = gradeList;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grade, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        GradeItem grade = gradeList.get(position);
        holder.tvSubjectName.setText(grade.getSubjectName());
        holder.tvSubjectCode.setText(grade.getSubjectCode());
        holder.tvExamName.setText(grade.getExamName());
        holder.tvScore.setText(String.format("%.2f", grade.getScore()));
        holder.tvPublishedAt.setText("Published: " + grade.getPublishedAt());
    }

    @Override
    public int getItemCount() {
        return gradeList.size();
    }

    public void updateData(List<GradeItem> newGradeList) {
        this.gradeList.clear();
        this.gradeList.addAll(newGradeList);
        notifyDataSetChanged();
    }

    static class GradeViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvSubjectCode, tvExamName, tvScore, tvPublishedAt;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            tvSubjectCode = itemView.findViewById(R.id.tv_subject_code);
            tvExamName = itemView.findViewById(R.id.tv_exam_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvPublishedAt = itemView.findViewById(R.id.tv_published_at);
        }
    }
}