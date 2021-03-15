package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangheng.myapplication.permissions.ReadAndWrite;
import com.zhangheng.myapplication.util.LocalFileTool;
import com.zhangheng.myapplication.util.OpenFile;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class Main5Activity extends AppCompatActivity implements View.OnClickListener {

    private SlidingDrawer m5_SD;
    private EditText editText1,m5_et_url,m5_et_name,m5_et_password;
    private TextView tv_pro,textView;
    private ProgressBar progressBar;
    private Button button1,button2,m5_btn_intent_url;
    private ListView listView;
    private RadioGroup radioGroup;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private String[] strings=LocalFileTool.imageType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        editText1=findViewById(R.id.et_upload_01);
        button1=findViewById(R.id.btn_upload_01);
        listView=findViewById(R.id.list_view_upload);
        button2=findViewById(R.id.btn_uploadfile);
        progressBar=findViewById(R.id.progress_uploadfile);
        tv_pro=findViewById(R.id.text_progress_uploadfile);
        textView=findViewById(R.id.text_uploadfile);
        m5_et_url=findViewById(R.id.m5_et_url);
        m5_et_name=findViewById(R.id.m5_et_name);
        m5_et_password=findViewById(R.id.m5_et_password);
        m5_SD=findViewById(R.id.m5_SD);
        m5_btn_intent_url=findViewById(R.id.m5_btn_intent_url);
        m5_btn_intent_url.setOnClickListener(this);

        radioGroup=findViewById(R.id.rg_upload);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rd_upload_01:
                        strings=LocalFileTool.imageType;
                        break;
                    case R.id.rd_upload_02:
                        strings=LocalFileTool.videoType;
                        break;
                    case R.id.rd_upload_03:
                        strings=LocalFileTool.audioType;
                        break;
                    case R.id.rd_upload_04:
                        strings=LocalFileTool.docType;
                        break;
                    case R.id.rd_upload_05:
                        strings=LocalFileTool.zipType;
                        break;
                }
            }
        });
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }


    public void multiFileUpload(String url,String username,String password,String filepath)
    {
        String[] strs=filepath.split("/");
        String name=strs[strs.length-1];
//        File file = new File(Environment.getExternalStorageDirectory(), "baidu图标.png");
        File file = new File(filepath);
        if (!file.exists()/*||!file2.exists()*/)
        {
            Toast.makeText(Main5Activity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
//        String url = mBaseUrl + "user!uploadFile";
        OkHttpUtils
                .post()//
                .addFile("file", name, file)//
//                .addFile("mFile", "test1.txt", file2)//
                .url(url)
                .params(params)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("错误：",e.getMessage());
                        textView.setText("错误："+e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        textView.setText(response);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        progressBar.setProgress((int) (progress*100));
                        tv_pro.setText((int) (progress*100)+"%");
                        if (progress==1){
                            Toast.makeText(Main5Activity.this, "上传完成", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_uploadfile:
                progressBar.setProgress(0);
                tv_pro.setText("0%");
                textView.setText("");
                String url=m5_et_url.getText().toString();
                String username=m5_et_name.getText().toString();
                String password=m5_et_password.getText().toString();
                String path = editText1.getText().toString();
                if (path.length()<4){
                    Toast.makeText(Main5Activity.this,"请输入文件路径或路径过短",Toast.LENGTH_SHORT).show();
                }else {
                    multiFileUpload(url,username,password,path);
                }
                break;
            case R.id.btn_upload_01:

                Toast.makeText(Main5Activity.this,"检索中，请稍后。。。",Toast.LENGTH_SHORT).show();

                boolean b = ReadAndWrite.RequestPermissions(this, PERMISSIONS_STORAGE[0]);
                if (b){
                    String s = editText1.getText().toString();
//                    strings=LocalFileTool.audioType;
                    LocalFileTool.readFile(strings, this, new LocalFileTool.IReadCallBack() {
                    @Override
                    public void callBack(List<String> localPath) {
                        for (String path:localPath){
                            Log.e("图片地址:",path);
                        }
                        final String[] localpath=new String[localPath.size()];
                        localPath.toArray(localpath);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                Main5Activity.this, R.layout.item_list_text, localpath);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                editText1.setText(localpath[i]);
                                File file=new File(localpath[i]);
                                openFile(file);
                            }
                        });
                    }
                });

                }else {
                    Toast.makeText(Main5Activity.this, "没有权限，请先获取权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.m5_btn_intent_url:
                String html=getResources().getString(R.string.upload_html_url);
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(html));
                startActivity(intent);
                break;
        }
    }
    private void openFile(File file){
        try {
//        Uri uri = Uri.parse("file://"+file.getAbsolutePath());
        Intent intent1 = new Intent();
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent1.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent1.setDataAndType(/*uri*/Uri.fromFile(file), type);
        //跳转
        startActivity(intent1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private String getMIMEType(File file)
    {
        String type="*/*";
        String fName=file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
        /* 获取文件的后缀名 */
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }
    private final String[][] MIME_MapTable={
            //{后缀名，    MIME类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",      "image/bmp"},
            {".c",        "text/plain"},
            {".class",    "application/octet-stream"},
            {".conf",    "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",    "application/x-gtar"},
            {".gz",        "application/x-gzip"},
            {".h",        "text/plain"},
            {".htm",    "text/html"},
            {".html",    "text/html"},
            {".jar",    "application/java-archive"},
            {".java",    "text/plain"},
            {".jpeg",    "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js",        "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",    "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",    "video/mp4"},
            {".mpga",    "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".prop",    "text/plain"},
            {".rar",    "application/x-rar-compressed"},
            {".rc",        "text/plain"},
            {".rmvb",    "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh",        "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            //{".xml",    "text/xml"},
            {".xml",    "text/plain"},
            {".z",        "application/x-compress"},
            {".zip",    "application/zip"},
            {"",        "*/*"}
    };
}
