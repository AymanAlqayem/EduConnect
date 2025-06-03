package com.tarificompany.android_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_LIST_ITEM = 0;
    private static final int TYPE_DETAIL_VIEW = 1;

    private List<Message> messages;
    private final OnMessageClickListener listener;
    private boolean isDetailView = false;

    public interface OnMessageClickListener {
        void onMessageClick(Message message);
    }

    public MessageAdapter(List<Message> messages, OnMessageClickListener listener, boolean isDetailView) {
        this.messages = messages != null ? messages : new ArrayList<>();
        this.listener = listener;
        this.isDetailView = isDetailView;
    }

    @Override
    public int getItemViewType(int position) {
        return isDetailView ? TYPE_DETAIL_VIEW : TYPE_LIST_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_DETAIL_VIEW) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_message_detail, parent, false);
            return new DetailViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message, parent, false);
            return new ListItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder instanceof DetailViewHolder) {
            ((DetailViewHolder) holder).bind(message);
        } else if (holder instanceof ListItemViewHolder) {
            ((ListItemViewHolder) holder).bind(message);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMessageClick(message);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<Message> newMessages) {
        this.messages = newMessages != null ? newMessages : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ListItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSender;
        private final TextView tvPreview;
        private final TextView tvTimestamp;

        public ListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tv_sender);
            tvPreview = itemView.findViewById(R.id.tv_message_preview);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }

        public void bind(Message message) {
            tvSender.setText(message.getSender());
            tvPreview.setText(message.getPreview());
            tvTimestamp.setText(message.getFormattedTimestamp());
        }
    }

    static class DetailViewHolder extends RecyclerView.ViewHolder {
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
}