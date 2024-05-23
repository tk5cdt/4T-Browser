package com.example.a4tbrowser.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a4tbrowser.R;
import com.example.a4tbrowser.activity.MainActivity;
import com.example.a4tbrowser.fragment.BrowseFragment;
import com.example.a4tbrowser.model.BookmarkEntity;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.logging.Handler;

public class RVBookmarkAdapter extends RecyclerView.Adapter<RVBookmarkAdapter.Holder> {
    private Context context;
    Handler mhandler;
    private List<BookmarkEntity> bookmarkList;

    public RVBookmarkAdapter(Context context, List<BookmarkEntity> bookmarkList) {
        this.context = context;
        this.bookmarkList = bookmarkList;
    }

    public RVBookmarkAdapter() {

    }

    public void setBookmarkList(List<BookmarkEntity> bookmarkList) {
        this.bookmarkList = bookmarkList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.bookmark_item_btn, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        BookmarkEntity bookmarkEntity = bookmarkList.get(position);
        if(bookmarkEntity == null)
            return;

        try {
            if(bookmarkList.get(position).getImg().length != 0) {
                Bitmap icon = BitmapFactory.decodeByteArray(bookmarkList.get(position).getImg(), 0,
                        bookmarkList.get(position).getImg().length);
                Drawable drawable = new BitmapDrawable(context.getResources(), icon);
                holder.img_cover.setBackground(drawable);
            }
        }catch (Exception e)
        {

        }
        //holder.img_cover.setText(bookmarkList.get(position).getName().substring(0,1).toUpperCase());
        holder.txt_bookmark_name.setText(bookmarkList.get(position).getUrlPath());

        // Set click listener for the root view
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {if (!isNetworkAvailable(context)) {
                // Show Snackbar indicating no internet connection
                Snackbar snackbar = Snackbar.make(v, "No internet connection", Snackbar.LENGTH_SHORT);
                snackbar.show();
                return; // Prevent further action if no connection
            }
                ((MainActivity)context).changeTab(holder.txt_bookmark_name.getText().toString(),new BrowseFragment(holder.txt_bookmark_name.getText().toString()));


            }
            private boolean isNetworkAvailable(Context context) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            }

        });


        /*try
        {
            Bitmap icon =BitmapFactory.decodeByteArray(bookmarkList.get(position).getUrlPath().getBytes(),0,
                    bookmarkList.get(position).getUrlPath().length());
            holder.img_cover.setImageBitmap(icon);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            holder.img_cover.setImageResource(R.drawable.ic_launcher_background);
        }*/
    }


    @Override
    public int getItemCount() {
        return bookmarkList != null ? bookmarkList.size():0;
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView txt_bookmark_name, img_cover;
        private LinearLayout root;
        public Holder(@NonNull View itemView) {
            super(itemView);
            txt_bookmark_name = itemView.findViewById(R.id.txt_bookmark_name);
            img_cover = itemView.findViewById(R.id.img_cover);
            root = itemView.findViewById(R.id.root);
        }
    }

}
