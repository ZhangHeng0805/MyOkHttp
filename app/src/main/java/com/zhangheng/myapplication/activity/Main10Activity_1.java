package com.zhangheng.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.adapter.BookList_Adapter;
import com.zhangheng.myapplication.bean.books.bookslist.BookListBean;
import com.zhangheng.myapplication.bean.books.bookslist.Data;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class Main10Activity_1 extends AppCompatActivity  {
    private String id,name;
    private TextView title;
    private ListView listView;
    private List<Data> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main10_1);

        Intent intent = getIntent();
        id= intent.getStringExtra("id");
        name = intent.getStringExtra("name");
//        Toast.makeText(Main10Activity_1.this, "id：" + id+name, Toast.LENGTH_SHORT).show();

        title=findViewById(R.id.m10_1_title);
        listView=findViewById(R.id.m10_1_list);
        title.setText(name+"图书列表");
        getBooksList();

    }
    private void getBooksList() {
        DialogUtil dialogUtil = new DialogUtil(this);
        dialogUtil.createProgressDialog();
        String url = "http://apis.juhe.cn/goodbook/query";
        String key =  getResources().getString(R.string.key_books);
        Map<String, String> map = new HashMap<>();
        map.put("key", key);
        map.put("catalog_id", id);
        map.put("pn", "0");
        map.put("rn", "30");

        OkHttpUtils
                .get()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    private final Main10Activity_1 context = Main10Activity_1.this;

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialogUtil.closeProgressDialog();
                        Toast.makeText(context, "错误：" + OkHttpMessageUtil.error(e), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        BookListBean bean = gson.fromJson(response, BookListBean.class);
                        int error_code = bean.getError_code();
                        if (error_code ==0){
                            data = bean.getResult().getData();
                            listView.setAdapter(new BookList_Adapter(context,data));
                            String rn = bean.getResult().getRn();
                            String totalNum = bean.getResult().getTotalNum();
                            if (Integer.valueOf(rn)>Integer.valueOf(totalNum)){
                                rn=totalNum;
                            }
                            Toast.makeText(context,"加载："+rn+"/"+totalNum,Toast.LENGTH_SHORT).show();

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
