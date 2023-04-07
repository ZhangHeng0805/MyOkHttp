package com.zhangheng.myapplication.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.zhangheng.myapplication.setting.ServerSetting;
/*位置服务*/
public class MyService extends IntentService {

    protected ServerSetting setting;
    protected final Context context=MyService.this;

    public MyService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setting=new ServerSetting(context);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    protected void timingService(Long ms,Class<?> receiverClass) {
        timingService(AlarmManager.RTC_WAKEUP,ms,receiverClass);
    }
    protected void timingService(int type,Long ms,Class<?> receiverClass) {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime = System.currentTimeMillis() + ms;
        Intent i = new Intent(this, receiverClass);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(type, triggerAtTime, pi);
    }
}
