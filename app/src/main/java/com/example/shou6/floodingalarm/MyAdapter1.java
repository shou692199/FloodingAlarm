package com.example.shou6.floodingalarm;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tony on 2019/2/19.
 */
public class MyAdapter1 extends ArrayAdapter {

    Context context;
    //ArrayList<String> productID;
    ArrayList<String> productName;
    String status[];
    String level[];

    public MyAdapter1(Context context, int resource, ArrayList<String> productName, String[] status, String[] level) {
        super(context, resource, productName);
        this.context = context;
        this.productName = productName;
        this.status = status;
        this.level = level;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.my_list_item1, parent, false);
        TextView textView1 = (TextView) v.findViewById(R.id.textView1);
        textView1.setText(productName.get(position));
        TextView textView2 = (TextView) v.findViewById(R.id.textView2);
        textView2.setText(status[position]);
        if (!status[position].equals("正常") && !status[position].equals("取得中..."))
            textView2.setTextColor(Color.RED);
        else
            textView2.setTextColor(Color.parseColor("#333333"));
        TextView textView3 = (TextView) v.findViewById(R.id.textView3);
        textView3.setText(level[position]);
        if (!level[position].equals("無淹水") && !level[position].equals("取得中..."))
            textView3.setTextColor(Color.RED);
        else
            textView3.setTextColor(Color.parseColor("#333333"));
        return v;
    }
}
