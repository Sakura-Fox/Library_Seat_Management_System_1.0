package com.example.firstapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PersonalInformation extends BaseActivity {
    private Button mButton_sendPersonalInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        mButton_sendPersonalInformation = findViewById(R.id.btn_sendPersonalInformation);

        mButton_sendPersonalInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PersonalInformation.this,"发送成功",Toast.LENGTH_LONG).show();
            }
        });
    }
}
