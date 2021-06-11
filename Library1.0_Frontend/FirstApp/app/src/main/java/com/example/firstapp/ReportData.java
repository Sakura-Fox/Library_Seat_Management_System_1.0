package com.example.firstapp;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReportData extends BaseActivity{
    List<mData> mList;
    LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_information);
        mList = new ArrayList<>();
        mLinearLayout = findViewById(R.id.reportInformationFace);
        Integer id = 1;
        String[] arr = new String[5];
        arr[0] = "吵闹";
        arr[1] = "吃东西";
        arr[2] = "打游戏/睡觉";
        arr[3] = "以物占座";
        arr[4] = "其他";

        Integer times = 1;
        for(int i=1;i<=100;i++)
        {
            id+=i;
            times+=i/2;
            mData tmp = new mData(id.toString(),arr[id%5],times.toString());
            mList.add(tmp);
        }
        TextView textView = new TextView(this);
        textView.setTextColor(Color.parseColor("#fffff0"));
        textView.setTextSize(28);
        textView.setText("座位号   举报类型    被举报次数");
        mLinearLayout.addView(textView);

        for(mData p:mList)
        {
            TextView tv = new TextView(this);
            tv.setText(p.toString());
            tv.setTextColor(Color.parseColor("#fffff0"));
            tv.setTextSize(28);
            mLinearLayout.addView(tv);
        }
    }
}

class mData
{
    private String id;
    private String type;
    private String times;

    @Override
    public String toString() {
        return id + "           " + type + ", " + times + "次";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public mData(String id,String type,String times)
    {
        super();
        this.id = id;
        this.type = type;
        this.times = times;
    }
}
