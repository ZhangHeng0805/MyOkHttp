package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Main10Activity_2 extends AppCompatActivity {
    private TextView t1;
    private String title,catalog,tags,sub1,sub2,reading,img,online,bytime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main10_2);
        t1=findViewById(R.id.m10_2_text);
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

        String s=title+"\n"
                +catalog+"\n"
                +tags+"\n"
                +sub1+"\n"
                +sub2+"\n"
                +img+"\n"
                +reading+"\n"
                +online+"\n"
                +bytime+"\n";
        t1.setText(s);
    }
}
