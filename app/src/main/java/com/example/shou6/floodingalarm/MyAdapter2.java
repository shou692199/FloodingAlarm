package com.example.shou6.floodingalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Tony on 2019/2/19.
 */
public class MyAdapter2 extends ArrayAdapter {

    Context context;
    String name[];
    String time[];
    String level[];

    public MyAdapter2(Context context, int resource, String[] name, String[] time, String[] level) {
        super(context, resource, name);
        this.context=context;
        this.name=name;
        this.time=time;
        this.level=level;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.my_list_item2, parent, false);
        TextView textView1 = (TextView) v.findViewById(R.id.textView1);
        textView1.setText(name[position]);
        TextView textView2 = (TextView) v.findViewById(R.id.textView2);
        textView2.setText(time[position]);
        TextView textView3 = (TextView) v.findViewById(R.id.textView3);
        textView3.setText(level[position]);
        return v;

    }
}
