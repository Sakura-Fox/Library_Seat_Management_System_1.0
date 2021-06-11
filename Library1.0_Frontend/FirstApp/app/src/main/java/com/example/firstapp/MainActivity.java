package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
    private EditText mEditText_username;
    private EditText mEditText_password;
    private Button mButton_login;
    private String str_username;
    private String str_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText_username = findViewById(R.id.Edit_username);
        mEditText_password = findViewById(R.id.Edit_password);
        mButton_login = findViewById(R.id.btn_login);

        mButton_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_username = mEditText_username.getText().toString();
                str_password = mEditText_password.getText().toString();
                /*if(str_username.length()!=12)
                    Toast.makeText(MainActivity.this,"学号位数必须为12位", Toast.LENGTH_LONG).show();
                else if(str_password.length()<4){
                    Toast.makeText(MainActivity.this,"密码不能少于四位",Toast.LENGTH_LONG).show();
                }else */if(str_username.equals("2")&&str_password.equals("2")){
                    Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this,Admin.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this,"用户名或密码错误",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}