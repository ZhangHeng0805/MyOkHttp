package com.zhangheng.myapplication.service;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;

import androidx.annotation.Nullable;

import java.io.IOException;

import cn.hutool.core.util.StrUtil;

public class AudioService extends MyIntentService {

    private MediaPlayer mediaPlayer;

    public AudioService() {
        super("AudioService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String audio = intent.getExtras().getString("audio");
        if (!StrUtil.isBlank(audio))
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        playAudio(audio);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }

    public void playAudio(String audio) throws IOException {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            // 设置类型
            mediaPlayer.setAudioAttributes(new AudioAttributes
                    .Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mp != null) {
                        if (mp.isPlaying())
                            mp.stop();
                        mp.release();
                    }
                }
            });
        }
        mediaPlayer.reset();
//        Uri uri = Uri.parse(audio);
        mediaPlayer.setDataSource(audio);// 设置文件源
        mediaPlayer.prepare();// 解析文件
        mediaPlayer.start();

    }
}
