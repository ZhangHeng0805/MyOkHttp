package com.zhangheng.myapplication.activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangheng.file.FiletypeUtil;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.reptile.TextToSpeech;
import com.zhangheng.myapplication.setting.AppSetting;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.LocalFileTool;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.SystemUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class Main26Activity extends AppCompatActivity {

    private final String Tag = this.getClass().getSimpleName();

    private final Context context = Main26Activity.this;

    private TextToSpeech speech;

    private EditText m26_et_context;
    private Button m26_btn_search, m26_btn_copyUrl, m26_btn_save;
    private Spinner m26_sp_type;
    private ImageView m26_iv_play;
    private LinearLayout m26_LL_btn;

    private String text, id, url;
    private MediaPlayer mediaPlayer;
    private String[] types;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main26);
        initView();
    }

    private void initView() {
        m26_et_context = findViewById(R.id.m26_et_context);
        m26_btn_search = findViewById(R.id.m26_btn_search);
        m26_btn_copyUrl = findViewById(R.id.m26_btn_copyUrl);
        m26_btn_save = findViewById(R.id.m26_btn_save);
        m26_sp_type = findViewById(R.id.m26_sp_type);
        m26_iv_play = findViewById(R.id.m26_iv_play);
        m26_LL_btn = findViewById(R.id.m26_LL_btn);

        int size = AppSetting.XunFei_Voice_Map.size() + AppSetting.BaiDu_Voice_Map.size();
        types = new String[size];
        for (Map.Entry<String, String> entry : AppSetting.XunFei_Voice_Map.entrySet()) {
            Integer i = Convert.toInt(entry.getKey());
            types[i - 1] = entry.getValue();
        }
        for (Map.Entry<String, String> entry : AppSetting.BaiDu_Voice_Map.entrySet()) {
            Integer i = AppSetting.XunFei_Voice_Map.size() + Convert.toInt(entry.getKey());
            types[i - 1] = entry.getValue();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_list_text, types);
        m26_sp_type.setAdapter(adapter);

        speech = new TextToSpeech(context);
        m26_btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m26_LL_btn.setVisibility(View.GONE);
                text = m26_et_context.getText().toString();
                if (!StrUtil.isBlank(text)) {
                    boolean chinese = Validator.hasChinese(text);
                    Integer index = m26_sp_type.getSelectedItemPosition() + 1;
                    if (chinese) {
                        if (index.equals(20)) {
                            DialogUtil.dialog(context, "类型选择错误", "中文不能选择英文专用语音");
                            id = null;
                        } else {
                            id = index + "";
                        }
                    } else {
                        if (index.equals(20)) {
                            id = index + "";
                        } else {
                            DialogUtil.dialog(context, "类型选择提示", "英文推荐使用英文专用语音");
                            id = null;
                        }
                    }
                    if (!StrUtil.isBlank(id))
                        getPlay(text, id);
                } else {
                    DialogUtil.dialog(context, "输入错误", "输入内容不能为空！");
                }
            }
        });
        m26_btn_copyUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validator.isUrl(url)) {
                    boolean b = SystemUtil.copyStr(context, url);
                    if (b)
                        DialogUtil.dialog(context, "复制成功", "音频地址已复制到到剪切板");
                    else
                        DialogUtil.dialog(context, "复制失败", "音频地址复制失败！");
                } else {
                    DialogUtil.dialog(context, "复制失败", "音频地址错误！");
                }
            }
        });
        m26_iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                } catch (Exception e) {
                }
                if (Validator.isUrl(url)) {
                    try {
                        int i = Convert.toInt(id) - 1;
                        m26_sp_type.setSelection(i, true);
                        plagAudio(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    m26_LL_btn.setVisibility(View.GONE);
                }
            }
        });
        m26_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = getString(R.string.app_name) + "文字转语音-" + new Date().getTime() + ".mp3";
                String fileType = FiletypeUtil.getFileType(name);
                String path = LocalFileTool.BasePath + "/" + getString(R.string.app_name) + "/download/" + fileType + "/";
                OkHttpUtil.downLoad(context, url, path, name);
                DialogUtil.dialog(context, "保存成功", "保存位置：\n" + path + name);
            }
        });
    }

    private void getPlay(String text, String id) {
        String type;
        Integer i = Convert.toInt(id);
        if (i >= 1 && i <= 20) {
            type = "xunfei";
        } else if (i > 20 && i <= 28) {
            type = "baidu";
            id = (i - 20) + "";
        } else {
            DialogUtil.dialog(context, "语音类型错误", "语音类型不存在！");
            return;
        }

        DialogUtil dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog();
        GetBuilder builder = OkHttpUtils.get()
                .url("https://xiaoapi.cn/API/zs_tts.php")
                .addParams("type", type)//可为baidu、youdao、xunfei（即为百度、有道、讯飞）
                .addParams("id", id)//语音类型1-20(20为英文专用语音)
                .addParams("msg", text);
        builder.build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialogUtil.closeProgressDialog();
                        m26_LL_btn.setVisibility(View.GONE);
                        Log.e(Tag, e.toString());
                        DialogUtil.dialog(context, "播放错误", OkHttpMessageUtil.error(e));
                    }

                    @Override
                    public void onResponse(String response, int i1) {
                        try {
                            Log.d(Tag + "音频", "response:" + response);
                            if (JSONUtil.isTypeJSON(response)) {
                                JSONObject obj = JSONUtil.parseObj(response);
                                if (obj.getInt("code").equals(200)) {
                                    url = obj.getStr("tts");
                                }else {
                                    DialogUtil.dialog(context, "播放失败", obj.getStr("msg"));
                                    return;
                                }
                                Log.d(Tag + "音频地址["+obj.getStr("name")+"]：", url);
                                plagAudio(url);
                            } else {
                                DialogUtil.dialog(context, "播放失败", response);
                                m26_LL_btn.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            DialogUtil.dialog(context, "播放失败", "对不起，此语音暂时无法播放");
                            e.printStackTrace();
                            m26_LL_btn.setVisibility(View.GONE);
                        } finally {
                            dialogUtil.closeProgressDialog();
                        }
                    }
                });

    }

    private void plagAudio(String url) throws IOException {
        if (Validator.isUrl(url)) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            Uri uri = Uri.parse(url);
            mediaPlayer.setDataSource(context, uri);// 设置文件源
            mediaPlayer.prepare();// 解析文件
            mediaPlayer.start();
            m26_LL_btn.setVisibility(View.VISIBLE);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mp != null) {
                        mp.stop();
                        mp.release();
                    }
                }
            });
        } else {
            m26_LL_btn.setVisibility(View.GONE);
            throw new IOException("音频地址错误！");
        }
    }

}
