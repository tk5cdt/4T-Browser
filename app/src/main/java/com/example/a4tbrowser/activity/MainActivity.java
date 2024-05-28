package com.example.a4tbrowser.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
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
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.a4tbrowser.R;
import com.example.a4tbrowser.adapter.TabAdapter;
import com.example.a4tbrowser.database.DB_History;
import com.example.a4tbrowser.database.MyDatabase;
import com.example.a4tbrowser.databinding.ActivityMainBinding;
import com.example.a4tbrowser.databinding.BookmarkDialogBinding;
import com.example.a4tbrowser.databinding.MoreFeaturesBinding;
import com.example.a4tbrowser.databinding.TabsViewBinding;
import com.example.a4tbrowser.fragment.BrowseFragment;
import com.example.a4tbrowser.fragment.HomeFragment;
import com.example.a4tbrowser.model.BookmarkEntity;
import com.example.a4tbrowser.model.Tab;
import com.example.a4tbrowser.model.Websites;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static List<Fragment> fragments = new ArrayList<>();
    public List<BookmarkEntity> bookmarkList = new ArrayList<>();
    public static ArrayList<Tab> tabsList = new ArrayList<>();
    public ActivityMainBinding binding;
    PrintJob printJob;
    Boolean fullScreen = true;
    public static Boolean desktopMode = false;

    //Khai bao cho refresh
    private SwipeRefreshLayout swipeRefreshLayout;
    private WebView webView;
    public int bookmarkIndex = -1;
    List<Websites> lswebsites;

    public static ViewPager2 myPager;
    public static MaterialTextView tabsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rcview_his), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        if(getIntent().getStringExtra("url") != null){
            Bundle bundle = new Bundle();
            BrowseFragment fragment = new BrowseFragment(bundle.getString("url", getIntent().getStringExtra("url")));
            fragment.setArguments(bundle);
            fragments.add(fragment);
            binding.myPager.setCurrentItem(fragments.size() - 1);
        }


        tabsList.add(new Tab("Home", new HomeFragment()));
        binding.myPager.setAdapter(new TabAdapter(getSupportFragmentManager(), getLifecycle()));
        binding.myPager.setUserInputEnabled(false);
        fragments.add(new HomeFragment());

        myPager = binding.myPager;
        tabsBtn=binding.tabsBtn;

        initView();
        changeFullScreen(true);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void handleOnBackPressed() {
//                BrowseFragment fragment;
//                try {
//                    fragment = (BrowseFragment) fragments.get(binding.myPager.getCurrentItem());
//                    if (fragment.binding.webView.canGoBack()) {
//                        fragment.binding.webView.goBack();
//                        return;
//                    }
//                } catch (Exception ignored) {
//                }
//                if (binding.myPager.getCurrentItem() == 0) {
//                    finish();
//                } else {
//                    fragments.remove(binding.myPager.getCurrentItem());
//                    Objects.requireNonNull(binding.myPager.getAdapter()).notifyDataSetChanged();
//                    binding.myPager.setCurrentItem(fragments.size() - 1);
//                }
//            }
                Fragment frag;
                try {
                    frag = tabsList.get(binding.myPager.getCurrentItem()).getFragment()      ;
                } catch (Exception e) {
                    return;
                }

                if (frag != null && frag.getView() != null && ((BrowseFragment) frag).binding.webView.canGoBack()) {
                    ((BrowseFragment) frag).binding.webView.goBack();
                    return;
                }

                if (binding.myPager.getCurrentItem() == 0) {
                    finish();
                } else {
                    tabsList.remove(binding.myPager.getCurrentItem());
                    binding.myPager.getAdapter().notifyDataSetChanged();
                    binding.myPager.setCurrentItem(tabsList.size() - 1);
                }
            }
        });
    }

    // Phương thức kiểm tra kết nối internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
        }
        return false;
    }
    // Phương thức thông báo
    public void showMessage(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }


    private static class TabAdapter extends FragmentStateAdapter {
        public TabAdapter(@NonNull FragmentManager fragmentManager, Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return tabsList.get(position).getFragment();
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeTab(String url, Fragment fragment) {
        fragments.add(fragment);
        tabsList.add(new Tab(url, fragment));
        Objects.requireNonNull(binding.myPager.getAdapter()).notifyDataSetChanged();
        //binding.myPager.setCurrentItem(fragments.size() - 1);
        binding.myPager.setCurrentItem(tabsList.size() - 1);
        tabsBtn.setText(String.valueOf(tabsList.size()));
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
        binding.tabsBtn.setOnClickListener(v -> {
            View viewTabs = getLayoutInflater().inflate(R.layout.tabs_view, binding.getRoot(), false);
            TabsViewBinding bindingTabs = TabsViewBinding.bind(viewTabs);

            MaterialAlertDialogBuilder dialogTabsBuilder = new MaterialAlertDialogBuilder(this, R.style.roundCornerDialog)
                    .setView(viewTabs)
                    .setTitle("Select Tab")
                    .setPositiveButton("Home", (self, which) -> {
                        changeTab("Home", new HomeFragment());
                        self.dismiss();
                    })
                    .setNeutralButton("Google", (self, which) -> {
                        changeTab("Google", new BrowseFragment("www.google.com"));
                        self.dismiss();
                    });

            AlertDialog dialogTabs = dialogTabsBuilder.create();

            bindingTabs.tabsRV.setHasFixedSize(true);
            bindingTabs.tabsRV.setLayoutManager(new LinearLayoutManager(this));
            bindingTabs.tabsRV.setAdapter(new com.example.a4tbrowser.adapter.TabAdapter(this, dialogTabs));

            dialogTabs.show();

            Button pBtn = dialogTabs.getButton(AlertDialog.BUTTON_POSITIVE);
            Button nBtn = dialogTabs.getButton(AlertDialog.BUTTON_NEUTRAL);

            pBtn.setAllCaps(false);
            nBtn.setAllCaps(false);

            pBtn.setTextColor(Color.BLACK);
            nBtn.setTextColor(Color.BLACK);

            pBtn.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_home, getTheme()), null, null, null);
            nBtn.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_add, getTheme()), null, null, null);
        });
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
//                        BrowseFragment frag = (BrowseFragment) fragments.get(binding.myPager.getCurrentItem());
//                        String tilte = frag.binding.webView.getTitle();
//                        String url = frag.binding.webView.getUrl();
//                        String time = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//                        byte[] image = frag.image();
//
//                        frag.save(url, tilte, time, image);
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
//                                String tilte = frag.binding.webView.getTitle();
//                                String url = frag.binding.webView.getUrl();
//                                String time = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//                                byte[] image = frag.image();
//
//                                frag.save(url, tilte, time, image);

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
                dialogBinding.btnRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BrowseFragment fragment = null;
                        try {
                            fragment = (BrowseFragment) fragments.get(binding.myPager.getCurrentItem());
                        } catch (Exception ignored) {
                        }
                        if (fragment != null && fragment.binding.webView != null) {
                            if (isNetworkAvailable()) {
                                fragment.binding.webView.loadUrl(fragment.binding.webView.getUrl());
                                dialog.dismiss();
                            } else {
                                showMessage("No internet connection");
                                dialog.dismiss();
                            }
                        }
                    }
                });
                dialogBinding.btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the current BrowseFragment
                        BrowseFragment fragment = null;
                        try {
                            fragment = (BrowseFragment) fragments.get(binding.myPager.getCurrentItem());
                        } catch (Exception ignored) {
                            // Handle potential exceptions
                        }

                        // Check if the fragment exists and has a webView
                        if (fragment != null && fragment.binding.webView != null) {
                            // Get the current URL of the webView
                            String urlToShare = fragment.binding.webView.getUrl();

                            // Create an Intent with the URL action and set the URL as data
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

                            // Use the Chooser to show available sharing options
                            Intent chooserIntent = Intent.createChooser(intent, "Share this link");
                            startActivity(chooserIntent);
                        }
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

                dialogBinding.btnHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), History.class);
                        startActivity(intent);
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