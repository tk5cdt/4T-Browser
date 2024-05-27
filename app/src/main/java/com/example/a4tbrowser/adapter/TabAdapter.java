//package com.example.a4tbrowser.adapter;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.a4tbrowser.R;
//import com.example.a4tbrowser.activity.MainActivity;
//import com.example.a4tbrowser.databinding.TabBinding;
//import com.example.a4tbrowser.fragment.BrowseFragment;
//import com.example.a4tbrowser.model.BookmarkEntity;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.imageview.ShapeableImageView;
//import com.google.android.material.snackbar.Snackbar;
//import com.google.android.material.textview.MaterialTextView;
//
//import java.util.List;
//import java.util.logging.Handler;
//
//    public class TabAdapter extends RecyclerView.Adapter<TabAdapter.MyHolder> {
//
//        private final Context context;
//        private final AlertDialog dialog;
//
//        public TabAdapter(@NonNull Context context, @NonNull androidx.appcompat.app.AlertDialog dialog) {
//            this.context = context;
//            this.dialog = dialog;
//        }
//
//
//        @NonNull
//        @Override
//        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            TabBinding binding = TabBinding.inflate(LayoutInflater.from(context), parent, false);
//            return new MyHolder(binding);
//        }
//
//        @SuppressLint("NotifyDataSetChanged")
//        @Override
//        public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {
//            int adapterPosition = holder.getAdapterPosition();
//            if (adapterPosition != RecyclerView.NO_POSITION) { // Check for valid position
//                holder.name.setText(MainActivity.tabList.get(adapterPosition).getName());
//            }
//            holder.root.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    MainActivity.myPager.setCurrentItem(position);
//                    dialog.dismiss();
//                }
//            });
//
//            holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (MainActivity.tabList.size() == 1 || position == MainActivity.myPager.getCurrentItem()) {
//                        Snackbar.make(MainActivity.myPager, "Can't Remove this tab", Snackbar.LENGTH_SHORT).show();
//                    } else {
//                        MainActivity.tabList.remove(position);
//                        notifyDataSetChanged();
//                        MainActivity.myPager.getAdapter().notifyItemRemoved(position);
//                    }
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return MainActivity.tabList.size();
//        }
//
//        public static class MyHolder extends RecyclerView.ViewHolder {
//
//            private final View root;
//            private final ShapeableImageView cancelBtn;
//            private final TextView name;
//
//            public MyHolder(TabBinding binding) {
//                super(binding.getRoot());
//                root = binding.getRoot();
//                cancelBtn = binding.cancelBtn;
//                name = binding.tabName;
//            }
//        }
//    }
//
