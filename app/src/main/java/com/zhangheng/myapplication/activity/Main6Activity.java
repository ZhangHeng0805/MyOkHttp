package com.zhangheng.myapplication.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.AndroidImageUtil;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.LocalFileTool;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.util.FormatUtil;
import com.zhangheng.util.TimeUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.Date;

import okhttp3.Call;

public class  Main6Activity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private EditText editText;
    private ProgressBar progressBar;
    private TextView tv_pro,textView;
    private ImageView imageView;
    private final Context context = Main6Activity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        button=findViewById(R.id.btn_img);
        editText=findViewById(R.id.et_img_url);
        progressBar=findViewById(R.id.progress_img);
        tv_pro=findViewById(R.id.text_progress_img);
        textView=findViewById(R.id.m6_txt_img);
        imageView=findViewById(R.id.iv_img);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Drawable drawable = imageView.getDrawable();
                if (drawable!=null){
                    Bitmap bitmap = AndroidImageUtil.drawableToBitmap(drawable);
                    String appname = getString(R.string.app_name);
                    String path= LocalFileTool.BasePath+"/"+ appname +"/download/image/";
                    String name=appname+"_download_"+ TimeUtil.toTime(new Date(),"yyyyMMddHHmmss")+".png";
                    String s = AndroidImageUtil.saveImage(context, bitmap, path, name, Bitmap.CompressFormat.PNG);
                    if (s!=null){
                        DialogUtil.dialog(context,"保存成功！","保存路径："+s.replace(LocalFileTool.BasePath,"内部存储"));
                    }else {
                        Toast.makeText(Main6Activity.this,"保存失败！",Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_img:
                progressBar.setProgress(0);
                tv_pro.setText("0%");

                imageView.setImageBitmap(null);
                String url = editText.getText().toString();
                if (!FormatUtil.isWebUrl(url)){
                    textView.setVisibility(View.GONE);

                    Toast.makeText(context,"请输入正确网址",Toast.LENGTH_SHORT).show();
                }else {
                    textView.setVisibility(View.VISIBLE);
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
                        textView.setText("错误："+ OkHttpMessageUtil.error(e));
                    }
                    @Override
                    public void onResponse(Bitmap response, int id) {
                        imageView.setImageBitmap(response);
                        progressBar.setProgress( (100));
                        tv_pro.setText("100%");
                        textView.setText("长按图片即可保存");
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
