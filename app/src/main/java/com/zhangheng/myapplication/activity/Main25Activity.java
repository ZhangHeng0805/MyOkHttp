package com.zhangheng.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.util.FormatUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class Main25Activity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private final Context context = Main25Activity.this;

    private EditText m25_et_url;
    private Button m25_btn_submit, m25_btn_down_video, m25_btn_down_music, m25_btn_down_pic;
    private Spinner m25_spinner_type;
    private LinearLayout m25_LL_result;
    private VideoView m25_video;
    private TextView m25_tv_title, m25_tv_author;

    private String[] types = {
            "抖音无水印解析",
            "快手无水印解析",
            "火山无水印解析",
            "皮皮虾无水印解析",
            "微视无水印解析",
    };
    private String[] flags = {
            "douyin",
            "kuaishou",
            "huoshan",
            "pipix",
            "weishi",
    };
    private String[] urls = {
            "https://api.gmit.vip/Api/DouYin?format=json&url=",
            "https://api.gmit.vip/Api/KuaiShou?format=json&url=",
            "https://api.gmit.vip/Api/HuoShan?format=json&url=",
            "https://api.gmit.vip/Api/PiPiX?format=json&url=",
            "https://api.gmit.vip/Api/WeiShi?format=json&url=",
    };
    private Integer index = 0;
    private Map<String, String> resultMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main25);

        m25_et_url = findViewById(R.id.m25_et_url);
        m25_btn_submit = findViewById(R.id.m25_btn_submit);
        m25_btn_down_video = findViewById(R.id.m25_btn_down_video);
        m25_btn_down_music = findViewById(R.id.m25_btn_down_music);
        m25_btn_down_pic = findViewById(R.id.m25_btn_down_pic);
        m25_spinner_type = findViewById(R.id.m25_spinner_type);
        m25_LL_result = findViewById(R.id.m25_LL_result);
        m25_video = findViewById(R.id.m25_video);
        m25_tv_title = findViewById(R.id.m25_tv_title);
        m25_tv_author = findViewById(R.id.m25_tv_author);

        listener();
    }

    private void listener() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, R.layout.item_list_text, types);
        m25_spinner_type.setAdapter(adapter);
        m25_spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final String[] et_url = {""};
        m25_et_url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = m25_et_url.getText().toString();
                if (!et_url[0].equals(s)) {
                    et_url[0] = s;
                    List<String> list = extractUrls(s);
                    if (!list.isEmpty() && FormatUtil.isWebUrl(list.get(list.size() - 1))) {
                        String url = list.get(list.size() - 1);
                        m25_et_url.setText(url);
                        String host = URLUtil.getHost(URLUtil.url(url)).getHost();
                        for (int i = 0; i < flags.length; i++) {
                            if (host.indexOf(flags[i]) > -1) {
                                m25_spinner_type.setSelection(i, true);
                            }
                        }
                    } else {
                        m25_et_url.setText("");
                    }
                }
            }
        });

        m25_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = m25_et_url.getText().toString();
                if (!StrUtil.isBlank(url)) {
                    String host = URLUtil.getHost(URLUtil.url(url)).getHost();
                    for (int i = 0; i < flags.length; i++) {
                        if (host.indexOf(flags[i]) > -1) {
                            if (index!=i){
                                DialogUtil.dialog(context,"提示:解析类型选择错误","该地址的视频类型应选择【"+types[i]+"】");
                            }
                        }
                    }
                    getData(index, url);
                } else {
                    DialogUtil.dialog(context, "输入错误", "请输入或粘贴视频地址");
                }
            }
        });
        m25_btn_down_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!resultMap.isEmpty() && !StrUtil.isBlank(resultMap.get("url"))) {
                    intentDownLoad(resultMap.get("url"), resultMap.get("title") + "@" + resultMap.get("author") + ".mp4");
                } else {
                    DialogUtil.dialog(context, "暂无下载", "对不起，暂时不能下载");
                }
            }
        });
        m25_btn_down_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!resultMap.isEmpty() && !StrUtil.isBlank(resultMap.get("music"))) {
                    intentDownLoad(resultMap.get("music"), resultMap.get("title") + "@" + resultMap.get("author") + ".mp3");
                } else {
                    DialogUtil.dialog(context, "暂无下载", "对不起，暂时不能下载");
                }
            }
        });
        m25_btn_down_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!resultMap.isEmpty() && !StrUtil.isBlank(resultMap.get("pic"))) {
                    intentDownLoad(resultMap.get("pic"), resultMap.get("title") + "@" + resultMap.get("author") + ".png");
                } else {
                    DialogUtil.dialog(context, "暂无下载", "对不起，暂时不能下载");
                }
            }
        });
    }

    private void intentDownLoad(String url, String name) {
        Intent intent = new Intent(context, Main4Activity.class);
        intent.putExtra("name", name);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void getData(Integer index, String url) {
        DialogUtil dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog();
        OkHttpUtils.get()
                .url(urls[index] + url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialogUtil.closeProgressDialog();
                        Log.e(TAG + types[index], e.toString());
                        DialogUtil.dialog(context, "解析失败", OkHttpMessageUtil.error(e));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject obj = JSONUtil.parseObj(response);
                            if (obj.getInt("code").equals(200)) {
                                m25_LL_result.setVisibility(View.VISIBLE);
                                JSONObject data = obj.getJSONObject("data");
                                String author = data.getStr("author");
                                String title = data.getStr("title");
                                m25_tv_author.setText(author);
                                m25_tv_title.setText(title);
                                resultMap.put("author", author);
                                resultMap.put("title", title);
                                String url1 = data.getStr("url");
                                resultMap.put("url", url1);
                                String music = data.getStr("music");
                                resultMap.put("music", music);
                                String pic = data.getStr("pic");
                                resultMap.put("pic", pic);
                                m25_video.setVideoURI(Uri.parse(url1));
                                m25_video.setMediaController(new MediaController(context));
                                m25_video.requestFocus();
                                m25_video.start();
                            } else {
                                String msg = obj.getStr("msg");
                                if (msg.equals("获取视频地址失败"))
                                    DialogUtil.dialog(context, "解析失败","该地址视频暂时无法解析");
                                else
                                    DialogUtil.dialog(context, "解析失败", msg);
                            }
                        } catch (Exception e) {
                            Log.e(TAG + types[index], e.toString());
                            DialogUtil.dialog(context, "解析错误", OkHttpMessageUtil.error(e));
                        } finally {
                            dialogUtil.closeProgressDialog();
                        }
                    }
                });
    }


    public static void main(String[] args) {
        String s = "1.23 Njc:/ 复制打开抖音，看看【智桃lucky的作品】今年不行就明年# 冬天专属的纯欲感  https://v.douyin.com/hVLSoJS/\n";
        List<String> list = extractUrls(s);
        String url = list.get(list.size() - 1);

        URI host = URLUtil.getHost(URLUtil.url(url));
        System.out.println(host.getHost());
    }

    /**
     * 将字符串中的url提取出来
     *
     * @param text
     * @return
     */
    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);
        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }
        return containedUrls;
    }
}
