package com.zhangheng.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.service.MyService;
import com.zhangheng.myapplication.setting.ServerSetting;
import com.zhangheng.myapplication.util.LocalFileTool;
import com.zhangheng.myapplication.util.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class LauncherActivity extends Activity {
    private final String Tag = getClass().getSimpleName();
    private final Context context = LauncherActivity.this;
    private Button launcher_btn_exit;
    private TextView launcher_tv_url1, launcher_tv_url2, launcher_tv_greet;

    private ServerSetting setting;

    private int i = 6;//倒计时为i-1秒

    private String[] greetings = {
            "午夜零点整了，还在忙吗？闭上眼睛，让身心休息，静静的迎接下一个清晨吧！",
            "凌晨一点了，此刻的你正在为谁难以入眠，别多想了一切都会更好。",
            "快睡吧，凌晨两点了，为了更好的一天不要太累。",
            "凌晨三点了，现在的你是在梦乡之中，还是在为梦想四处奔波，可别忘了休息哦！",
            "凌晨四点，正在加班的你还好吗？朋友辛苦了。",
            "凌晨五点了，天开始微亮，你的梦是不是也开始发光发热了呢？",
            "清晨六点，该起床了，伸个懒腰为新的一天做个准备吧。",
            "早上七点了，你是在上班上学的途中还是在品味着美味的早餐呢？",
            "八点了，开始工作学习了吗？来为自己加把油吧！",
            "现在是上午九点，打起精神认真工作，别打瞌睡哦亲！",
            "上午十点了，正在忙碌的你来和我一起喝杯早茶吧",
            "上午十一点了，半天的工作时间就快结束了，记得让牙齿晒晒太阳。",
            "现在是正午十二点，别想着减肥，快去吃饭吧，好吃的都在等着你哦！",
            "下午一点整了，放下手中的工作午休一下吧。",
            "起床了起床了，下午两点整了小心迟到哦！",
            "现在是下午三点整，下午茶时间到了，你还在等什么？",
            "叮咚，下午四点整了，今天的计划完成多少了呢？",
            "下午五点了，回家的你一路平安。",
            "下午六点了，美味的晚餐正等着你哦！",
            "傍晚七点了，忙碌了一天是时候看看电视休息一下了。",
            "晚上八点了，和家人朋友聊聊天，述说一下心事吧！",
            "嘟嘟嘟嘟，每日晚九点准时提醒，打个哈欠，清新一下大脑空气。",
            "晚上十点整了，已经忙完的你敷上面膜快快休息吧！",
            "晚上十一点了，在忙碌中我们结束了充实的一天，期待着在梦里等你的那个人。",
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Intent intent = new Intent(this, MyService.class);
        startService(intent);

        launcher_btn_exit = findViewById(R.id.launcher_btn_exit);
        launcher_tv_url1 = findViewById(R.id.launcher_tv_url1);
        launcher_tv_url2 = findViewById(R.id.launcher_tv_url2);
        launcher_tv_greet = findViewById(R.id.launcher_tv_greet);
        setting = new ServerSetting(context);
        if (setting.getSetting("is_m3_voice_time",true)) {
            Calendar instance = Calendar.getInstance();
            int time = instance.get(Calendar.HOUR_OF_DAY);
            launcher_tv_greet.setText(greetings[time]);
            voice_time(time);
        }
        launcher_tv_url2.setText("服务器："+setting.getMainUrl());
        countDown();
    }

    public void playAudio(String audio) throws IOException {
        MediaPlayer mediaPlayer = new MediaPlayer();
        // 设置类型
        mediaPlayer.setAudioAttributes(new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
        mediaPlayer.reset();
//        Uri uri = Uri.parse(audio);
        mediaPlayer.setDataSource(audio);// 设置文件源
        mediaPlayer.prepare();// 解析文件
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp != null) {
                    mp.stop();
                    mp.release();
                }
            }
        });
    }

    private void voice_time(int time) {
        String url, audio;
        if (time == 0) {
            url = "https://xiaoapi.cn/API/data/baoshi/24.mp3";
        } else {
            url = "https://xiaoapi.cn/API/data/baoshi/" + time + ".mp3";
        }
        String path = LocalFileTool.BasePath + "/" + getString(R.string.app_name) + "/data/baoshi/";
        String name = time + ".mp3";
        if (!new File(path + name).exists()) {
            audio = url;
            OkHttpUtil.downLoad(context, url, path, name);
        } else {
            audio = path + name;
        }
        try {
            playAudio(audio);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void countDown() {
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
        handler.postDelayed(runnable, (i - 1) * 1000);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                LauncherActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        launcher_btn_exit.setText("跳转 " + String.valueOf(i));
                    }
                });
                i--;
            }
        };
        timer.schedule(task, 0, 1000);

        launcher_btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LauncherActivity.this, Main3Activity.class);
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
