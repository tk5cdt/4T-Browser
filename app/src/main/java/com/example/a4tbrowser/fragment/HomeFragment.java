package com.example.a4tbrowser.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.a4tbrowser.R;
import com.example.a4tbrowser.activity.MainActivity;
import com.example.a4tbrowser.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.bind(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        MainActivity activity = (MainActivity) requireActivity();

        activity.binding.topSearchBar.setText(null);
        binding.searchView.setQuery(null, false);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(activity.checkForInternet(requireContext()))
                    activity.changeTab(query, new BrowseFragment(query));
                else
                    Snackbar.make(binding.getRoot(), "No internet connection", Snackbar.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        activity.binding.goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.binding.topSearchBar.getText() == null)
                    return;
                if (activity.checkForInternet(requireContext()))
                    activity.changeTab(Objects.requireNonNull(activity.binding.topSearchBar.getText()).toString(), new BrowseFragment(activity.binding.topSearchBar.getText().toString()));
                else
                    Snackbar.make(binding.getRoot(), "No internet connection", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}