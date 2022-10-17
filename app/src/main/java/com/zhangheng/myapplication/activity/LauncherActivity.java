package com.zhangheng.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.setting.ServerSetting;
import com.zhangheng.myapplication.util.SystemUtil;

import java.util.Timer;
import java.util.TimerTask;

public class LauncherActivity extends Activity {
    private Button launcher_btn_exit;
    private TextView launcher_tv_url1,launcher_tv_url2;

    private ServerSetting serverSetting;

    private int i=5;//倒计时为i-1秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        launcher_btn_exit=findViewById(R.id.launcher_btn_exit);
        launcher_tv_url1=findViewById(R.id.launcher_tv_url1);
        launcher_tv_url2=findViewById(R.id.launcher_tv_url2);

        serverSetting=new ServerSetting(LauncherActivity.this);

        launcher_tv_url2.setText(serverSetting.getMainUrl());
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
        launcher_tv_url1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = SystemUtil.copyStr(LauncherActivity.this, launcher_tv_url1.getText().toString());
                if (b)
                Toast.makeText(LauncherActivity.this, "内容已复制到剪切板", Toast.LENGTH_SHORT).show();
            }
        });
        launcher_tv_url2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = SystemUtil.copyStr(LauncherActivity.this, launcher_tv_url2.getText().toString());
                if (b)
                Toast.makeText(LauncherActivity.this, "内容已复制到剪切板", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
