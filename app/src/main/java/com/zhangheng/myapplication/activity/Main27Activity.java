package com.zhangheng.myapplication.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class Main27Activity extends AppCompatActivity {

    private EditText m27_et_context;
    private Button m27_btn_search;
    private TextView m27_tv_res_my, m27_tv_res_robot;
    private LinearLayout m27_LL_res;

    private Context context = Main27Activity.this;
    private final String Tag = this.getClass().getSimpleName();

    private String default_my = "我问: ",
            default_robot = "ChatGPT: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main27);

        initView();
    }

    private void initView() {
        m27_et_context = findViewById(R.id.m27_et_context);
        m27_btn_search = findViewById(R.id.m27_btn_search);
        m27_tv_res_my = findViewById(R.id.m27_tv_res_my);
        m27_tv_res_robot = findViewById(R.id.m27_tv_res_robot);
        m27_LL_res = findViewById(R.id.m27_LL_res);
        m27_LL_res.setVisibility(View.GONE);

        m27_btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = m27_et_context.getText().toString();
                if (!StrUtil.isBlank(text)) {
                    getData(text);
                } else {
                    DialogUtil.dialog(context, "输入错误", "输入内容不能为空");
                }
            }
        });
    }

    private void getData(String text) {
        DialogUtil dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog("查询时间可能有点长，请耐心等待，加载中...");
        String url = "https://v1.apigpt.cn/";
        OkHttpUtils.get().url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.41")
                .addParams("q",text)
                .addParams("apitype","sql")
                .build()
                .connTimeOut(15000L)
                .readTimeOut(120000L)
                .writeTimeOut(20000L)
                .execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(Tag, e.toString());
                DialogUtil.dialog(context, "数据获取失败", OkHttpMessageUtil.error(e));
                dialogUtil.closeProgressDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    if (JSONUtil.isTypeJSON(response)) {
                        JSONObject parse = JSONUtil.parseObj(response);
                        Integer code = parse.getInt("code");
                        String msg = parse.getStr("msg");
                        if (code.equals(200)) {
                            m27_LL_res.setVisibility(View.VISIBLE);
                            String questions = parse.getStr("Questions");
                            m27_tv_res_my.setText(default_my + questions);
                            String chatGPT_answer = parse.getStr("ChatGPT_Answer");
                            m27_tv_res_robot.setText(default_robot + chatGPT_answer);
                            if (!StrUtil.isBlank(msg))
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            DialogUtil.dialog(context, "获取失败", msg);
                        }
                    } else {
                        DialogUtil.dialog(context, "数据格式错误", response);
                    }
                } catch (Exception e) {
                    DialogUtil.dialog(context, "数据获取异常", e.toString());
                    e.printStackTrace();
                } finally {
                    dialogUtil.closeProgressDialog();
                }
            }
        });

    }
}
