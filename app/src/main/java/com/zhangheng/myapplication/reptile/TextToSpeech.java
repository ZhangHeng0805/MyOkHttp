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
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class TextToSpeech {
    private MediaPlayer mediaPlayer;
    private Context context;
    public static Map<String,String> data=new HashMap<>();

    public TextToSpeech(Context context) {
        this.context = context;
    }

    /**
     * 当type为baidu和xunfei时该值有效，
     * 其中为百度时id，id值为1~8，
     * 分别为度逍遥-磁性男声、度博文-情感男声、
     * 度小贤-情感男声、度小鹿-甜美女声、度灵儿-清澈女声、
     * 度小乔-情感女声、度小雯-成熟女声、度米朵-可爱女童；
     * 当为讯飞时，id值为1~20，分别为讯飞-七哥（男声）、
     * 讯飞-子晴（女声）、讯飞-一菲（女声）、讯飞-小露（女声）、
     * 讯飞-小鹏（男声）、讯飞-萌小新（男声）、讯飞-小雪（女声）、
     * 讯飞-超哥（男声）、讯飞-小媛（女声）、讯飞-叶子（女声）、
     * 讯飞-千雪（女声）、讯飞-小忠（男声）、讯飞-万叔（男声）、
     * 讯飞-虫虫（女声）、讯飞-楠楠（儿童-男）、讯飞-晓璇（女声）、
     * 讯飞-芳芳（儿童-女）、讯飞-嘉嘉（女声）、讯飞-小倩（女声）、
     * 讯飞-Catherine（女声-英文专用）
     * @param text
     * @param id
     */
    public void getPlay(String text, String id) {
        if (StrUtil.isBlank(data.get(text))) {
            String Tag = context.getClass().getSimpleName();
            DialogUtil dialogUtil = new DialogUtil(context);
            dialogUtil.createProgressDialog();
            String[] types={"baidu"};
            GetBuilder builder = OkHttpUtils.get()
                    .url("https://xiaoapi.cn/API/zs_tts.php")
                    .addParams("type", types[Convert.toInt(id)%types.length])//可为baidu、youdao、xunfei（即为百度、有道、讯飞）
//                .addParams("id", id)//语音类型1-20(20为英文专用语音)
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
                                    data.put(text, url);
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
        }else {
            try {
                playAudio(data.get(text));
            } catch (IOException e) {
                DialogUtil.dialog(context, "播放失败", "对不起，此语音暂时无法播放");
                e.printStackTrace();
            }
        }
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
