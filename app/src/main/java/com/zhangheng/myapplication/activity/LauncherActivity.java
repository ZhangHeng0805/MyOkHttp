package com.zhangheng.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.zhangheng.myapplication.R;

import java.util.Timer;
import java.util.TimerTask;

public class LauncherActivity extends Activity {
    private Button launcher_btn_exit;
    private int i=4;//倒计时为i-1秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        launcher_btn_exit=findViewById(R.id.launcher_btn_exit);
        countDown();
    }
    private void countDown(){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //在主线程中执行
                Intent intent = new Intent(LauncherActivity.this, Main3Activity.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(runnable, (i-1)*1000);

        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                LauncherActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        launcher_btn_exit.setText("跳转 "+String.valueOf(i));
                    }
                });
                i--;
            }
        };
        timer.schedule(task,0,1000);

        launcher_btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LauncherActivity.this,Main3Activity.class);
                startActivity(intent);
                finish();
                handler.removeCallbacks(runnable);
            }
        });
    }
}
