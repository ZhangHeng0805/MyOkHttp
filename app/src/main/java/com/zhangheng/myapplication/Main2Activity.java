package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

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
import com.zhangheng.myapplication.util.Json;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;

public class Main2Activity extends AppCompatActivity {

    private static final int GET = 1;
    private static final int POST = 2;
    private EditText et_url,et_name,et_email;
    private TextView text1;
    private Button btn_submit;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET:
                    //获取数据
                    text1.setText((String) msg.obj);
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
        setContentView(R.layout.activity_main2);
        et_url=findViewById(R.id.et_submit_url);
        et_name=findViewById(R.id.et_submit_name);
        et_email=findViewById(R.id.et_submit_email);
        btn_submit=findViewById(R.id.btn_submit);
        text1=findViewById(R.id.text1_submit);
        setOnClickListener();
    }
    //使用Post请求
    public void getDataFromPost(String url, String a,String b) throws IOException {
                OkHttpUtils
                        .post()
                        .url(url)
                        .addParams("lastName", a)
                        .addParams("email", b)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                e.printStackTrace();
                                text1.setText(e.getMessage());
                            }

                            @Override
                            public void onResponse(String response, int id) {

                                try {
                                    User u= Json.jsonUser(response);
                                    response = u.toString();
                                    text1.setText("返回结果："+response);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    text1.setText("返回结果："+response);
                                }
                                Log.d("请求结果", "PostRun: " + response);
                            }
                        });

    }

    private void setOnClickListener() {
        OnClick onClick =new OnClick();
        btn_submit.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_submit:
                    try {
                        String url=et_url.getText().toString();
                        String name=et_name.getText().toString();
                        String email=et_email.getText().toString();
                        if (name.length()<1&&email.length()<1){
                            Toast.makeText(Main2Activity.this,"输入长度过短",Toast.LENGTH_SHORT).show();
                        }else {
                            text1.setText("数据提交中。。。");
//                            String json ="{\"lastName\":"+name+",\"email\":"+email+"}";
                            getDataFromPost(url,name,email);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
