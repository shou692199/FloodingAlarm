package com.example.shou6.floodingalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Tony on 2019/2/19.
 */
public class AlarmService extends Service implements ValueEventListener {

    int levelScale[] = {200, 600, 900};

    String message[] = {"一級警報", "二級警報", "三級警報"};
    int iconID[] = {R.drawable.flood_warning_icon0, R.drawable.flood_warning_icon1, R.drawable.flood_warning_icon2};
    Bitmap largeIcon[] = new Bitmap[3];

    SharedPreferences sp;
    int sum;
    String productIDStr, productNameStr;
    ArrayList<String> productID, productName;
    String recordNameStr, recordTimeStr, recordLevelStr;
    int check1[], check2[];

    DatabaseReference dr1[], dr2[];
    int number[];
    NotificationManager manager;
    boolean sent[][];

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        for (int i = 0; i < 3; i++)
            largeIcon[i] = BitmapFactory.decodeResource(getResources(), iconID[i]);

        sp = getSharedPreferences("F_alarm_Set", MODE_WORLD_WRITEABLE);

        sum = sp.getInt("sum", 0);
        productIDStr = sp.getString("productID", "");
        productNameStr = sp.getString("productName", "");

        recordNameStr = sp.getString("recordName", "");
        recordTimeStr = sp.getString("recordTime", "");
        recordLevelStr = sp.getString("recordLevel", "");

        productID = new ArrayList<String>(Arrays.asList(productIDStr.split(",")));
        productName = new ArrayList<String>(Arrays.asList(productNameStr.split(",")));

        dr1 = new DatabaseReference[sum];
        dr2 = new DatabaseReference[sum];
        number = new int[sum];
        sent= new boolean[sum][3];

        check1 = new int[sum];
        check2 = new int[sum];

        if(!productIDStr.equals("") && !productNameStr.equals("")) {
            for (int i = 0; i < sum; i++) {
                dr1[i] = FirebaseDatabase.getInstance().getReference().child(productID.get(i)).child(productID.get(i) + "_number");
                dr1[i].addValueEventListener(this);
                dr2[i] = FirebaseDatabase.getInstance().getReference().child(productID.get(i)).child(productID.get(i) + "_check");
                dr2[i].addValueEventListener(this);
                sent[i][0] = false;
                sent[i][1] = false;
                sent[i][2] = false;
                check1[i] = 0;
                check2[i] = 0;
            }

        }
        if(!productIDStr.equals("") && !productNameStr.equals(""))
            checkConnect();
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(!productIDStr.equals("") && !productNameStr.equals("")) {
            //String key = dataSnapshot.getRef().getParent().getKey();
            String key = dataSnapshot.getKey();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

            Log.d("key", key);

            for (int i = 0; i < sum; i++) {
                if (key.equals(productID.get(i) + "_number")) {
                    if(check1[i] - check2[i] < 2) {
                        number[i] = Integer.parseInt(dataSnapshot.getValue().toString());
                        Log.d("TEST", productName.get(i) + number[i]);
                        if (number[i] < levelScale[0]) {
                            sent[i][0] = false;
                            sent[i][1] = false;
                            sent[i][2] = false;
                        }
                        if (number[i] >= levelScale[0] && number[i] < levelScale[1] && sent[i][0] == false) {
                            Notification notification = new NotificationCompat.Builder(this)
                                    .setLargeIcon(largeIcon[0])
                                    .setSmallIcon(R.drawable.ic_alarm_24dp)
                                    .setContentTitle("淹水了！")
                                    .setContentText(productName.get(i) + "：" + message[0] + "!")          //.setWhen(System.currentTimeMillis())
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                                    .build();
                            manager.notify(3 * i, notification);    //manager.notify(0 + 3 * i, notification);
                            sent[i][0] = true;
                            sent[i][1] = false;
                            sent[i][2] = false;
                            record(productName.get(i), message[0]);
                        }
                        if (number[i] >= levelScale[1] && number[i] < levelScale[2] && sent[i][1] == false) {
                            Notification notification = new NotificationCompat.Builder(this)
                                    .setLargeIcon(largeIcon[1])
                                    .setSmallIcon(R.drawable.ic_alarm_24dp)
                                    .setContentTitle("淹水了！")
                                    .setContentText(productName.get(i) + "：" + message[1] + "!")
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                                    .build();
                            manager.notify(3 * i, notification);   //manager.notify(1 + 3 * i, notification);
                            sent[i][0] = false;
                            sent[i][1] = true;
                            sent[i][2] = false;
                            record(productName.get(i), message[1]);
                        }
                        if (number[i] >= levelScale[2] && sent[i][2] == false) {
                            Notification notification = new NotificationCompat.Builder(this)
                                    .setLargeIcon(largeIcon[2])
                                    .setSmallIcon(R.drawable.ic_alarm_24dp)
                                    .setContentTitle("淹水了！")
                                    .setContentText(productName.get(i) + "：" + message[2] + "!")
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                                    .build();
                            manager.notify(3 * i, notification);    //manager.notify(2 + 3 * i, notification);
                            sent[i][0] = false;
                            sent[i][1] = false;
                            sent[i][2] = true;
                            record(productName.get(i), message[2]);
                        }
                    }
                }
                if (key.equals(productID.get(i) + "_check")) {
                    check2[i] = check1[i];
                }
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    void record(String name, String level) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd\nHH:mm:ss");
        String time = sdf.format(d);
        recordNameStr = name + "," + recordNameStr;
        recordTimeStr = time + "," + recordTimeStr;
        recordLevelStr = level + "," + recordLevelStr;
        sp.edit().putString("recordName", recordNameStr).commit();
        sp.edit().putString("recordTime", recordTimeStr).commit();
        sp.edit().putString("recordLevel", recordLevelStr).commit();
    }

    public void checkConnect() {
        Thread th = new Thread() {
            @Override
            public void run() {
                for (; ; ) {
                    for (int i = 0; i < sum; i++) {
                        check1[i]++;
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
}