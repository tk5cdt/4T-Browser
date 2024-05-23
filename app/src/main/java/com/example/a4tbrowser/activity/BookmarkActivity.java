package com.example.a4tbrowser.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a4tbrowser.R;
import com.example.a4tbrowser.adapter.LVBookmarkAdapter;
import com.example.a4tbrowser.database.MyDatabase;
import com.example.a4tbrowser.databinding.ActivityBookmarkBinding;
import com.example.a4tbrowser.databinding.ActivityMainBinding;
import com.example.a4tbrowser.model.BookmarkEntity;

import java.util.List;

public class BookmarkActivity extends AppCompatActivity {

    ActivityBookmarkBinding binding;
    List<BookmarkEntity> bookmarkList;
    LVBookmarkAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookmarkBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bookmarkList = MyDatabase.getDatabase(this).bDAO().getListBookmark();
        adapter = new LVBookmarkAdapter(this, bookmarkList);
        binding.lvBookmark.setAdapter(adapter);
        binding.lvBookmark.setDivider(null);
        binding.lvBookmark.setDividerHeight(0);
    }
}