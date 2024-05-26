//package com.example.a4tbrowser.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.example.a4tbrowser.R;
//import com.example.a4tbrowser.activity.BookmarkActivity;
//import com.example.a4tbrowser.activity.History;
//import com.example.a4tbrowser.activity.MainActivity;
//import com.example.a4tbrowser.database.DB_History;
//import com.example.a4tbrowser.databinding.ActivityHistoryBinding;
//import com.example.a4tbrowser.databinding.ItemHistoryBinding;
//import com.example.a4tbrowser.model.BookmarkEntity;
//import com.example.a4tbrowser.model.Websites;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class HistoryAdapter extends BaseAdapter {
//
//    private Context context;
//    private List<Websites> lswebsite;
//    ItemHistoryBinding binding;
//    public HistoryAdapter(History history, List<Websites> lswebsite) {
//        this.context = history;
//        this.lswebsite = lswebsite;
//    }
//
//    @Override
//    public int getCount() {
//        return lswebsite.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return lswebsite.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        if (convertView == null) {
//            binding = ItemHistoryBinding.inflate(LayoutInflater.from(context), parent, false);
//            convertView = binding.getRoot();
//            convertView.setTag(binding);
//        } else {
//            binding = (ItemHistoryBinding) convertView.getTag();
//            }
//
//        Websites web = lswebsite.get(position);
//        if (web != null) {
//            try {
//                if (web.getImage() != null && web.getImage().length != 0) {
//                    Bitmap icon = BitmapFactory.decodeByteArray(web.getImage(), 0, web.getImage().length);
//                    binding.image.setImageBitmap(icon);
//                } else {
//                    binding.image.setImageResource(R.drawable.ic_launcher_background);
//                }
//            } catch (Exception e) {
//                Log.e("Adapter", "Error setting image bitmap", e);
//                binding.image.setImageResource(R.drawable.ic_launcher_background);
//            }
//            binding.tvTilte.setText(web.getTitle());
//            binding.tvUrl.setText(web.getUrl());
//            binding.tvTime.setText(web.getTimee());
//
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, MainActivity.class);
//                    intent.putExtra("url", web.getUrl());
//                    context.startActivity(intent);
//                }
//            });
//            binding.btndelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    lswebsite.remove(position);
//                    DB_History.getDatabase(context).historyDAO().delete(web.getUrl(), web.getTitle(), web.getTimee());
//                    notifyDataSetChanged();
//                }
//            });
//        } else {
//            Log.d("Adapter", "Web object is null");
//        }
//        return convertView;
//    }
//
//
//
//}
