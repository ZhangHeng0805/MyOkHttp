package com.zhangheng.myapplication.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import org.jsoup.Jsoup;

import cn.hutool.core.util.StrUtil;
import okhttp3.Call;

public class Main14Activity extends AppCompatActivity {
    private final String Tag = this.getClass().getSimpleName();
    private final Context context=Main14Activity.this;

    private Spinner m14_sp_type;
    private TextView m14_tv_content;
    private Button m14_btn_copy,m14_btn_next;

    private String[] title = {"毒鸡汤", "舔狗日记", "社会语录"};
    private String type=title[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main14);

        m14_btn_copy=findViewById(R.id.m14_btn_copy);
        m14_btn_next=findViewById(R.id.m14_btn_next);
        m14_tv_content=findViewById(R.id.m14_tv_content);
        m14_sp_type=findViewById(R.id.m14_sp_type);

        init();
        getData(type);
    }

    private void init() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, R.layout.item_list_text, title);
        m14_sp_type.setAdapter(adapter);

        m14_sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = title[i];
                getData(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        m14_btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData(type);
            }
        });
        m14_btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence text = m14_tv_content.getText();
                if (!StrUtil.isEmpty(text)){
                    boolean b = SystemUtil.copyStr(context, text.toString());
                    if (b){
                        Toast.makeText(context,"文案已成功复制到剪切板中！",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context,"文案为空，无法复制！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getData(String type){
        DialogUtil dialogUtil = new DialogUtil(context);
        String url="https://du.liuzhijin.cn/";
        if (type.equals(title[1])){
            url="https://du.liuzhijin.cn/dog.php";
        }else if (type.equals(title[2])){
            url="https://du.liuzhijin.cn/yulu.php";
        }
        m14_tv_content.setText("");
        m14_btn_copy.setVisibility(View.GONE);
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(Tag, e.toString());
                        dialogUtil.closeProgressDialog();
                        DialogUtil.dialog(context,"文案获取错误", OkHttpMessageUtil.error(e));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String text = Jsoup.parse(response).body().getElementById("text").text();
                        m14_tv_content.setText(text);
                        m14_btn_copy.setVisibility(View.VISIBLE);
                        dialogUtil.closeProgressDialog();
                    }
                });
    }
}
