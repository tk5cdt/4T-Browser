package com.example.a4tbrowser.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.a4tbrowser.R;
import com.example.a4tbrowser.databinding.ActivityMainBinding;
import com.example.a4tbrowser.fragment.BrowseFragment;
import com.example.a4tbrowser.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    List<Fragment> fragments = new ArrayList<>();
    public ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void handleOnBackPressed() {
                BrowseFragment fragment;
                try {
                    fragment = (BrowseFragment) fragments.get(fragments.size() - 1);
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

}