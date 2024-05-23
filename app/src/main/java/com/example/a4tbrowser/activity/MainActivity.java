package com.example.a4tbrowser.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.a4tbrowser.R;
import com.example.a4tbrowser.database.MyDatabase;
import com.example.a4tbrowser.databinding.ActivityMainBinding;
import com.example.a4tbrowser.databinding.BookmarkDialogBinding;
import com.example.a4tbrowser.databinding.MoreFeaturesBinding;
import com.example.a4tbrowser.fragment.BrowseFragment;
import com.example.a4tbrowser.fragment.HomeFragment;
import com.example.a4tbrowser.model.BookmarkEntity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    List<Fragment> fragments = new ArrayList<>();
    public List<BookmarkEntity> bookmarkList = new ArrayList<>();
    public ActivityMainBinding binding;
    PrintJob printJob;
    Boolean fullScreen = true;
    public static Boolean desktopMode = false;

    public int bookmarkIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.myPager.setAdapter(new TabAdapter(getSupportFragmentManager(), getLifecycle()));
        binding.myPager.setUserInputEnabled(false);
        fragments.add(new HomeFragment());
        initView();
        changeFullScreen(true);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void handleOnBackPressed() {
                BrowseFragment fragment;
                try {
                    fragment = (BrowseFragment) fragments.get(binding.myPager.getCurrentItem());
                    if (fragment.binding.webView.canGoBack()) {
                        fragment.binding.webView.goBack();
                        return;
                    }
                } catch (Exception ignored) {
                }
                if (binding.myPager.getCurrentItem() == 0) {
                    finish();
                } else {
                    fragments.remove(binding.myPager.getCurrentItem());
                    Objects.requireNonNull(binding.myPager.getAdapter()).notifyDataSetChanged();
                    binding.myPager.setCurrentItem(fragments.size() - 1);
                }
            }
        });
    }

    private class TabAdapter extends FragmentStateAdapter {
        public TabAdapter(@NonNull FragmentManager fragmentManager, Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeTab(String url, Fragment fragment) {
        fragments.add(fragment);
        Objects.requireNonNull(binding.myPager.getAdapter()).notifyDataSetChanged();
        binding.myPager.setCurrentItem(fragments.size() - 1);
    }

    @SuppressLint("ObsoleteSdkInt")
    public boolean checkForInternet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
        boolean isAvailable = false;

        if (capabilities!= null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                isAvailable = true;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                isAvailable = true;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                isAvailable = true;
            }
        }
        return isAvailable;
    }

    void initView(){

        binding.settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getLayoutInflater().inflate(R.layout.more_features, binding.getRoot(), false);
                @NonNull MoreFeaturesBinding dialogBinding = MoreFeaturesBinding.bind(view);
                AlertDialog dialog = new MaterialAlertDialogBuilder(MainActivity.this).setView(view).create();
                Objects.requireNonNull(dialog.getWindow()).getAttributes().gravity = Gravity.BOTTOM;
                Objects.requireNonNull(dialog.getWindow()).getAttributes().y = 50;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
                dialog.show();

                if(fullScreen){
                    dialogBinding.btnFullscreen.setIconTintResource(R.color.cool_blue);
                    dialogBinding.btnFullscreen.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.cool_blue));
                    dialogBinding.btnFullscreen.setIconResource(R.drawable.baseline_fullscreen_exit_24);
                }

                if(desktopMode){
                    dialogBinding.btnDesktop.setIconTintResource(R.color.cool_blue);
                    dialogBinding.btnDesktop.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.cool_blue));
                }

                dialogBinding.btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getOnBackPressedDispatcher().onBackPressed();
                    }
                });
                dialogBinding.btnForward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BrowseFragment frag = null;
                        try {
                            frag = (BrowseFragment) fragments.get(binding.myPager.getCurrentItem());
                            if (frag.binding.webView.canGoForward()) {
                                frag.binding.webView.goForward();
                            }
                        }catch (Exception ignored) {}
                    }
                });
                dialogBinding.btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        BrowseFragment frag = null;
                        try {
                            frag = (BrowseFragment) fragments.get(binding.myPager.getCurrentItem());
                            saveAsPdf(frag.binding.webView);
                        }catch (Exception e) {
                            Snackbar.make(binding.getRoot(), "Error saving as PDF", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                dialogBinding.btnFullscreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(fullScreen){
                            changeFullScreen(false);
                            dialogBinding.btnFullscreen.setIconTintResource(R.color.black);
                            dialogBinding.btnFullscreen.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                            dialogBinding.btnFullscreen.setIconResource(R.drawable.baseline_fullscreen_24);
                            fullScreen = false;
                        }else{
                            changeFullScreen(true);
                            dialogBinding.btnFullscreen.setIconTintResource(R.color.cool_blue);
                            dialogBinding.btnFullscreen.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.cool_blue));
                            dialogBinding.btnFullscreen.setIconResource(R.drawable.baseline_fullscreen_exit_24);
                            fullScreen = true;
                        }
                    }
                });
                dialogBinding.btnDesktop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BrowseFragment frag = null;
                        try {
                            frag = (BrowseFragment) fragments.get(binding.myPager.getCurrentItem());
                            if(desktopMode){
                                frag.binding.webView.getSettings().setUserAgentString(null);
                                dialogBinding.btnDesktop.setIconTintResource(R.color.black);
                                dialogBinding.btnDesktop.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                                desktopMode = false;
                            }else{
                                frag.binding.webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:125.0) Gecko/20100101 Firefox/125.0");
                                frag.binding.webView.getSettings().setUseWideViewPort(true);
                                frag.binding.webView.evaluateJavascript("document.querySelector('meta[name=\"viewport\"]').setAttribute('content'," +
                                        " 'width=1024px, initial-scale=' + (document.documentElement.clientWidth / 1024));", null);
                                frag.binding.webView.reload();
                                dialogBinding.btnDesktop.setIconTintResource(R.color.cool_blue);
                                dialogBinding.btnDesktop.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.cool_blue));
                                desktopMode = true;
                            }
                            frag.binding.webView.reload();
                            dialog.dismiss();
                        }catch (Exception ignored) {}
                    }
                });
                // Ton
                dialogBinding.btnBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /*// lưu trữ bookmark vào room database
                        String name = binding.topSearchBar.getText().toString();
                        // lấy url từ webView của fragment đang mở
                        String url = binding.topSearchBar.getText().toString();

                        try {
                            BrowseFragment frag = (BrowseFragment) fragments.get(binding.myPager.getCurrentItem());
                            name = frag.binding.webView.getTitle();
                            url = frag.binding.webView.getUrl();
                        }catch (Exception ignored) {}
                        if(name.isEmpty() || url.isEmpty()){
                            Snackbar.make(binding.getRoot(), "Please wait for the page to load", Snackbar.LENGTH_SHORT).show();
                        }
                        BookmarkEntity bookmarkEntity = new BookmarkEntity(name, url);
                        MyDatabase.getDatabase(MainActivity.this).bDAO().insertBookmark(bookmarkEntity);
                        Snackbar.make(binding.getRoot(), "Bookmark added", Snackbar.LENGTH_SHORT).show();*/
                        BrowseFragment frag = (BrowseFragment) fragments.get(binding.myPager.getCurrentItem());
                        if(bookmarkIndex == -1)
                        {
                            View vb = getLayoutInflater().inflate(R.layout.bookmark_dialog, binding.getRoot(), false);
                            BookmarkDialogBinding binding = BookmarkDialogBinding.bind(vb);
                            Dialog dialog = new MaterialAlertDialogBuilder(MainActivity.this).setView(vb).create();
                            binding.bookmarkMessage.setText("Url"+ frag.binding.webView.getUrl());

                            /*try {
                            }catch (Exception e){}*/
                            openDialog(Gravity.CENTER);
                        }
                    }
                });
            }
        });
    }

    private void changeFullScreen(boolean b) {
        if(b){
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
            new WindowInsetsControllerCompat(getWindow(), binding.getRoot()).hide(WindowInsetsCompat.Type.systemBars());
            new WindowInsetsControllerCompat(getWindow(), binding.getRoot()).setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
            new WindowInsetsControllerCompat(getWindow(), binding.getRoot()).show(WindowInsetsCompat.Type.systemBars());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(printJob != null){
            if(printJob.isCompleted()){
                Snackbar.make(binding.getRoot(), "Successfully save " + printJob.getInfo().getLabel(), Snackbar.LENGTH_SHORT).show();
            }else if(printJob.isFailed()){
                Snackbar.make(binding.getRoot(), "Failed to save " + printJob.getInfo().getLabel(), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void saveAsPdf(WebView webView) throws MalformedURLException {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        String jobName;
        jobName = new URL(webView.getUrl()).getHost() + "_" + new SimpleDateFormat("HH:mm:ss d_MMM_yy").format(Calendar.getInstance().getTime());
        webView.createPrintDocumentAdapter(jobName);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);
        PrintAttributes printAttributes = new PrintAttributes.Builder().build();
        printJob = printManager.print(jobName, printAdapter, printAttributes);
    }

    private void openDialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bookmark_dialog);


        Window window = dialog.getWindow();
        if(window == null)
        {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        TextView bookmarkMessage = dialog.findViewById(R.id.bookmark_message);
        String url="";
        try {
            BrowseFragment frag = (BrowseFragment) fragments.get(binding.myPager.getCurrentItem());
            url = frag.binding.webView.getUrl();
        }catch (Exception ignored) {}

        // Set the URL on the TextView
        bookmarkMessage.setText(url);

        if(Gravity.CENTER == gravity)
        {
            dialog.setCancelable(true);
        }
        else
            dialog.setCancelable(false);

        EditText edtName = dialog.findViewById(R.id.bookmark_title);
        Button btnAdd = dialog.findViewById(R.id.btn_add);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
                // lưu trữ bookmark vào room database
                String name="";
                name = edtName.getText().toString();

                // lấy url từ webView của fragment đang mở
                /*String url="";
                try {
                    BrowseFragment frag = (BrowseFragment) fragments.get(binding.myPager.getCurrentItem());
                    //name = frag.binding.webView.getTitle();
                    url = frag.binding.webView.getUrl();
                }catch (Exception ignored) {}*/
                BookmarkEntity bookmarkEntity;
                // lấy hình ảnh
                try {

                    ByteArrayOutputStream a = new ByteArrayOutputStream();
                    final Bitmap icon = ((BrowseFragment) fragments.get(binding.myPager.getCurrentItem())).favicon;
                    if(icon != null) {
                        icon.compress(Bitmap.CompressFormat.PNG, 100, a);
                    }
                    if(name.isEmpty() || bookmarkMessage.getText().toString().isEmpty()){
                        Snackbar.make(binding.getRoot(), "Please wait for the page to load", Snackbar.LENGTH_SHORT).show();
                    }
                    bookmarkEntity = new BookmarkEntity(name, bookmarkMessage.getText().toString(), a.toByteArray());
                }
                catch (Exception e)
                {
                    bookmarkEntity = new BookmarkEntity(name, bookmarkMessage.getText().toString(), null);
                }
                MyDatabase.getDatabase(MainActivity.this).bDAO().insertBookmark(bookmarkEntity);
                Snackbar.make(binding.getRoot(), "Bookmark added", Snackbar.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
    public int isBookmark(String url){
        // duyệt các bookmark trong list bookmark
        for(int i = 0; i < bookmarkList.size(); i++){
            if(bookmarkList.get(i).getUrlPath() == url){
                return i;
            }
        }
        return -1;
    }

}