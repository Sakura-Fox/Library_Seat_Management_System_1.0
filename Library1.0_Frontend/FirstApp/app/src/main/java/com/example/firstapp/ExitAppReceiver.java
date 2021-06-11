package com.example.firstapp;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;

//自定义一个广播接收器,用来接收应用程序退出广播.
public class ExitAppReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (context != null) {

            if (context instanceof Activity) {

                ((Activity) context).finish();
            } else if (context instanceof FragmentActivity) {

                ((FragmentActivity) context).finish();
            } else if (context instanceof Service) {

                ((Service) context).stopSelf();
            }
        }
    }
}
