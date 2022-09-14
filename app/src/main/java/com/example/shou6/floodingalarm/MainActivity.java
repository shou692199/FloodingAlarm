package com.example.shou6.floodingalarm;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ValueEventListener {

    int levelScale[] = {200, 600, 900};

    int sum;
    String productIDStr, productNameStr, productVerifyStr;
    ArrayList<String> productID, productName, productVerify;

    DatabaseReference dr1[], dr2[], dr3[];

    String status[];
    int number[];
    String level[];
    String key;
    boolean connect;

    ListView listView;
    Toolbar toolbar;
    SharedPreferences sp;
    FloatingActionButton fab;
    Context context;

    int check1[], check2[];
    //@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        sp = getSharedPreferences("F_alarm_Set", MODE_WORLD_WRITEABLE);

        sum = sp.getInt("sum", 0);
        productIDStr = sp.getString("productID", "");
        productNameStr = sp.getString("productName", "");
        productVerifyStr = sp.getString("productVerify", "");
        sp.edit()
                .putInt("sum", sum)
                .putString("productID", productIDStr)
                .putString("productName", productNameStr)
                .putString("productVerify", productVerifyStr)
                .commit();

        productID = new ArrayList<String>(Arrays.asList(productIDStr.split(",")));
        productName = new ArrayList<String>(Arrays.asList(productNameStr.split(",")));
        productVerify = new ArrayList<String>(Arrays.asList(productVerifyStr.split(",")));

        checkNetWork();

        dr1 = new DatabaseReference[sum];
        dr2 = new DatabaseReference[sum];
        dr3 = new DatabaseReference[sum];

        number = new int[sum];
        status = new String[sum];
        level = new String[sum];

        check1 = new int[sum];
        check2 = new int[sum];

        if(!productIDStr.equals("") && !productNameStr.equals("")) {
            for (int i = 0; i < sum; i++) {
                dr1[i] = FirebaseDatabase.getInstance().getReference().child(productID.get(i)).child(productID.get(i) + "_number");
                dr1[i].addValueEventListener(this);
                dr2[i] = FirebaseDatabase.getInstance().getReference().child(productID.get(i)).child(productID.get(i) + "_check");
                dr2[i].addValueEventListener(this);
                dr3[i] = FirebaseDatabase.getInstance().getReference().child(productID.get(i)).child(productID.get(i) + "_res");
                dr3[i].addValueEventListener(this);
                check1[i] = 0;
                check2[i] = 0;
                status[i] = "取得中...";
                level[i] = "取得中...";

            }
        }

        Intent intent = new Intent(MainActivity.this, AlarmService.class);

        if (!isServiceExisted(this, AlarmService.class.getName()))
            startService(intent);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.record:
                        Intent intent = new Intent(MainActivity.this, Record.class);
                        startActivity(intent);
                        break;
                    case R.id.about:

                        new AlertDialog.Builder(MainActivity.this)
                                .setIcon(R.mipmap.ic_launcher)
                                .setTitle("關於智慧淹水警示系統")
                                .setMessage("開發團隊：蕭澧邦、譚暐霖、黃宥鈞、李知易\n\n指導老師：黃國穎\n\n版本號：3.7\n\n更新日期：2019/5/25")
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
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

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("移除設備？")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setCancelable(false)
                        .show();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Detials.class);
                intent  .putExtra("name", productName.get((int)id))
                        .putExtra("ID", productID.get((int) id));
                startActivity(intent);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View myEditText = LayoutInflater.from(MainActivity.this).inflate(R.layout.my_edit_text, null);
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("新增設備")
                            .setMessage("請輸入產品序號")
                            .setView(myEditText)
                            .setPositiveButton("下一步", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Dialog dialogObj1 = Dialog.class.cast(dialog);
                                    EditText editText1 = (EditText) dialogObj1.findViewById(R.id.editText);
                                    productID.add(editText1.getText().toString());
                                    Log.d("ID", "有");

                                    final View myEditText2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.my_edit_text, null);
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setIcon(R.mipmap.ic_launcher)
                                            .setTitle("新增設備")
                                            .setMessage("請輸入設備名稱")
                                            .setView(myEditText2)
                                            .setPositiveButton("測試設備", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog2, int which) {
                                                    Dialog dialogObj2 = Dialog.class.cast(dialog2);
                                                    EditText editText2 = (EditText) dialogObj2.findViewById(R.id.editText);
                                                    productName.add(editText2.getText().toString());
                                                    productVerify.add("false");
                                                    sum++;

                                                    if (!productIDStr.equals("")) {
                                                        sp.edit()
                                                                .putString("productID", productIDStr+","+productID.get(productID.size()-1))
                                                                .putString("productName", productNameStr+","+productName.get(productName.size()-1))
                                                                .putString("productVerify", productVerifyStr + ",true")
                                                                .putInt("sum", sum)
                                                                .commit();
                                                        productVerify.set(productVerify.size() - 1, "true");
                                                        Intent intent = getIntent();
                                                        finish();
                                                        startActivity(intent);
                                                    } else if(productIDStr.equals("")) {
                                                        sp.edit()
                                                                .putString("productID", productID.get(productID.size() - 1))
                                                                .putString("productName", productName.get(productName.size() - 1))
                                                                .putString("productVerify", "true")
                                                                .putInt("sum", sum)
                                                                .commit();
                                                        productVerify.set(productVerify.size() - 1, "true");
                                                        Intent intent = getIntent();
                                                        finish();
                                                        startActivity(intent);
                                                    }

                                                    if (productVerify.get(productVerify.size() - 1).toString().equals("false")) {
                                                        productVerify.remove(productVerify.size()-1);
                                                        productName.remove(productName.size() - 1);
                                                        productID.remove(productID.size() - 1);
                                                        sum--;
                                                        new AlertDialog.Builder(MainActivity.this)
                                                                .setIcon(R.mipmap.ic_launcher)
                                                                .setTitle("操作失敗")
                                                                .setMessage("序號錯誤")
                                                                .setPositiveButton("確定", null)
                                                                .setCancelable(false)
                                                                .show();
                                                    }
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();

                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setCancelable(false)
                            .show();
            }
        });
        if(!productIDStr.equals("") && !productNameStr.equals("") && !productVerifyStr.equals(""))
            lvUpdate();
        checkConnect();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(!productIDStr.equals("") && !productNameStr.equals("") && !productVerifyStr.equals("") && connect == true) {
            //String key = dataSnapshot.getRef().getParent().getKey();
            key = dataSnapshot.getKey();
            Log.d("key", key);

            for (int i = 0; i < sum; i++) {
                if (key.equals(productID.get(i) + "_number")) {
                    if (check1[i] - check2[i] < 2) {
                        number[i] = Integer.parseInt(dataSnapshot.getValue().toString());
                        if (number[i] < levelScale[0] && !level[i].equals("無淹水")) {
                            level[i] = "無淹水";
                            lvUpdate();
                        }
                        if (number[i] >= levelScale[0] && number[i] < levelScale[1] && !level[i].equals("一級警報")) {
                            level[i] = "一級警報";
                            lvUpdate();
                        }
                        if (number[i] >= levelScale[1] && number[i] < levelScale[2] && !level[i].equals("二級警報")) {
                            level[i] = "二級警報";
                            lvUpdate();
                        }
                        if (number[i] >= levelScale[2] && !level[i].equals("三級警報")) {
                            level[i] = "三級警報";
                            lvUpdate();
                        }
                    }
                }
                if (key.equals(productID.get(i) + "_check")) {
                    check2[i] = check1[i];
                    if (!status[i].equals("正常") || status[i].equals("取得中..."))
                        lvUpdate();
                }
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void checkNetWork() {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        if (mNetworkInfo != null) {
            //網路是否已連線
            mNetworkInfo.isConnected();
            //網路連線方式名稱
            mNetworkInfo.getTypeName();
            //網路連線狀態
            mNetworkInfo.getState();
            //網路是否可使用
            mNetworkInfo.isAvailable();
            //網路是否已連接or連線中
            mNetworkInfo.isConnectedOrConnecting();
            //網路是否故障有問題
            mNetworkInfo.isFailover();
            //網路是否在漫遊模式
            mNetworkInfo.isRoaming();
            //網路詳細狀態
            mNetworkInfo.getDetailedState();
            //網路狀態詳細資訊
            mNetworkInfo.getExtraInfo();
            //網路出錯時的原因回報:
            mNetworkInfo.getReason();
        } else {
            new AlertDialog.Builder(this).setMessage("沒有網路")
                    .setPositiveButton("前往設定網路", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent callNetSettingIntent = new Intent(
                                    android.provider.Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                            Toast.makeText(context, "請前往開啟網路", Toast.LENGTH_LONG).show();
                            startActivity(callNetSettingIntent);
                        }
                    })
                    .show();

        }
    }

    public static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    public void checkConnect() {
        Thread th = new Thread() {
            @Override
            public void run() {
                for (; ; ) {
                    for (int i = 0; i < sum; i++) {
                        check1[i]++;
                        int dif = check1[i] - check2[i];
                        //Log.d("Checking" + i, String.valueOf(dif));
                        if (dif < 2) {    //if (dif < 4) {
                            status[i] = "正常";
                            //Log.d("連線" + i, status[i]);
                            dr3[i].setValue(status[i]);
                        } else {
                            status[i] = "離線";
                            level[i] = "";
                            //Log.d("連線" + i, status[i]);
                            dr3[i].setValue(status[i]);
                        }
                    }
                    try {
                        sleep(1000);     //sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        th.start();
    }

    public void lvUpdate() {
        listView.setAdapter(new MyAdapter1(this, R.layout.my_list_item1, productName, status, level));
    }
}
