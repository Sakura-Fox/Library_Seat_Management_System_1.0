package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ThreeChoice extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.three_choice);
        Button mButton1 = findViewById(R.id.btn_firstChoice);
        Button mButton2 = findViewById(R.id.btn_secondChoice);
        Button mButton3 = findViewById(R.id.btn_thirdChoice);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ThreeChoice.this,Order1.class);
                startActivity(intent1);
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(ThreeChoice.this,Order2.class);
                startActivity(intent2);
            }
        });
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(ThreeChoice.this,Order3.class);
                startActivity(intent3);
            }
        });
    }
}
