package com.example.a4tbrowser.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
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

    @SuppressLint("SetJavaScriptEnabled")
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
        });
        binding.webView.setWebChromeClient(new WebChromeClient());
        if (URLUtil.isValidUrl(url))
            binding.webView.loadUrl(url);
        else if (url.contains(".com"))
            binding.webView.loadUrl(url);
        else
            binding.webView.loadUrl("https://www.google.com/search?q=" + url);
    }
}