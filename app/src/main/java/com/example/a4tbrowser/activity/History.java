package com.example.a4tbrowser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.print.PrintJob;
import android.view.View;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.a4tbrowser.adapter.HistoryAdapter;
import com.example.a4tbrowser.database.DB_History;
import com.example.a4tbrowser.model.Websites;
import com.example.a4tbrowser.R;
import com.example.a4tbrowser.databinding.ActivityHistoryBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class History extends AppCompatActivity {

    public ActivityHistoryBinding bindingHis;
    List<Websites> lswebsites;
    public HistoryAdapter hisAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        bindingHis = ActivityHistoryBinding.inflate(getLayoutInflater());

        setContentView(bindingHis.getRoot());
        Intent intent = getIntent();
        boolean fullScreen = intent.getBooleanExtra("full", true);
        if(fullScreen != true){
            changeFullScreen(false);
        }
        else{
            bindingHis.linearLayoutHistory.setPadding(0, 80, 0, 0);

        }
        lswebsites = DB_History.getDatabase(this).historyDAO().getAllHistory();

        if (lswebsites == null) {
            lswebsites = new ArrayList<>();
        }


        // Đảo ngược danh sách để mục mới nhất ở trên cùng
        lswebsites = reverseWebsites(lswebsites);

        List<Object> items = prepareHistoryData(lswebsites);
        hisAdapter = new HistoryAdapter(this, items);
        bindingHis.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bindingHis.recyclerView.setAdapter(hisAdapter);

        hisAdapter.setWebsiteClickListener(new HistoryAdapter.OnWebsiteClickListener() {
            @Override
            public void onWebsiteClick(Websites website) {
                String url = website.getUrl();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
                finish();
            }
        });

        bindingHis.btnBackHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
        hisAdapter.setDeleteClickListener(new HistoryAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(Websites website) {
                DB_History.getDatabase(getApplicationContext()).historyDAO().delete(website.getUrl(), website.getTitle(), website.getTimee());
                items.remove(website);
                hisAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private List<Websites> reverseWebsites(List<Websites> websites) {
        Collections.reverse(websites);
        return websites;
    }
    private List<Object> prepareHistoryData(List<Websites> websites) {
        List<Object> items = new ArrayList<>();
        String lastDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (Websites website : websites) {
            String date = website.getDate();
            if (!date.equals(lastDate)) {
                items.add(date);
                lastDate = date;
            }
            items.add(website);
        }
        return items;
    }
    private void changeFullScreen(boolean b) {
        if(b){
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
            new WindowInsetsControllerCompat(getWindow(), bindingHis.getRoot()).hide(WindowInsetsCompat.Type.systemBars());
            new WindowInsetsControllerCompat(getWindow(), bindingHis.getRoot()).setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
            new WindowInsetsControllerCompat(getWindow(), bindingHis.getRoot()).show(WindowInsetsCompat.Type.systemBars());
        }
    }
}
