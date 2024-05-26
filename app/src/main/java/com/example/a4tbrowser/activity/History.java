package com.example.a4tbrowser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.print.PrintJob;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.a4tbrowser.adapter.his_adapter;
import com.example.a4tbrowser.database.DB_History;
import com.example.a4tbrowser.model.Websites;
import com.example.a4tbrowser.R;
import com.example.a4tbrowser.databinding.ActivityHistoryBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class History extends AppCompatActivity {

    PrintJob printJob;
    Boolean fullScreen = true;
    public static Boolean desktopMode = false;
    public ActivityHistoryBinding bindingHis;
    List<Websites> lswebsites;
//    HistoryAdapter historyAdapter;
    public his_adapter hisAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        bindingHis = ActivityHistoryBinding.inflate(getLayoutInflater());


        setContentView(bindingHis.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvHistory), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        lswebsites = DB_History.getDatabase(this).historyDAO().getAllHistory();

        if (lswebsites == null) {
            lswebsites = new ArrayList<>();
        }
//        historyAdapter = new HistoryAdapter(this, lswebsites);
//        bindingHis.lvHistory.setAdapter(historyAdapter);
//
//
//        if (lswebsites.size() <= 0) {
//            // Hiển thị 1 textview Today trước khi load lvhistory
//            bindingHis.lvHistory.setDivider(null);
//        }

        List<Object> items = prepareHistoryData(lswebsites);
        hisAdapter = new his_adapter(this, items);
        bindingHis.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bindingHis.recyclerView.setAdapter(hisAdapter);

//        bindingHis.items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String url = lswebsites.get(position).getUrl();
//                Intent intent = new Intent(view.getContext(), MainActivity.class);
//                intent.putExtra("url", url);
//                startActivity(intent);
//                finish();
//            }
//
//        });

        hisAdapter.setWebsiteClickListener(new his_adapter.OnWebsiteClickListener() {
            @Override
            public void onWebsiteClick(Websites website) {
                String url = website.getUrl();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
                finish();
            }
        });


        hisAdapter.setDeleteClickListener(new his_adapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(Websites website) {
                DB_History.getDatabase(getApplicationContext()).historyDAO().delete(website.getUrl(), website.getTitle(), website.getTimee());
                items.remove(website);
                hisAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();

            }
        });
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
}
