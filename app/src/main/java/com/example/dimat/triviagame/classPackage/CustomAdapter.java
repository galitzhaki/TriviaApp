package com.example.dimat.triviagame.classPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dimat.triviagame.GameActivities.R;

import java.util.List;


public class CustomAdapter extends BaseAdapter {
    Context context;
    List<Score> scoreData;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, List<Score> scoreData) {
        this.context = applicationContext;
        this.scoreData = scoreData;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return scoreData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_listview, null);
        TextView t1 = (TextView) view.findViewById(R.id.textView1);
        t1.setText(new String(scoreData.get(i).id + ""));
        TextView t2 = (TextView) view.findViewById(R.id.textView2);
        t2.setText(scoreData.get(i).name);
        TextView t3 = (TextView) view.findViewById(R.id.textView3);
        t3.setText(new String(scoreData.get(i).score + ""));
        return view;
    }
}