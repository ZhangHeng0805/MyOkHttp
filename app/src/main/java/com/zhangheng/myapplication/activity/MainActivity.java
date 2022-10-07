package com.zhangheng.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangheng.myapplication.Object.User;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.okhttp.okHttp;
import com.zhangheng.myapplication.util.Json;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int GET = 1;
    private static final int POST = 2;
    private Button btn_get,btn_post,btn_submit;
    private TextView text1;
    private EditText editText1;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET:
                    //获取数据
                    try {
                        List<User> u= Json.jsonArray((String) msg.obj);
                        if (u.size()!=0){
                            String s="";
                            for (User user:u){
                                s+=user.toString()+"\n";
                            }
                            text1.setText(s);
                        }else {
                            text1.setText((String) msg.obj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        text1.setText((String) msg.obj);
                    }
                    break;
                case POST:
                    //获取数据
                    text1.setText((String) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       btn_get=findViewById(R.id.btn_get);
       btn_post=findViewById(R.id.btn_post);
       btn_submit=findViewById(R.id.btn_post_sumint);
       text1=findViewById(R.id.text1);
       editText1=findViewById(R.id.edit_url);
       editText1.setText(getResources().getString(R.string.zhangheng_url)+"users");
       setOnclickListener();
    }

    private void setOnclickListener() {
        OnClick1 onClick=new OnClick1();
        btn_get.setOnClickListener(onClick);
        btn_post.setOnClickListener(onClick);
        btn_submit.setOnClickListener(onClick);

    }

    //使用Get请求
    public void getDataFormGet(final String url) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = okHttp.get(url);
                    Log.d("请求结果", "GetRun: " + s);
                    Message message=Message.obtain();
                    message.what=GET;
                    message.obj=s;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //使用Post请求
    public void getDataFromPost(final String url, final String json) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = okHttp.post(url,json);
                    Log.d("请求结果", "PostRun: " + s);
                    Message message=Message.obtain();
                    message.what=POST;
                    message.obj=s;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public class OnClick1 implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_get:
                    try {
                        String url = editText1.getText().toString();
                        if (url.length()<5){
                            Toast.makeText(MainActivity.this,"输入长度过短",Toast.LENGTH_SHORT).show();
                        }else {
//                        String url="https://www.baidu.com";
                            text1.setText("数据请求中。。。");
                            getDataFormGet(url);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_post:
                    try {
                        String url = editText1.getText().toString();
                        if (url.length()<5){
                            Toast.makeText(MainActivity.this,"输入长度过短",Toast.LENGTH_SHORT).show();
                        }else {
//                        String url="https://www.baidu.com";
                            text1.setText("数据请求中。。。");
                            getDataFromPost(url,"");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_post_sumint:
                    Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

}
