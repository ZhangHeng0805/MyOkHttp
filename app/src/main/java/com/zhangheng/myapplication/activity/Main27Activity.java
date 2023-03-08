package com.zhangheng.myapplication.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.SystemUtil;
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
    private Spinner m27_spinner_type;

    private String[] types={"通道一","通道二"};

    private Context context = Main27Activity.this;
    private final String Tag = this.getClass().getSimpleName();

    private String default_my = "我问: ",
            default_robot = "ChatGPT: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main27);

        initView();
        Toast.makeText(context,"提示：本功能每日有次数限制，有时可能使用不了",Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        m27_et_context = findViewById(R.id.m27_et_context);
        m27_btn_search = findViewById(R.id.m27_btn_search);
        m27_tv_res_my = findViewById(R.id.m27_tv_res_my);
        m27_tv_res_robot = findViewById(R.id.m27_tv_res_robot);
        m27_LL_res = findViewById(R.id.m27_LL_res);
        m27_spinner_type = findViewById(R.id.m27_spinner_type);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,R.layout.item_list_text,types);
        m27_spinner_type.setAdapter(adapter);

        m27_LL_res.setVisibility(View.GONE);

        m27_btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = m27_et_context.getText().toString();
                if (!StrUtil.isBlank(text)) {
                    SystemUtil.closeInput(Main27Activity.this);
                    getData(text,m27_spinner_type.getSelectedItemPosition());
                } else {
                    DialogUtil.dialog(context, "输入错误", "输入内容不能为空");
                }
            }
        });


    }

    private void getData(String text,Integer index) {
        DialogUtil dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog("查询时间可能有点长，请耐心等待，加载中...");
        String url = "https://v1.apigpt.cn/?q="+text+"&apitype=sql";
        switch (index){
            case 0:
                url="https://api.wqwlkj.cn/wqwlapi/chatgpt.php?msg="+text+"&type=json";
                break;
            case 1:
                url="https://v1.apigpt.cn/?q="+text+"&apitype=sql";
                break;
        }

        OkHttpUtils.get().url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.41")
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
                            switch (index){
                                case 0:
                                    Response2(response);
                                    break;
                                case 1:
                                    Response1(response);
                                    break;
                            }
//                            Response2(response);
                        } catch (Exception e) {
                            DialogUtil.dialog(context, "数据获取异常", e.toString());
                            e.printStackTrace();
                        } finally {
                            dialogUtil.closeProgressDialog();
                        }
                    }
                });
    }

    /**
     *https://api.wqwlkj.cn/wqwlapi/chatgpt.php?msg="+text+"&type=json
     * @param response
     */
    public void Response2(String response){
        if (JSONUtil.isTypeJSON(response)) {
            JSONObject parse = JSONUtil.parseObj(response);
            Integer code = parse.getInt("code");
            String answer = parse.getStr("answer");
            String question = parse.getStr("question");
//            if (code.equals(1)){
                m27_LL_res.setVisibility(View.VISIBLE);
                m27_tv_res_my.setText(default_my + question);
                if (StrUtil.isBlank(answer))
                    answer="对不起，我暂时无法回答";
                m27_tv_res_robot.setText(default_robot + answer);
//            }else {
//                DialogUtil.dialog(context, "获取失败", answer);
//            }
        }else {
            DialogUtil.dialog(context, "数据格式错误", response);
        }
    }

    /**
     * https://v1.apigpt.cn/?q="+text+"&apitype=sql
     * @param response
     */
    public void Response1(String response){
        if (JSONUtil.isTypeJSON(response)) {
            JSONObject parse = JSONUtil.parseObj(response);
            Integer code = parse.getInt("code");
            String msg = parse.getStr("msg");
            if (code.equals(200)) {
                m27_LL_res.setVisibility(View.VISIBLE);
                String questions = parse.getStr("Questions");
                m27_tv_res_my.setText(default_my + questions);
                String chatGPT_answer = parse.getStr("ChatGPT_Answer");
                if (!StrUtil.isBlank(chatGPT_answer))
                    m27_tv_res_robot.setText(default_robot + chatGPT_answer);
                else
                    m27_tv_res_robot.setText(default_robot + msg);
                if (!StrUtil.isBlank(msg))
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            } else {
                DialogUtil.dialog(context, "获取失败", msg);
            }
        } else {
            DialogUtil.dialog(context, "数据格式错误", response);
        }
    }
}
