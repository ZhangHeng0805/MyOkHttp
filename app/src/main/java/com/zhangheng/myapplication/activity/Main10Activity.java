package com.zhangheng.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.bean.books.BooksRootBean;
import com.zhangheng.myapplication.bean.books.Result;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class Main10Activity extends AppCompatActivity {

    private ListView m10_list_booktype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main10);
        m10_list_booktype=findViewById(R.id.m10_list_booktype);
        getBooksTypeList();
    }
    private void setAdapter(final List<Result> result){
        List<String> list1=new ArrayList<>();
        for (Result r:result){
            list1.add(r.toString());
        }
        String[] res=new String[list1.size()];
        list1.toArray(res);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                Main10Activity.this, R.layout.item_booktype_list_text,res);
        m10_list_booktype.setAdapter(adapter);
        m10_list_booktype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(Main10Activity.this,"点击："+result.get(i).getCatalog(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("id",result.get(i).getId());
                intent.putExtra("name",result.get(i).getCatalog());
                intent.setClass(Main10Activity.this,Main10Activity_1.class);
                startActivity(intent);

            }
        });
    }
    private void getBooksTypeList() {
        DialogUtil dialogUtil = new DialogUtil(this);
        dialogUtil.createProgressDialog();
        String url = "http://apis.juhe.cn/goodbook/catalog";
        String key = getResources().getString(R.string.key_books);
        Map<String, String> map = new HashMap<>();
        map.put("key", key);
        final List<Result> list = new ArrayList<>();
        OkHttpUtils
                .get()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    private final Main10Activity context = Main10Activity.this;

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialogUtil.closeProgressDialog();
                        Toast.makeText(context, "错误：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        BooksRootBean bean = gson.fromJson(response, BooksRootBean.class);
                        String resultcode = bean.getResultcode();
                        if (resultcode.equals("200")) {
                            List<Result> result = bean.getResult();
                            if (result.size() > 0) {
                                setAdapter(result);
                            } else {
                                Toast.makeText(context, "没有请求到数据", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            String msg = null;
                            if (resultcode.equals("10011")||resultcode.equals("111")){
                                msg="当前IP请求超过限制";
                            }else if (resultcode.equals("10012")||resultcode.equals("112")){
                                msg="请求超过次数限制,请明日再来";
                            }else if (resultcode.equals("10020")||resultcode.equals("120")){
                                msg="功能维护中...";
                            }else if (resultcode.equals("10021")||resultcode.equals("121")){
                                msg="对不起,该功能已停用";
                            }
                            else {
                                msg="错误码："+ resultcode;
                            }
                            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                        }
                        dialogUtil.closeProgressDialog();
                    }
                });

    }
}
