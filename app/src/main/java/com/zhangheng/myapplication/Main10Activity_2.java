package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class Main10Activity_2 extends AppCompatActivity {
    private TextView m10_2_book_title,m10_2_book_catalog,m10_2_book_bytime,
            m10_2_bookt_tags,m10_2_bookt_reading,m10_2_bookt_sub1,m10_2_bookt_sub2;
    private ImageView m10_2_book_image;
    private String title,catalog,tags,sub1,sub2,reading,img,online,bytime;

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
        m10_2_bookt_sub2.setText(sub2);

    }
}
