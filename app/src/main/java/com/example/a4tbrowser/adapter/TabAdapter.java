package com.example.a4tbrowser.adapter;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a4tbrowser.R;
import com.example.a4tbrowser.activity.MainActivity;
import com.example.a4tbrowser.databinding.TabBinding;
import com.example.a4tbrowser.databinding.TabsViewBinding;
import com.example.a4tbrowser.fragment.BrowseFragment;
import com.example.a4tbrowser.model.BookmarkEntity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.logging.Handler;
public class TabAdapter extends RecyclerView.Adapter<TabAdapter.MyHolder> {

    private final Context context;
    private final AlertDialog dialog;

    public TabAdapter(Context context, AlertDialog dialog) {
        this.context = context;
        this.dialog = dialog;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        public final ShapeableImageView cancelBtn;
        public final TextView name;
        public final View root;

        public MyHolder(TabBinding binding) {
            super( binding.getRoot());
            cancelBtn = binding.cancelBtn;// Assuming cancelBtn has an ID in your layout
            name = binding.tabName; // Assuming tabName has an ID in your layout
            root = binding.getRoot();
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder( TabBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(MainActivity.tabsList.get(position).getName());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.myPager.setCurrentItem(position);
                dialog.dismiss();
            }
        });

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.tabsList.size() == 1 || position == MainActivity.myPager.getCurrentItem()) {
                    Snackbar.make(MainActivity.myPager, "Can't Remove this tab", Snackbar.LENGTH_SHORT).show();
                } else {
                    MainActivity.tabsList.remove(position);
                    notifyDataSetChanged();
                    MainActivity.myPager.getAdapter().notifyItemRemoved(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return MainActivity.tabsList.size();
    }
}
