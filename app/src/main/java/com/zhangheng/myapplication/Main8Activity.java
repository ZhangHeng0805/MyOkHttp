package com.zhangheng.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangheng.myapplication.permissions.ReadAndWrite;
import com.zhangheng.myapplication.util.DisplyUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;

public class Main8Activity extends AppCompatActivity {
    private EditText m8_et_message;
    private Button m8_btn_submit;
    private ProgressBar m8_progress;
    private TextView m8_text_progress;
    private ImageView m8_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main8);
        m8_et_message=findViewById(R.id.m8_et_message);
        m8_btn_submit=findViewById(R.id.m8_btn_submit);
//        m8_progress=findViewById(R.id.m8_progress);
//        m8_text_progress=findViewById(R.id.m8_text_progress);
        m8_image=findViewById(R.id.m8_image);

        m8_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = m8_et_message.getText().toString();
                getQRImage(text);
            }
        });
    }

    private void getQRImage(String text) {
        String url="http://apis.juhe.cn/qrcode/api";
        String key=getResources().getString(R.string.key_QRImage);
        int width = DisplyUtil.getScreenWidth(this);
        int w= (int) (width*0.9);
        String W= String.valueOf(w);
        Map<String,String> param=new HashMap<>();
        Map<String,Integer> param1=new HashMap<>();
        param.put("key",key);
        param.put("text",text);//二维码内容
        param.put("el","h");//纠错等级，el可用值：h\q\m\l，例如：h
        param.put("bgcolor",null);//背景色代码，例如：ffffff
        param.put("fgcolor",null);//前景色代码，例如：000000
        param.put("logo",null);//logo图片URL地址或base64encode编码的图片内容，需要urlencode
        param.put("w",  W);//尺寸大小（像素），例如：300
        param.put("m","10");//边距大小（像素），例如：10
        param.put("lw",null);//logo宽度（像素），例如：60
        param.put("type", "2");//返回模式，1:二维码图片以base64encode编码返回 2:直接返回二维码图像，默认1

        final boolean b = ReadAndWrite.RequestPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);//写入权限
        final String dir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/";
        if (b) {
            OkHttpUtils
                    .get()
                    .url(url)
                    .params(param)
                    .build()
                    .execute(new BitmapCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(Main8Activity.this, "错误" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(final Bitmap response, int id) {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");//设置日期格式
                            String time = df.format(new Date());// new Date()为获取当前系统时间
                            String name= time+"@星曦向荣二维码.png";
//                            String et=m8_et_message.getText().toString().replace("/","*");
//                            int l=15;
//                            if (et.length()>l) {
//                                name= et.substring(0,l)+"_二维码.png";
//                            }else {
//                                name= et+"_二维码.png";
//                            }
                            AlertDialog.Builder builder=new AlertDialog.Builder(Main8Activity.this);
                            builder.setTitle("是否保存？");
                            builder.setMessage("是如将二维码保存到本地（根目录下）");
                            final String finalName = name;
                            builder.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).setPositiveButton("保存", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    File file=new File(dir,finalName);
                                    if (!file.exists()){
                                        try {
                                            if (!file.createNewFile()){
                                                Toast.makeText(Main8Activity.this, "文件保存失败" , Toast.LENGTH_SHORT).show();
                                            }
                                        }catch (Exception e){
                                            Toast.makeText(Main8Activity.this, "错误" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        try {
                                            FileOutputStream outputStream = new FileOutputStream(file);
                                            response.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                            outputStream.flush();
                                            outputStream.close();
                                            Toast.makeText(Main8Activity.this, "图片保存成功！路径："+file.getAbsolutePath() , Toast.LENGTH_LONG).show();
                                        } catch (Exception e) {
                                            Toast.makeText(Main8Activity.this, "错误" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        Toast.makeText(Main8Activity.this, "图片已存在", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.show();
                            m8_image.setImageBitmap(response);
                        }

                    });
        }else {
            Toast.makeText(Main8Activity.this,"没有打开文件写入权限",Toast.LENGTH_SHORT).show();
        }
    }

}
