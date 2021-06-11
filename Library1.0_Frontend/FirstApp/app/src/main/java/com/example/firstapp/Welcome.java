package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;

import static com.example.firstapp.Constant.EXIT_APP_ACTION;

public class Welcome extends BaseActivity{

    private DrawerLayout mDrawerLayout;
    private ListView mListView;
    private ArrayList<String> menuLists;
    private ArrayAdapter<String> mAdapter;
    private Button mButton_report;
    private Button mButton_order;
    private Button mButton_quitSeat;
    private Button mButton_showResult;
    private Button mButton_searchSeat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mButton_report = findViewById(R.id.btn_report);
        mButton_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Welcome.this, Report.class);
                startActivity(intent);
            }
        });

        mButton_order = findViewById(R.id.btn_orderSeat);
        mButton_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Welcome.this, ThreeChoice.class);
                startActivity(intent);
            }
        });

        mButton_quitSeat = findViewById(R.id.btn_quitSeat);
        mButton_quitSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Welcome.this,"发送成功",Toast.LENGTH_LONG).show();
            }
        });

        mButton_showResult = findViewById(R.id.btn_orderResult);
        mButton_showResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Welcome.this,Seat.class);
                startActivity(intent);
            }
        });

        mButton_searchSeat = findViewById(R.id.btn_showSeat);
        mButton_searchSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Welcome.this, Search.class);
                startActivity(intent);
            }
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mListView = findViewById(R.id.left_drawer);
        menuLists = new ArrayList<String>();
        menuLists.add(new String("查看信誉分"));
        menuLists.add(new String("编辑个人信息"));
        menuLists.add(new String("修改密码"));
        menuLists.add(new String("退出当前账号"));
        mAdapter = new ArrayAdapter<String>(
                this,R.layout.array_adapter,menuLists);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent0 = new Intent(Welcome.this,Score.class);
                        startActivity(intent0);
                        break;
                    case 1:
                        Intent intent1 = new Intent(Welcome.this,PersonalInformation.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent2 = new Intent(Welcome.this,ChangePwd.class);
                        startActivity(intent2);
                        break;
                    case 3:
                        Intent intent = new Intent();
                        intent.setAction(EXIT_APP_ACTION);
                        sendBroadcast(intent);
                        Intent intent3 = new Intent(Welcome.this,MainActivity.class);
                        startActivity(intent3);
                        break;
                }
            }
        });
    }
}
