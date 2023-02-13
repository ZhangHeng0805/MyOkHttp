package com.zhangheng.myapplication.reptile;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;

import com.zhangheng.myapplication.setting.AppSetting;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;

import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class TextToSpeech {
    private MediaPlayer mediaPlayer;
    private Context context;

    public TextToSpeech(Context context) {
        this.context = context;
    }

    public void getPlay(String text, String id) {
        String Tag = context.getClass().getSimpleName();
        DialogUtil dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog();
        GetBuilder builder = OkHttpUtils.get()
                .url("https://xiaoapi.cn/API/zs_tts.php")
                .addParams("type", "xunfei")//可为baidu、youdao、xunfei（即为百度、有道、讯飞）
                .addParams("id", id)//语音类型1-20(20为英文专用语音)
                .addParams("msg", text);
        builder.build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(Tag, e.toString());
                        DialogUtil.dialog(context, "播放错误", OkHttpMessageUtil.error(e));
                        dialogUtil.closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            Log.d(Tag + "音频[" + AppSetting.XunFei_Voice_Map.get(id) + "]", "response:" + response);
                            if (JSONUtil.isTypeJSON(response)) {
                                String url = JSONUtil.parseObj(response).getStr("tts");
//                                url = URLEncodeUtil.encode(url);
                                Log.d(Tag + "音频地址：", url);
                                playAudio(url);
                            } else {
                                DialogUtil.dialog(context, "播放失败", response);
                            }
                        } catch (Exception e) {
                            DialogUtil.dialog(context, "播放失败", "对不起，此语音暂时无法播放");
                            e.printStackTrace();
                        } finally {
                            dialogUtil.closeProgressDialog();
                        }
                    }
                });

    }


    public void playAudio(String audio) throws IOException {

        mediaPlayer = new MediaPlayer();
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

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
