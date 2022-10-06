package com.zhangheng.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
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
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialogUtil.closeProgressDialog();
                        Toast.makeText(Main10Activity.this, "错误：" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(Main10Activity.this, "没有请求到数据", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            switch (resultcode){
                                case "112":
                                    Toast.makeText(Main10Activity.this, "今天请求超过次数限制（100次）", Toast.LENGTH_SHORT).show();
                                    break;
                                case "120":
                                    Toast.makeText(Main10Activity.this, "系统维护，暂时停用", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(Main10Activity.this, "错误码："+resultcode, Toast.LENGTH_SHORT).show();
                                    break;
                            }

                        }
                        dialogUtil.closeProgressDialog();
                    }
                });

    }
}
