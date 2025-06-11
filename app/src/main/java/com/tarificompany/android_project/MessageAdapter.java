package com.tarificompany.android_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_LIST_ITEM = 0;
    private static final int TYPE_DETAIL_VIEW = 1;

    private List<Message> messages;
    private final OnMessageClickListener listener;
    private boolean isDetailView;

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
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_DETAIL_VIEW) {
            View view = inflater.inflate(R.layout.activity_message_detail, parent, false);
            return new DetailViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message, parent, false);
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
                if (!message.isRead()) {
                    // حدث حالة القراءة محليًا
                    message.setRead(true);
                    // استدعاء notifyItemChanged على الـ Adapter، وتمرير موقع العنصر الحالي
                    notifyItemChanged(holder.getAdapterPosition());
                }
                // أخبر الـ Fragment أو الـ Activity عن النقر
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
}
