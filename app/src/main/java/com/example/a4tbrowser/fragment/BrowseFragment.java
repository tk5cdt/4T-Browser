package com.example.a4tbrowser.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.a4tbrowser.R;
import com.example.a4tbrowser.activity.MainActivity;
import com.example.a4tbrowser.database.DB_History;
import com.example.a4tbrowser.databinding.ActivityMainBinding;
import com.example.a4tbrowser.databinding.FragmentBrowseBinding;
import com.example.a4tbrowser.model.Websites;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BrowseFragment extends Fragment {
    String url;
    public ActivityMainBinding activityMainBinding;
    public BrowseFragment()
    {

    }
    public BrowseFragment(String url) {
        this.url = url;
    }
    public FragmentBrowseBinding binding;
    //favicon
    public Bitmap favicon = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse, container, false);
        binding = FragmentBrowseBinding.bind(view);

        return view;
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) requireActivity();
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.getSettings().setSupportZoom(true);
        binding.webView.getSettings().setBuiltInZoomControls(true);
        binding.webView.getSettings().setDisplayZoomControls(false);
        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                if (MainActivity.desktopMode){
                    view.evaluateJavascript("document.querySelector('meta[name=\"viewport\"]').setAttribute('content'," +
                            " 'width=1024px, initial-scale=' + (document.documentElement.clientWidth / 1024));", null);
                    view.zoomOut();
                }
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                activity.binding.topSearchBar.setText(url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                activity.binding.progressBar.setProgress(0);
                activity.binding.progressBar.setVisibility(View.VISIBLE);

                if(url.contains("you")){
                    activity.binding.getRoot().transitionToEnd();
                }

                isPageSaved = false;
                BrowseFragment.this.favicon = activity.binding.webIcon.getDrawingCache();

            }

            private boolean isPageSaved = false;
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                activity.binding.progressBar.setVisibility(View.GONE);


            }

        });
        binding.webView.setWebChromeClient(new WebChromeClient(){
            private boolean isPageSaved = false;
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                try {
                    Log.d("BrowseFragment", "Received new favicon");
                    activity.binding.webIcon.setImageBitmap(icon);
                    favicon = icon; // Cập nhật favicon mới nhận được

                    if (activity.isBookmark(view.getUrl()) != -1) {
                        activity.bookmarkIndex = activity.isBookmark(view.getUrl());
                    }
                    if (activity.bookmarkIndex != -1) {
                        ByteArrayOutputStream a = new ByteArrayOutputStream();
                        if (icon != null) {
                            icon.compress(Bitmap.CompressFormat.PNG, 100, a);
                            activity.bookmarkList.get(activity.bookmarkIndex).setImg(a.toByteArray());
                        }
                    }

                    if (!isPageSaved) {
                        activity.binding.getRoot().transitionToStart();
                        String url = activity.binding.topSearchBar.getText().toString();
                        String title = view.getTitle();
                        String timee = new SimpleDateFormat("hh:mm", Locale.getDefault()).format(new Date());
                        byte[] image = image();
                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        save(url, title, timee, image, date);
                        isPageSaved = true;
                    }
                }
                catch (Exception ignored) {
                    Log.e("BrowseFragment", "Error receiving favicon", ignored);
                }
            }


            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                binding.webView.setVisibility(View.GONE);
                binding.customView.setVisibility(View.VISIBLE);
                binding.customView.addView(view);
                activity.binding.getRoot().transitionToEnd();
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                binding.webView.setVisibility(View.VISIBLE);
                binding.customView.setVisibility(View.GONE);
                binding.customView.removeAllViews();
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                activity.binding.progressBar.setProgress(newProgress);
            }
        });
        if (URLUtil.isValidUrl(url))
            binding.webView.loadUrl(url);
        else if (url.contains(".com"))
            binding.webView.loadUrl(url);
        else
            binding.webView.loadUrl("https://www.google.com/search?q=" + url);

        binding.webView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                activity.binding.getRoot().onTouchEvent(event);
                return false;
            }
        });
    }

    public void save(String url, String title, String time, byte[] image, String date)
    {
        DB_History.getDatabase(requireContext()).historyDAO().addHistory(new Websites(url, image, title, time, date));

    }
    public void loadIcon(String url)
    {
        if(url.contains("you"))
        {
            MainActivity activity = (MainActivity) requireActivity();
            activity.binding.webIcon.setImageBitmap(favicon);
            activity.binding.webIcon.setVisibility(View.VISIBLE);
            activity.binding.webIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.binding.getRoot().transitionToStart();
                    activity.binding.webIcon.setVisibility(View.GONE);
                    binding.webView.setVisibility(View.VISIBLE);
                }
            });
        }
    }
    public byte[] image() {
        if (BrowseFragment.this.favicon == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        favicon.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    @Override
    public void onPause() {
        super.onPause();
        binding.webView.clearMatches();
        binding.webView.clearHistory();
        binding.webView.clearFormData();
        binding.webView.clearSslPreferences();
        binding.webView.clearCache(true);
        CookieManager.getInstance().removeAllCookies(null);
        WebStorage.getInstance().deleteAllData();

    }
}