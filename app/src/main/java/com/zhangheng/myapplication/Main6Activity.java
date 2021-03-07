package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.io.IOException;

import okhttp3.Call;

public class Main6Activity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private EditText editText;
    private ProgressBar progressBar;
    private TextView tv_pro,textView;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        button=findViewById(R.id.btn_img);
        editText=findViewById(R.id.et_img_url);
        progressBar=findViewById(R.id.progress_img);
        tv_pro=findViewById(R.id.text_progress_img);
        textView=findViewById(R.id.text_img);
        imageView=findViewById(R.id.iv_img);

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_img:
                progressBar.setProgress(0);
                tv_pro.setText("0%");
                textView.setText("");
                imageView.setImageBitmap(null);
                String url = editText.getText().toString();
                if (url.length()<4){
                    Toast.makeText(Main6Activity.this,"输入内容不能为空或过短",Toast.LENGTH_SHORT).show();
                }else {
                    getImage(url);
                }
                break;
        }
    }
    public void  getImage(String url){
        OkHttpUtils
                .get()//
                .url(url)//
                .build()//
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        textView.setText("错误："+e.getMessage());
                    }
                    @Override
                    public void onResponse(Bitmap response, int id) {
                        imageView.setImageBitmap(response);
                        progressBar.setProgress((int) (100));
                        tv_pro.setText(100 + "%");
                    }
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        progressBar.setProgress((int) (progress * 100));
                        tv_pro.setText((int) (progress * 100) + "%");
                        if (progress==1){
                            Toast.makeText(Main6Activity.this,"加载完毕！",Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }
}
