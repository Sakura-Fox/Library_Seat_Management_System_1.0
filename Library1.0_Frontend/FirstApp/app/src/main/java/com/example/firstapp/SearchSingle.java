package com.example.firstapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchSingle extends BaseActivity {
    private List<CharSequence> List1 = null;
    private ArrayAdapter<CharSequence> Adapter1 = null;
    private Spinner Spinner1= null;
    private List<CharSequence> List2 = null;
    private ArrayAdapter<CharSequence> Adapter2 = null;
    private Spinner Spinner2= null;
    private Button mReportButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_single);
        Spinner1 = (Spinner)super.findViewById(R.id.sch2_spinner1);
        Spinner1.setPrompt("请选择您想举报的位置所在的区域:");
        List1 = new ArrayList<CharSequence>();
        List1.add("3F自然科学综合图书书库");
        List1.add("3F工业技术专业书库");
        List1.add("4F法律经济专业书库");
        List1.add("4F社会科学综合书库");
        List1.add("4F文学、艺术、历史专业书库");
        List1.add("5F自然科学报刊阅览室");
        List1.add("5F社会科学报刊阅览室");
        List1.add("6F医学专业书库");
        Adapter1 = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item,List1);
        Adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner1.setAdapter(Adapter1);


        Spinner2 = (Spinner)super.findViewById(R.id.sch2_spinner2);
        Spinner2.setPrompt("请选择您想举报的座位号:");
        List2 = new ArrayList<CharSequence>();
        List2.add("请先选择区域");
        Adapter2 = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item,List2);
        Adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner2.setAdapter(Adapter2);

        Spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                List2 = new ArrayList<CharSequence>();
                for(int i=1+arg2*200;i<=200+arg2*200;i++)
                {
                    String s=Integer.toString(i);
                    List2.add(s);
                }
                Adapter2 = new ArrayAdapter<CharSequence>(SearchSingle.this,android.R.layout.simple_spinner_item,List2);
                Adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Spinner2.setAdapter(Adapter2);
                arg0.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        mReportButton = findViewById(R.id.sch2_button);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SearchSingle.this,"发送成功",Toast.LENGTH_LONG).show();
            }
        });
    }
}
