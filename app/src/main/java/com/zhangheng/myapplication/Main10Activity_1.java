package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhangheng.myapplication.adapter.BookList_Adapter;
import com.zhangheng.myapplication.bean.books.BooksRootBean;
import com.zhangheng.myapplication.bean.books.Result;
import com.zhangheng.myapplication.bean.books.bookslist.BookListBean;
import com.zhangheng.myapplication.bean.books.bookslist.Data;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class Main10Activity_1 extends AppCompatActivity {
    private String id,name;
    private TextView title;
    private ListView listView;

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
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(Main10Activity_1.this, "错误：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        BookListBean bean = gson.fromJson(response, BookListBean.class);
                        if (bean.getError_code()==0){
                            final List<Data> data = bean.getResult().getData();
                            listView.setAdapter(new BookList_Adapter(Main10Activity_1.this,data, new BookList_Adapter.OnItemClickListen() {
                                @Override
                                public void onItemClick(int position) {
                                    Toast.makeText(Main10Activity_1.this,position,Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent();
                                    Data data1 = data.get(position);
                                    intent.putExtra("title",data1.getTitle());
                                    intent.putExtra("catalog",data1.getCatalog());
                                    intent.putExtra("tags",data1.getTags());
                                    intent.putExtra("sub1",data1.getSub1());
                                    intent.putExtra("sub2",data1.getSub2());
                                    intent.putExtra("img",data1.getImg());
                                    intent.putExtra("reading",data1.getReading());
                                    intent.putExtra("online",data1.getOnline());
                                    intent.putExtra("bytime",data1.getBytime());
                                    intent.setClass(Main10Activity_1.this,Main10Activity_2.class);
                                    startActivity(intent);
                                }
                            }));
                        }else {
                            Toast.makeText(Main10Activity_1.this, "错误码：" + bean.getError_code(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }
}
