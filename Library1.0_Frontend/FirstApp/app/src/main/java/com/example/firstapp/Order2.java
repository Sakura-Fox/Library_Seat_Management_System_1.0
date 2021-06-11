package com.example.firstapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Order2 extends BaseActivity {

    private List<CharSequence> List1 = null;
    private ArrayAdapter<CharSequence> Adapter1 = null;
    private Spinner Spinner1= null;
    private List<CharSequence> List2 = null;
    private ArrayAdapter<CharSequence> Adapter2 = null;
    private Spinner Spinner2= null;
    Button Btn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order2);
        Spinner1 = (Spinner)super.findViewById(R.id.yd2_spinner1);
        Spinner1.setPrompt("请选择您想选择的座位类型:");
        List1 = new ArrayList<CharSequence>();
        List1.add("随意");
        List1.add("单人座");
        List1.add("双人座");
        List1.add("多人座");
        Adapter1 = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item,List1);
        Adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner1.setAdapter(Adapter1);
        Spinner2 = (Spinner)super.findViewById(R.id.yd2_spinner2);
        Spinner2.setPrompt("请选择您想选择的区域类型:");
        List2 = new ArrayList<CharSequence>();
        List2.add("随意");
        List2.add("3F自然科学综合图书书库");
        List2.add("3F工业技术专业书库");
        List2.add("4F法律经济专业书库");
        List2.add("4F社会科学综合书库");
        List2.add("4F文学、艺术、历史专业书库");
        List2.add("5F自然科学报刊阅览室");
        List2.add("5F社会科学报刊阅览室");
        List2.add("6F医学专业书库");
        Adapter2 = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item,List2);
        Adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner2.setAdapter(Adapter2);
        Btn2=findViewById(R.id.yd2_button);
        Btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Order2.this,"发送成功",Toast.LENGTH_LONG).show();
            }
        });
    }

}
