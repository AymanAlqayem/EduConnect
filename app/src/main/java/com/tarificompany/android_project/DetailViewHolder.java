package com.tarificompany.android_project;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DetailViewHolder extends RecyclerView.ViewHolder {

    private final TextView tvSender;
    private final TextView tvTimestamp;
    private final TextView tvContent;

    public DetailViewHolder(@NonNull View itemView) {
        super(itemView);
        tvSender = itemView.findViewById(R.id.tv_detail_sender);
        tvTimestamp = itemView.findViewById(R.id.tv_detail_timestamp);
        tvContent = itemView.findViewById(R.id.tv_detail_content);
    }

    public void bind(Message message) {
        tvSender.setText(message.getSender());
        tvTimestamp.setText(message.getFormattedTimestamp());
        tvContent.setText(message.getContent());
    }
}
