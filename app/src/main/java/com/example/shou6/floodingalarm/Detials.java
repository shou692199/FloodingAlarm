package com.example.shou6.floodingalarm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by shou6 on 2019/5/19.
 */
public class Detials extends AppCompatActivity implements ValueEventListener {

    String name;
    String ID;
    int battery;
    String IP;
    String MAC;
    int signal;
    String status;
    String level;
    int number;

    Toolbar toolbar;
    TextView textView1, textView2, textView3, textView4, textView5, textView6, textView7, textView8;
    DatabaseReference dr1, dr2, dr3, dr4, dr5, dr6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detials);

        Intent intent = this.getIntent();
        name = intent.getStringExtra("name");
        ID = intent.getStringExtra("ID");

        textView1 = (TextView) findViewById(R.id.textView9);
        textView2 = (TextView) findViewById(R.id.textView10);
        textView3 = (TextView) findViewById(R.id.textView11);
        textView4 = (TextView) findViewById(R.id.textView12);
        textView5 = (TextView) findViewById(R.id.textView13);
        textView6 = (TextView) findViewById(R.id.textView14);
        textView7 = (TextView) findViewById(R.id.textView15);
        textView8 = (TextView) findViewById(R.id.textView16);

        dr1 = FirebaseDatabase.getInstance().getReference().child(ID).child(ID + "_battery");
        dr1.addValueEventListener(this);
        dr2 = FirebaseDatabase.getInstance().getReference().child(ID).child(ID + "_IP");
        dr2.addValueEventListener(this);
        dr3 = FirebaseDatabase.getInstance().getReference().child(ID).child(ID + "_MAC");
        dr3.addValueEventListener(this);
        dr4 = FirebaseDatabase.getInstance().getReference().child(ID).child(ID + "_signal");
        dr4.addValueEventListener(this);
        dr5 = FirebaseDatabase.getInstance().getReference().child(ID).child(ID + "_number");
        dr5.addValueEventListener(this);
        dr6 = FirebaseDatabase.getInstance().getReference().child(ID).child(ID + "_res");
        dr6.addValueEventListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textView1.setText(name);
        textView2.setText(ID);

        checkNetWork();
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        if(key.equals(ID + "_battery")) {
            battery = Integer.parseInt(dataSnapshot.getValue().toString());
            textView3.setText(battery + "%");
        }
        else if(key.equals(ID + "_IP")) {
            IP = dataSnapshot.getValue().toString();
            textView4.setText(IP);
        }
        else if(key.equals(ID + "_MAC")) {
            MAC = dataSnapshot.getValue().toString();
            textView5.setText(MAC);
        }
        else if(key.equals(ID + "_signal")) {
            signal = Integer.parseInt(dataSnapshot.getValue().toString());
            textView6.setText(signal + "%");
        }
        else if(key.equals(ID + "_res")) {
            status = dataSnapshot.getValue().toString();
            if(status.equals("離線"))
                textView7.setTextColor(Color.RED);
            if(status.equals("正常"))
                textView7.setTextColor(Color.BLACK);
            textView7.setText(status);
        }
        else if(key.equals(ID + "_number")) {
            number = Integer.parseInt(dataSnapshot.getValue().toString());
            if(number < 300) {
                level = "無淹水";
                textView8.setTextColor(Color.BLACK);
            }
            if(number >= 300 && number < 600) {
                level = "一級警報";
                textView8.setTextColor(Color.RED);
            }
            if(number >= 600 && number < 900) {
                level = "二級警報";
                textView8.setTextColor(Color.RED);
            }
            if(number >= 900) {
                level = "三級警報";
                textView8.setTextColor(Color.RED);
            }
            textView8.setText(level);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void checkNetWork() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        if (mNetworkInfo == null || !mNetworkInfo.isConnected() || !mNetworkInfo.isAvailable()) {
            textView3.setText("取得中...");
            textView4.setText("取得中...");
            textView5.setText("取得中...");
            textView6.setText("取得中...");
            textView7.setText("取得中...");
            textView8.setText("取得中...");
        }
    }
}
