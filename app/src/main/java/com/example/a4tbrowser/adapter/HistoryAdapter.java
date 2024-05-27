package com.example.a4tbrowser.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a4tbrowser.R;
import com.example.a4tbrowser.model.Websites;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Context context;
    private static List<Object> items;
    private static OnDeleteClickListener deleteClickListener;

    public HistoryAdapter(Context context, List<Object> items) {
        this.context = context;
        this.items = items;
    }

    // xóa item
    public interface OnDeleteClickListener {
        void onDeleteClick(Websites website);
    }

    public void setDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    // link url
    public interface OnWebsiteClickListener {
        void onWebsiteClick(Websites website);
    }
    private static OnWebsiteClickListener websiteClickListener;

    public void setWebsiteClickListener(OnWebsiteClickListener listener) {
        this.websiteClickListener = listener;
    }


    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return VIEW_TYPE_DATE;
        }
        return VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_date, parent, false);
            return new DateViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
            return new HistoryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_DATE) {
            ((DateViewHolder) holder).bind((String) items.get(position));
        } else {
            HistoryViewHolder historyViewHolder = (HistoryViewHolder) holder;
            historyViewHolder.bind((Websites) items.get(position));
            historyViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = historyViewHolder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        Websites websiteToDelete = (Websites) items.get(adapterPosition);
                        deleteClickListener.onDeleteClick(websiteToDelete);
                    }
                }
            });

            historyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = historyViewHolder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION && websiteClickListener != null) {
                        Websites website = (Websites) items.get(adapterPosition);
                        websiteClickListener.onWebsiteClick(website);
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDate;

        DateViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        void bind(String date) {
            tvDate.setText(date);
        }
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUrl, tvTime;
        ImageView ivIcon;
        ImageButton btnDelete;

        HistoryViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_tilte);
            tvUrl = itemView.findViewById(R.id.tv_url);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivIcon = itemView.findViewById(R.id.image);
            btnDelete = itemView.findViewById(R.id.btndelete);

        }

        void bind(Websites website) {
            tvTitle.setText(website.getTitle());
            tvUrl.setText(website.getUrl());
            tvTime.setText(website.getTimee());
            // Set icon if available
            if (website.getImage() != null && website.getImage().length > 0) {
                Bitmap icon = BitmapFactory.decodeByteArray(website.getImage(), 0, website.getImage().length);
                ivIcon.setImageBitmap(icon);
            } else {
                ivIcon.setImageResource(R.drawable.internet);
            }
        }

    }
}
