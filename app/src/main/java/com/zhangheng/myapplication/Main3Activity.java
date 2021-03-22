package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhangheng.myapplication.Object.Resuilt;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class Main3Activity extends AppCompatActivity {

    private ListView listView;
    private AlertDialog.Builder builder;
    private SharedPreferences sharedPreferences;
    private String[] strArr = new String[] {
            "原生OkHttp的Get和Post请求文本数据",//1
            "使用OkHttpUtil的Post提交文本数据",//2
            "使用OkHttpUtil下载大文件",//4
            "上传文件和检索文件",//5
            "请求单张图片并显示",//6
            "查询天气列表（API）",//7
            "生成二维码（API）",//8
            "新华字典查询（API）",//9
            "图书电商查询（API）",//10
            "查询文件列表并下载（自制服务器）",//11
            "地图（高德地图）",//12
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        listView=findViewById(R.id.list_view_1);
        setAdapter();
        getupdatelist();
    }
    private void setAdapter(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                Main3Activity.this, R.layout.item_list_text, strArr);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(Main3Activity.this,"点击："+i,Toast.LENGTH_SHORT).show();
                Intent intent;
                switch (i){
                    case 0:
                        intent=new Intent(Main3Activity.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent=new Intent(Main3Activity.this,Main2Activity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent=new Intent(Main3Activity.this,Main4Activity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent=new Intent(Main3Activity.this,Main5Activity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent=new Intent(Main3Activity.this,Main6Activity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent=new Intent(Main3Activity.this,Main7Activity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent=new Intent(Main3Activity.this,Main8Activity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        intent=new Intent(Main3Activity.this,Main9Activity.class);
                        startActivity(intent);
                        break;
                    case 8:
                        intent=new Intent(Main3Activity.this,Main10Activity.class);
                        startActivity(intent);
                        break;
                    case 9:
                        intent=new Intent(Main3Activity.this,Main11Activity.class);
                        startActivity(intent);
                        break;
                    case 10:
                        intent=new Intent(Main3Activity.this,M12_LoginActivity.class);
                        startActivity(intent);
                        break;

                }
            }
        });
    }

    public void getupdatelist(){
        String url=getResources().getString(R.string.upload_html_url)
                +"filelist/updatelist/"+getResources().getString(R.string.app_name);
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (e.getMessage().indexOf("404")>1||e.getMessage().indexOf("not found")>1){
                            Toast.makeText(Main3Activity.this,"服务器未未连接",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(Main3Activity.this,"错误："+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Resuilt resuilt=null;
                        Gson gson=new Gson();
                        try {
                            resuilt = gson.fromJson(response, Resuilt.class);
                        }catch (Exception e){

                        }
                        if (resuilt!=null) {
                            Toast.makeText(Main3Activity.this,"服务器已连接",Toast.LENGTH_SHORT).show();
                            if (!resuilt.getTitle().equals("null")) {
                                if (resuilt.getTitle().equals( getResources().getString(R.string.app_name))) {
                                    sharedPreferences=getSharedPreferences("update",MODE_PRIVATE);
                                    String urlname = sharedPreferences.getString("urlname", "");
                                    if (!urlname.equals(resuilt.getMessage())) {
                                        showUpdate(resuilt.getMessage());
                                    }else {
                                        Log.d("urlname","urlname与更新地址一致");
                                    }
                                }else {
                                    Log.d("title","title与应用的名称不一致");
                                }
                            } else {
                                Log.d("title","title为null");
                            }
                        }else {
                            Log.d("resuilt","resuilt为空");
                        }
                    }
                });
    }
    public void showUpdate(final String name){
        String[] strings=name.split("/");
        String appname=strings[strings.length-1].replace(".apk","");
        String[] s = appname.split("_");
        String app;
        if (s.length>1){
            app=s[s.length-1];
        }else {
            app=s[0];
        }
        builder=new AlertDialog.Builder(this)
                .setTitle("更新")
                .setMessage("有新的版本《"+app+"》可以更新，是否去下载更新包？" +
                        "\n如果更新后还弹出更新，可以选不在弹出")
                .setPositiveButton("去更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        String url = getResources().getString(R.string.upload_html_url)
                                +"downloads/downupdate/"+name;
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                })
        .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        })
        .setNeutralButton("不再弹出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog.Builder builder1=new AlertDialog.Builder(Main3Activity.this)
                        .setTitle("提示")
                        .setMessage("不在弹出代表<相同版本>的更新不在提示，如果有其他版本，还会继续提示更新")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sharedPreferences=getSharedPreferences("update",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("urlname",name);
                                editor.apply();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder1.create().show();
            }
        });
        builder.create().show();
    }
}
