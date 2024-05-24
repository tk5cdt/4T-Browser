package com.example.a4tbrowser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.print.PrintJob;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a4tbrowser.adapter.HistoryAdapter;
import com.example.a4tbrowser.model.Websites;
import com.example.a4tbrowser.database.MyDbHandler;
import com.example.a4tbrowser.R;
import com.example.a4tbrowser.databinding.ActivityHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {

    PrintJob printJob;
    Boolean fullScreen = true;
    public static Boolean desktopMode = false;
    public ActivityHistoryBinding bindingHis;
    MyDbHandler myDbHandler = new MyDbHandler(this, null, null, 1);
    List<Websites> lswebsites;
    HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        bindingHis = ActivityHistoryBinding.inflate(getLayoutInflater());


        setContentView(bindingHis.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lswebsites = myDbHandler.getAllUrls();

        if (lswebsites == null) {
            lswebsites = new ArrayList<>();

        }
        historyAdapter = new HistoryAdapter(this, lswebsites, myDbHandler);
        bindingHis.lvHistory.setAdapter(historyAdapter);
        if (lswebsites.size() <= 0) {
            bindingHis.lvHistory.setDivider(null);
        }
        bindingHis.lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = lswebsites.get(position).getUrl();
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
                finish();
            }

        });

    }
}
