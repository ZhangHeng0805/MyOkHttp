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

import java.io.FileOutputStream;

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
        param.put("text",text);//???????????????
        param.put("el","h");//???????????????el????????????h\q\m\l????????????h
        param.put("bgcolor",null);//???????????????????????????ffffff
        param.put("fgcolor",null);//???????????????????????????000000
        param.put("logo",null);//logo??????URL?????????base64encode??????????????????????????????urlencode
        param.put("w",  W);//????????????????????????????????????300
        param.put("m","10");//????????????????????????????????????10
        param.put("lw",null);//logo??????????????????????????????60
        param.put("type", "2");//???????????????1:??????????????????base64encode???????????? 2:????????????????????????????????????1

        final boolean b = ReadAndWrite.RequestPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);//????????????
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
                            Toast.makeText(Main8Activity.this, "??????" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(final Bitmap response, int id) {
                            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");//??????????????????
                            String time = df.format(new Date());// new Date()???????????????????????????
                            String name= time+"@?????????????????????.png";
//                            String et=m8_et_message.getText().toString().replace("/","*");
//                            int l=15;
//                            if (et.length()>l) {
//                                name= et.substring(0,l)+"_?????????.png";
//                            }else {
//                                name= et+"_?????????.png";
//                            }
                            AlertDialog.Builder builder=new AlertDialog.Builder(Main8Activity.this);
                            builder.setTitle("???????????????");
                            builder.setMessage("????????????????????????????????????"+dir+"????????????");
                            final String finalName = name;
                            builder.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    File file=new File(dir,finalName);
                                    if (!file.exists()){
                                        try {
                                            if (!file.createNewFile()){
                                                Toast.makeText(Main8Activity.this, "??????????????????" , Toast.LENGTH_SHORT).show();
                                            }
                                        }catch (Exception e){
                                            Toast.makeText(Main8Activity.this, "??????" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        try {
                                            FileOutputStream outputStream = new FileOutputStream(file);
                                            response.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                            outputStream.flush();
                                            outputStream.close();
                                            Toast.makeText(Main8Activity.this, "??????????????????????????????"+file.getAbsolutePath() , Toast.LENGTH_LONG).show();
                                        } catch (Exception e) {
                                            Toast.makeText(Main8Activity.this, "??????" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        Toast.makeText(Main8Activity.this, "???????????????", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.show();
                            m8_image.setImageBitmap(response);
                        }

                    });
        }else {
            Toast.makeText(Main8Activity.this,"??????????????????????????????",Toast.LENGTH_SHORT).show();
        }
    }

}
