package com.example.a4tbrowser.activity;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.WebView;

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
import com.example.a4tbrowser.databinding.ActivityMainBinding;
import com.example.a4tbrowser.databinding.MoreFeaturesBinding;
import com.example.a4tbrowser.fragment.BrowseFragment;
import com.example.a4tbrowser.fragment.HomeFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    List<Fragment> fragments = new ArrayList<>();
    public ActivityMainBinding binding;
    PrintJob printJob;
    Boolean fullScreen = true;
    public static Boolean desktopMode = false;

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
    
}