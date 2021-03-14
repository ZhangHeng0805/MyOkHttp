package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhangheng.myapplication.util.WebPageUtil;

import java.util.Set;

public class Main10Activity_2 extends AppCompatActivity implements View.OnClickListener{
    private TextView m10_2_book_title,m10_2_book_catalog,m10_2_book_bytime,
            m10_2_bookt_tags,m10_2_bookt_reading,m10_2_bookt_sub1,m10_2_bookt_sub2;
    private ImageView m10_2_book_image;
    private Button m10_2_book_buy1,m10_2_book_buy2,m10_2_book_buy3,m10_2_book_buy4;
    private String title,catalog,tags,sub1,sub2,reading,img,online,bytime;
    private String buy1="",buy2="",buy3="",buy4="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main10_2);

        Intent intent=getIntent();
        title = intent.getStringExtra("title");
        catalog=intent.getStringExtra("catalog");
        tags=intent.getStringExtra("tags");
        sub1=intent.getStringExtra("sub1");
        sub2=intent.getStringExtra("sub2");
        img=intent.getStringExtra("img");
        reading=intent.getStringExtra("reading");
        online=intent.getStringExtra("online");
        bytime=intent.getStringExtra("bytime");

        m10_2_book_image=findViewById(R.id.m10_2_book_image);
        m10_2_book_title=findViewById(R.id.m10_2_book_title);
        m10_2_book_catalog=findViewById(R.id.m10_2_book_catalog);
        m10_2_book_bytime=findViewById(R.id.m10_2_book_bytime);
        m10_2_bookt_tags=findViewById(R.id.m10_2_bookt_tags);
        m10_2_bookt_reading=findViewById(R.id.m10_2_bookt_reading);
        m10_2_bookt_sub1=findViewById(R.id.m10_2_bookt_sub1);
        m10_2_bookt_sub2=findViewById(R.id.m10_2_bookt_sub2);
        m10_2_book_buy1=findViewById(R.id.m10_2_book_buy1);
        m10_2_book_buy2=findViewById(R.id.m10_2_book_buy2);
        m10_2_book_buy3=findViewById(R.id.m10_2_book_buy3);
        m10_2_book_buy4=findViewById(R.id.m10_2_book_buy4);
        String s=title+"\n"
                +catalog+"\n"
                +tags+"\n"
                +sub1+"\n"
                +sub2+"\n"
                +img+"\n"
                +reading+"\n"
                +online+"\n"
                +bytime+"\n";
        Glide.with(this).load(img).into(m10_2_book_image);
        m10_2_book_title.setText("《"+title+"》");
        m10_2_book_catalog.setText(catalog);
        m10_2_book_bytime.setText(bytime);
        m10_2_bookt_tags.setText(tags);
        m10_2_bookt_reading.setText(reading);
        m10_2_bookt_sub1.setText(sub1);
        Set<String> urls = WebPageUtil.extractUrls(online);
//        String buy1="";//京东
//        String buy2="";//当当
//        String buy3="";//亚马逊
//        String buy4="";//苏宁
        for (String str:urls){
            if (str.indexOf("//book.")>1||str.indexOf("jd.com")>1){
                buy1=str;
            }else if (str.indexOf("dangdang.com")>1){
                buy2=str;
            }else if (str.indexOf("www.amazon.cn")>1){
                buy3=str;
            }else if (str.indexOf("www.suning.com")>1){
                buy4=str;
            }
        }
        if (buy1.isEmpty()){
            m10_2_book_buy1.setVisibility(View.GONE);
        }
        if (buy2.isEmpty()){
            m10_2_book_buy2.setVisibility(View.GONE);
        }
        if (buy3.isEmpty()){
            m10_2_book_buy3.setVisibility(View.GONE);
        }
        if (buy4.isEmpty()){
            m10_2_book_buy4.setVisibility(View.GONE);
        }
        m10_2_bookt_sub2.setText(sub2);

        m10_2_book_buy1.setOnClickListener(this);
        m10_2_book_buy2.setOnClickListener(this);
        m10_2_book_buy3.setOnClickListener(this);
        m10_2_book_buy4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.m10_2_book_buy1:
                intent=new Intent();//创建Intent对象
                intent.setAction(Intent.ACTION_VIEW);//为Intent设置动作
                intent.setData(Uri.parse(buy1));//为Intent设置数据
                startActivity(intent);//将Intent传递给Activity
                break;
            case R.id.m10_2_book_buy2:
                intent=new Intent();//创建Intent对象
                intent.setAction(Intent.ACTION_VIEW);//为Intent设置动作
                intent.setData(Uri.parse(buy2));//为Intent设置数据
                startActivity(intent);//将Intent传递给Activity
                break;
            case R.id.m10_2_book_buy3:
                intent=new Intent();//创建Intent对象
                intent.setAction(Intent.ACTION_VIEW);//为Intent设置动作
                intent.setData(Uri.parse(buy3));//为Intent设置数据
                startActivity(intent);//将Intent传递给Activity
                break;
            case R.id.m10_2_book_buy4:
                intent=new Intent();//创建Intent对象
                intent.setAction(Intent.ACTION_VIEW);//为Intent设置动作
                intent.setData(Uri.parse(buy4));//为Intent设置数据
                startActivity(intent);//将Intent传递给Activity
                break;
        }
    }
}
