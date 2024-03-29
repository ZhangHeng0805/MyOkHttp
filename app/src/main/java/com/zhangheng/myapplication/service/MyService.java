package com.zhangheng.myapplication.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.zhangheng.myapplication.setting.ServerSetting;
/*位置服务*/
public class MyService extends Service {

    protected ServerSetting setting;
    protected Context context=MyService.this;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setting=new ServerSetting(context);
        return super.onStartCommand(intent, flags, startId);
    }

    protected void timingService(Long ms, Class<?> receiverClass) {
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
