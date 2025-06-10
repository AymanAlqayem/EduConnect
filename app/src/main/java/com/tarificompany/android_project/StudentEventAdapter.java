package com.tarificompany.android_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

public class StudentEventAdapter extends RecyclerView.Adapter<StudentEventAdapter.ViewHolder> {
    private List<JSONObject> events;

    public StudentEventAdapter(List<JSONObject> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject event = events.get(position);
            holder.tvTitle.setText(event.getString("title"));
            holder.tvContent.setText(event.getString("content"));
            holder.tvDate.setText(event.getString("event_date"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_event_title);
            tvContent = itemView.findViewById(R.id.tv_event_content);
            tvDate = itemView.findViewById(R.id.tv_event_date);
        }
    }
}