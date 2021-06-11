package com.example.firstapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePwd extends BaseActivity{
    private EditText mEditText_pwd1;
    private EditText mEditText_pwd2;
    private EditText mEditText_pwd3;
    private Button mButton;
    private String str_pwd1;
    private String str_pwd2;
    private String str_pwd3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        mEditText_pwd1 = findViewById(R.id.Edit_orginPassword);
        mEditText_pwd2 = findViewById(R.id.Edit_newPassword);
        mEditText_pwd3 = findViewById(R.id.Edit_repeatPassword);
        mButton = findViewById(R.id.btn_changePwd);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_pwd1 = mEditText_pwd1.getText().toString();
                str_pwd2 = mEditText_pwd2.getText().toString();
                str_pwd3 = mEditText_pwd3.getText().toString();
                if(!str_pwd1.equals("2")) {
                    Toast.makeText(ChangePwd.this,"原密码错误",Toast.LENGTH_LONG).show();
                }
                else if(!str_pwd2.equals(str_pwd3)) {
                    Toast.makeText(ChangePwd.this, "两次输入的新密码不一致", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(ChangePwd.this,"修改成功",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
