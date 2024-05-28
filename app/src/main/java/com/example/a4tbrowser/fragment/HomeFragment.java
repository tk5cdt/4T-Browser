package com.example.a4tbrowser.fragment;

import static com.example.a4tbrowser.activity.MainActivity.myPager;
import static com.example.a4tbrowser.activity.MainActivity.tabsBtn;
import static com.example.a4tbrowser.activity.MainActivity.tabsList;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.a4tbrowser.R;
import com.example.a4tbrowser.activity.BookmarkActivity;
import com.example.a4tbrowser.activity.MainActivity;
import com.example.a4tbrowser.adapter.RVBookmarkAdapter;
import com.example.a4tbrowser.database.MyDatabase;
import com.example.a4tbrowser.databinding.FragmentHomeBinding;
import com.example.a4tbrowser.model.BookmarkEntity;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    RVBookmarkAdapter RVBookmarkAdapter;
    private List<BookmarkEntity> listBookmark;

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

        tabsBtn.setText(String.valueOf(tabsList.size()));


            tabsBtn.setText(String.valueOf(tabsList.size()));
            MainActivity.tabsList.get(myPager.getCurrentItem()).setName("Home");




        activity.binding.topSearchBar.setText(null);
        activity.binding.webIcon.setImageResource(R.drawable.baseline_search_24);

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
        //TON
        // hiển thị list dữ liệu từ database lên recyclerview trong homefragment
        listBookmark = MyDatabase.getDatabase(this.binding.rvBookmark.getContext()).bDAO().getListBookmark();
        RVBookmarkAdapter = new RVBookmarkAdapter(requireContext(),listBookmark);
        binding.rvBookmark.setAdapter(RVBookmarkAdapter);
        binding.rvBookmark.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        if(activity.bookmarkList.size()<1)
            binding.viewAllbtn.setVisibility(View.VISIBLE);
        binding.viewAllbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), BookmarkActivity.class);
                startActivity(intent);
            }
        });
    }
}