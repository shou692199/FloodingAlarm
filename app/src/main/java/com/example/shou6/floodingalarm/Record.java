package com.example.shou6.floodingalarm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class Record extends AppCompatActivity {

    SharedPreferences sp;
    String recordNameStr, recordTimeStr, recordLevelStr;
    String recordName[], recordTime[], recordLevel[];

    Toolbar toolbar;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_record);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.listView);

        sp = getSharedPreferences("F_alarm_Set", MODE_WORLD_WRITEABLE);
        recordNameStr = sp.getString("recordName", "");
        recordTimeStr = sp.getString("recordTime", "");
        recordLevelStr = sp.getString("recordLevel", "");

        recordName = recordNameStr.split(",");
        recordTime = recordTimeStr.split(",");
        recordLevel = recordLevelStr.split(",");

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.clear:
                        new AlertDialog.Builder(Record.this)

                                .setIcon(R.mipmap.ic_launcher)
                                .setTitle("清除紀錄？")
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sp.edit()
                                                .putString("recordName", "")
                                                .putString("recordTime", "")
                                                .putString("recordLevel", "")
                                                .commit();

                                        recordNameStr = sp.getString("recordName", "");
                                        recordTimeStr = sp.getString("recordTime", "");
                                        recordLevelStr = sp.getString("recordLevel", "");

                                        recordName = recordNameStr.split(",");
                                        recordTime = recordTimeStr.split(",");
                                        recordLevel = recordLevelStr.split(",");
                                        listView.removeAllViews();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setCancelable(false)
                                .show();
                        break;
                }
                return true;
            }
        });
        if (!recordNameStr.equals("") && !recordTimeStr.equals("") && !recordLevelStr.equals(""))
            listView.setAdapter(new MyAdapter2(this, R.layout.my_list_item2, recordName, recordTime, recordLevel));

    }
}
