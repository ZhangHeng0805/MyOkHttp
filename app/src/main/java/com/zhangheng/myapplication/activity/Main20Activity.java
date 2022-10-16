package com.zhangheng.myapplication.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.SystemUtil;
import com.zhangheng.util.EncryptUtil;
import com.zhangheng.util.RandomrUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

/**
 * 翻译(爬虫
 */
public class Main20Activity extends AppCompatActivity {

    private final String Tag = this.getClass().getSimpleName();

    private EditText m20_et_context;
    private Button m20_btn_search;
    private Spinner m20_sp_type;
    private LinearLayout m20_LL_result;
    private TextView m20_tv_translateResult, m20_tv_smartResult;
    private ImageView m20_iv_bofang;


    private String type_code;
    private final static Map<String, String> Lag_Type = new HashMap<String, String>();

    static {
        Lag_Type.put("zh-CHS", "中文");
        Lag_Type.put("en", "英语");
        Lag_Type.put("ja", "日语");
        Lag_Type.put("ko", "韩语");
        Lag_Type.put("fr", "法语");
        Lag_Type.put("de", "德语");
        Lag_Type.put("ru", "俄语");
        Lag_Type.put("es", "西班牙语");
        Lag_Type.put("pt", "葡萄牙语");
        Lag_Type.put("it", "意大利语");
        Lag_Type.put("vi", "越南语");
        Lag_Type.put("id", "印尼语");
        Lag_Type.put("ar", "阿拉伯语");
        Lag_Type.put("nl", "荷兰语");
        Lag_Type.put("th", "泰语");
    }

    private String[] type_en = {"zh-CHS", "en", "ja", "ko", "fr", "de", "ru", "es", "pt", "it", "vi", "id", "ar", "nl", "th"};
    private String[] type_zh = {"中文", "英语", "日语", "韩语", "法语", "德语", "俄语", "西班牙语", "葡萄牙语", "意大利语", "越南语", "印尼语", "阿拉伯语", "荷兰语", "泰语"};
    private String[] types_code;
    private String translation_results;
    private String translation_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main20);
        init();
    }

    private void init() {
        m20_et_context = findViewById(R.id.m20_et_context);
        m20_btn_search = findViewById(R.id.m20_btn_search);
        m20_sp_type = findViewById(R.id.m20_sp_type);
        m20_LL_result = findViewById(R.id.m20_LL_result);
        m20_LL_result.setVisibility(View.GONE);
        m20_tv_translateResult = findViewById(R.id.m20_tv_translateResult);
        m20_tv_smartResult = findViewById(R.id.m20_tv_smartResult);
        m20_iv_bofang = findViewById(R.id.m20_iv_bofang);
        m20_iv_bofang.setVisibility(View.GONE);

        initType();
        listener();
    }

    private void listener() {
        //搜索
        m20_btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.closeInput(Main20Activity.this);
                String text = m20_et_context.getText().toString();
                if (!StrUtil.isEmptyIfStr(text)) {
                    m20_tv_translateResult.setText("");
                    m20_tv_smartResult.setText("");
                    getTranslate(text, type_code);
                } else {
                    DialogUtil.dialog(Main20Activity.this, "输入错误", "搜索内容不能为空");
                }
            }
        });
        m20_iv_bofang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StrUtil.isEmpty(translation_results) && !StrUtil.isEmpty(translation_type)) {
                        playAudio(translation_results,translation_type);

                }else {
                    m20_iv_bofang.setVisibility(View.GONE);
                }
            }
        });

    }

    private void initType() {
        String[] types_zh = new String[(type_zh.length - 1) * 2 + 1];
        String[] types_en = new String[(type_zh.length - 1) * 2 + 1];

        String zh = type_zh[0];
        String index, zh2en, en2zh;
//        int n=0;
        types_zh[0] = "自动检测";
        types_en[0] = "auto2auto";
        for (int i = 1; i < type_zh.length; i++) {
            index = type_zh[i];
            zh2en = zh + " > " + index;
            en2zh = index + " > " + zh;
            types_zh[2 * i - 1] = zh2en;
            types_en[2 * i - 1] = type_en[0] + "2" + type_en[i];

            types_zh[2 * i] = en2zh;
            types_en[2 * i] = type_en[i] + "2" + type_en[0];
        }
        types_code = types_en;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                Main20Activity.this, R.layout.item_list_text, types_zh);
        m20_sp_type.setAdapter(adapter);

        m20_sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                DialogUtil.dialog(Main20Activity.this,types_zh[i],types_en[i]);
                type_code = types_en[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void playAudio(String text,String type) {
        try {
            String url="https://tts.youdao.com/fanyivoice?word="+text+"&le="+type+"&keyfrom=speaker-target";
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            Uri uri = Uri.parse(url);
            mediaPlayer.setDataSource(this, uri);// 设置文件源
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getTranslate(String text, String type) {
        DialogUtil dialogUtil = new DialogUtil(Main20Activity.this);

        Log.d(Tag + "-type", type + ":" + text);
        String url = "https://fanyi.youdao.com/translate_o?smartresult=dict&smartresult=rule";
        String ncoo = "" + 2147483647 * Math.random();
//        String ipByHost = NetUtil.getIpByHost("www.baidu.com");
        String ipByHost = "110.242.68.4";
        String app_v = "5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.134 Safari/537.36 Edg/103.0.1264.77";
        String cookie = "OUTFOX_SEARCH_USER_ID=-257520112@" + ipByHost + "; OUTFOX_SEARCH_USER_ID_NCOO=" + ncoo + "; ___rl__test__cookies=" + new Date().getTime();

        Map<String, String> map = new HashMap<String, String>();
        String[] split = type.split("2");
        map.put("from", split[0]);
        map.put("to", split[1]);
        map.put("i", text);
        map.put("smartresult", "dict");
        map.put("client", "fanyideskweb");
        long time = new Date().getTime();
        map.put("lts", String.valueOf(time));
        String salt = time + String.valueOf(RandomrUtil.createRandom(0, 9));
        map.put("salt", salt);
        String sign = EncryptUtil.getMd5("fanyideskweb" + text + salt + "Ygy_4c=r#e#4EX^NUGUc5");
        map.put("sign", sign);
        map.put("bv", EncryptUtil.getMd5(app_v));
        map.put("doctype", "json");
        map.put("version", "2.1");
        map.put("keyfrom", "fanyi.web");
        map.put("action", "FY_BY_CLICKBUTTION");


        dialogUtil.createProgressDialog("翻译中。。。");
        OkHttpUtils.post()
                .url(url)
                .params(map)
                .addHeader("Referer", "https://fanyi.youdao.com/")
                .addHeader("User-Agent", "Mozilla/" + app_v)
                .addHeader("Cookie", cookie)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(Tag, e.toString());
                dialogUtil.closeProgressDialog();
                DialogUtil.dialog(Main20Activity.this,"翻译错误",e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                String json = response.replace("\\r", "")
                        .replace("\\n", "");
                JSONObject parseObj = JSONUtil.parseObj(json);
                Integer errorCode = parseObj.getInt("errorCode");
                if (errorCode.equals(0)) {
                    String type1 = parseObj.getStr("type");
                    int position = 0;
                    for (int i = 0; i < types_code.length; i++) {
                        if (type1.equals(types_code[i])) {
                            position = i;
                            break;
                        }
                    }
                    m20_sp_type.setSelection(position);

                    String[] type={"eng","jap","ko","fr"};
                    translation_type=null;
                    for (String s : type) {
                        if (s.indexOf(type1.split("2")[1])>-1){
                            translation_type=s;
                            break;
                        }
                    }
                    if (!StrUtil.isEmpty(translation_type)){
                        m20_iv_bofang.setVisibility(View.VISIBLE);
                    }else {
                        m20_iv_bofang.setVisibility(View.GONE);
                    }

                    JSONObject smartResult = parseObj.getJSONObject("smartResult");
                    if (smartResult != null) {
                        JSONArray entries = smartResult.getJSONArray("entries");
                        if (entries.size() > 0) {
                            StringBuilder sb2 = new StringBuilder(text + ":\n");
                            for (Object entry : entries) {
                                if (!StrUtil.isEmptyIfStr(entries)) {
                                    sb2.append(entry.toString() + "\n");
                                }
                            }
                            m20_tv_smartResult.setText(sb2.toString());
                        }
                    }

                    JSONArray translateResult = parseObj.getJSONArray("translateResult");
                    StringBuilder sb1 = new StringBuilder("[" + text + "]翻译结果如下:\n\n");
                    for (Object o : translateResult) {
                        JSONArray objects = JSONUtil.parseArray(o);
                        for (Object obj : objects) {
                            if (obj != null) {
                                JSONObject jsonObject = JSONUtil.parseObj(obj);
                                //原文
                                String src = jsonObject.getStr("src");
                                //译文
                                String tgt = jsonObject.getStr("tgt");
                                sb1.append(tgt + "\n");
                                translation_results = tgt;
                            }
                        }
                    }
                    m20_LL_result.setVisibility(View.VISIBLE);
                    m20_tv_translateResult.setText(sb1.toString());
                    type_code = types_code[0];
                } else {
                    Log.w(Tag, "翻译错误代码：" + errorCode);
                    DialogUtil.dialog(Main20Activity.this, "翻译错误", "错误码：" + errorCode);
                }
                dialogUtil.closeProgressDialog();
            }
        });

    }

}
