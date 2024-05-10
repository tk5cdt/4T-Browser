package com.example.a4tbrowser.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.a4tbrowser.R;
import com.example.a4tbrowser.activity.MainActivity;
import com.example.a4tbrowser.databinding.FragmentBrowseBinding;

public class BrowseFragment extends Fragment {
    String url;
    public BrowseFragment(String url) {
        this.url = url;
    }
    public FragmentBrowseBinding binding;


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
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                activity.binding.topSearchBar.setText(url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                activity.binding.progressBar.setProgress(0);
                activity.binding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                activity.binding.progressBar.setVisibility(View.GONE);
            }

        });
        binding.webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                try {
                    activity.binding.webIcon.setImageBitmap(icon);
                } catch (Exception ignored) {
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