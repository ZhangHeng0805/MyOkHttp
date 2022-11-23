package com.zhangheng.myapplication.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.bean.dictionary.DictionaryRootBean;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class Main9Activity extends AppCompatActivity {
    private TextView m9_result_zi,m9_result_py,m9_result_bihua
            ,m9_result_bushou,m9_result_wubi,m9_result_pinyin
            ,m9_result_jijie,m9_result_xiangjie;
    private Button m9_btn_query;
    private EditText m9_et_message;
    private final Context context = Main9Activity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main9);
        m9_result_zi=findViewById(R.id.m9_result_zi);
        m9_result_py=findViewById(R.id.m9_result_py);
        m9_result_bihua=findViewById(R.id.m9_result_bihua);
        m9_result_bushou=findViewById(R.id.m9_result_bushou);
        m9_result_wubi=findViewById(R.id.m9_result_wubi);
        m9_result_pinyin=findViewById(R.id.m9_result_pinyin);
        m9_result_jijie=findViewById(R.id.m9_result_jijie);
        m9_result_xiangjie=findViewById(R.id.m9_result_xiangjie);
        m9_btn_query=findViewById(R.id.m9_btn_query);
        m9_et_message=findViewById(R.id.m9_et_message);
        m9_btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = m9_et_message.getText().toString();
                getDictionary(word);

            }
        });
    }

    private void getDictionary(String word){
        DialogUtil dialogUtil = new DialogUtil(this);
        String url="http://v.juhe.cn/xhzd/query";
        String key=getResources().getString(R.string.key_dictionary);
        Map<String,String> map=new HashMap<>();
        map.put("key",key);
        map.put("word",word);
        dialogUtil.createProgressDialog("查询中...");
        OkHttpUtils
                .get()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {


                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialogUtil.closeProgressDialog();
                        Toast.makeText(context,"错误"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson=new Gson();
                        DictionaryRootBean bean = gson.fromJson(response, DictionaryRootBean.class);
                        int error_code = bean.getError_code();
                        if (error_code ==0){
                            //查询的汉字
                            String zi = bean.getResult().getZi();
                            m9_result_zi.setText(zi);
                            //拼音
                            String py = bean.getResult().getPy();
                            m9_result_py.setText(py);
                            //读音
                            String pinyin = bean.getResult().getPinyin();
                            m9_result_pinyin.setText(pinyin);
                            //五笔
                            String wubi = bean.getResult().getWubi();
                            m9_result_wubi.setText(wubi);
                            //部首
                            String bushou = bean.getResult().getBushou();
                            m9_result_bushou.setText(bushou);
                            //笔画数
                            String bihua = bean.getResult().getBihua();
                            m9_result_bihua.setText(bihua);
                            //简解
                            String jiexi="";
                            for (String jx:bean.getResult().getJijie()){
                                jiexi+=jx+"\n";
                            }
                            m9_result_jijie.setText(jiexi);
                            //详解
                            String xiangjie="";
                            for (String xj:bean.getResult().getXiangjie()){
                                xiangjie+=xj+"\n";
                            }
                            m9_result_xiangjie.setText(xiangjie);
                        }else {
                            String msg = null;
                            if (error_code ==10011||error_code==111){
                                msg="当前IP请求超过限制";
                            }else if (error_code ==10012||error_code==112){
                                msg="请求超过次数限制,请明日再来";
                            }else if (error_code ==10020||error_code==120){
                                msg="功能维护中...";
                            }else if (error_code ==10021||error_code==121){
                                msg="对不起,该功能已停用";
                            }
                            else {
                                msg="错误码："+ error_code;
                            }
                            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                        }
                        dialogUtil.closeProgressDialog();
                    }
                });
    }
}
