package com.example.a4tbrowser.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.a4tbrowser.activity.BookmarkActivity;
import com.example.a4tbrowser.databinding.BookmarkLvItemBinding;
import com.example.a4tbrowser.model.BookmarkEntity;

import java.util.List;

public class LVBookmarkAdapter extends BaseAdapter {
    private Context context;
    private List<BookmarkEntity> bookmarkList;

    public LVBookmarkAdapter(BookmarkActivity bookmarkActivity, List<BookmarkEntity> bookmarkList) {
        this.context = bookmarkActivity;
        this.bookmarkList = bookmarkList;
    }

    @Override
    public int getCount() {
        return bookmarkList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookmarkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookmarkLvItemBinding binding;
        if (convertView == null) {
            binding = BookmarkLvItemBinding.inflate(LayoutInflater.from(context), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (BookmarkLvItemBinding) convertView.getTag();
        }
        BookmarkEntity bookmarkEntity = bookmarkList.get(position);
        if(bookmarkEntity != null) {


            try {
                if (bookmarkList.get(position).getImg().length != 0) {
                    Bitmap icon = BitmapFactory.decodeByteArray(bookmarkList.get(position).getImg(), 0,
                            bookmarkList.get(position).getImg().length);
                    Drawable drawable = new BitmapDrawable(context.getResources(), icon);
                    binding.imgCover.setBackground(drawable);
                }
            } catch (Exception ignored) {

            }
        }
        binding.txtBookmarkName.setText(bookmarkList.get(position).getName());
        binding.txtBookmarkUrl.setText(bookmarkList.get(position).getUrlPath());
        return convertView;
    }
}
