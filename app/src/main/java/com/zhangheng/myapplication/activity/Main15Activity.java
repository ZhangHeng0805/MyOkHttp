package com.zhangheng.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class Main15Activity extends Activity {

    private final String TAG = getClass().getSimpleName();
    private final Context context = Main15Activity.this;

    private TextView m15_tv_title_week,m15_tv_title_tips,m15_tv_title_lunar,
            m15_tv_title,m15_tv_title_solar,m15_tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main15);

        m15_tv_title_week = findViewById(R.id.m15_tv_title_week);
        m15_tv_title_tips = findViewById(R.id.m15_tv_title_tips);
        m15_tv_title_lunar = findViewById(R.id.m15_tv_title_lunar);
        m15_tv_title = findViewById(R.id.m15_tv_title);
        m15_tv_title_solar = findViewById(R.id.m15_tv_title_solar);
        m15_tv_content = findViewById(R.id.m15_tv_content);
        getData();
    }

    private void getData() {
        DialogUtil dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog("加载中。。。");
        OkHttpUtils.get()
                .url("https://api.vvhan.com/api/60s?type=json")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, e.getMessage());
                        DialogUtil.dialog(context, "请求失败", OkHttpMessageUtil.error(e));
                        dialogUtil.closeProgressDialog();
                        ;
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject obj = JSONUtil.parseObj(response);

                            String title = obj.getStr("name");
                            m15_tv_title.setText(title);

                            JSONArray time = obj.getJSONArray("time");
                            m15_tv_title_solar.setText(time.get(0).toString());
                            m15_tv_title_lunar.setText(time.get(1).toString());
                            m15_tv_title_week.setText(time.get(2).toString());

                            JSONArray data = obj.getJSONArray("data");
                            String tips = data.get(data.size() - 1).toString();
                            if (tips.startsWith("【微语】")){
                                m15_tv_title_tips.setText(tips.replace("【微语】",""));
                            }
                            StringBuilder sb = new StringBuilder();

                            for (int i = 0; i < data.size(); i++) {
                                if (data.get(i).toString().startsWith("【微语】")){
                                    sb.append(data.get(i).toString());
                                }else {
                                    sb.append((i + 1) + "、" + data.get(i).toString() + "\n\n");
                                }
                            }
                            m15_tv_content.setText(sb.toString());
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            DialogUtil.dialog(context, "请求错误", OkHttpMessageUtil.error(e));
                        } finally {
                            dialogUtil.closeProgressDialog();
                        }
                    }
                });
    }


}
