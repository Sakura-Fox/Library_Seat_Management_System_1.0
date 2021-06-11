package com.example.firstapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class Seat extends BaseActivity {
    private TextView mTextView;
    private String room = "6F医学专业书库";
    private String num = "1401";
    private Button mButton1;
    private Button mButton2;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_success);
        mTextView = findViewById(R.id.tv_seat);
        mButton1 = findViewById(R.id.btn_orderYes);
        mButton2 = findViewById(R.id.btn_orderNo);
        mTextView.setText(room+" "+num);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Seat.this,"发送成功",Toast.LENGTH_LONG).show();
                finish();
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Seat.this,"发送成功",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
