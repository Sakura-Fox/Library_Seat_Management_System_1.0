package com.example.firstapp;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;

import static com.example.firstapp.Constant.EXIT_APP_ACTION;

//应用程序中所有Activity的基类
public class BaseActivity extends Activity {

    private ExitAppReceiver exitReceiver = new ExitAppReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerExitReceiver();
    }

    private void registerExitReceiver() {

        IntentFilter exitFilter = new IntentFilter();
        exitFilter.addAction(EXIT_APP_ACTION);
        registerReceiver(exitReceiver, exitFilter);
    }

    private void unRegisterExitReceiver() {

        unregisterReceiver(exitReceiver);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unRegisterExitReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
