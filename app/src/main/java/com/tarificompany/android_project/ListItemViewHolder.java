package com.tarificompany.android_project;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView tvSender;
    private final TextView tvSubject;
    private final TextView tvTimestamp;

    public ListItemViewHolder(@NonNull View itemView) {
        super(itemView);
        tvSender = itemView.findViewById(R.id.tv_detail_sender);
        tvSubject = itemView.findViewById(R.id.tv_detail_subject);
        tvTimestamp = itemView.findViewById(R.id.tv_detail_timestamp);
    }

    public void bind(Message message) {
        tvSender.setText(message.getSender());
        tvSubject.setText(message.getPreview());
        tvTimestamp.setText(message.getFormattedTimestamp());
    }
}
