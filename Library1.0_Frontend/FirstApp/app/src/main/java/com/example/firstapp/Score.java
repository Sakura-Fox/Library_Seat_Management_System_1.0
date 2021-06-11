package com.example.firstapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class Score extends BaseActivity {
    private TextView mTextView;
    private String score = "100分";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score);
        mTextView = findViewById(R.id.tv_score);
        mTextView.setText(score);
    }
}
