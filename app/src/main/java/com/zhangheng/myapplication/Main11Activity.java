package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.gson.Gson;
import com.zhangheng.myapplication.bean.FileList;
import com.zhangheng.myapplication.permissions.ReadAndWrite;
import com.zhangheng.myapplication.util.TimeUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class Main11Activity extends AppCompatActivity implements View.OnClickListener {
    private Button m11_btn_downloadfile,m11_btn_loadlist;
    private TextView m11_text_loadresult,m11_text_progress;
    private EditText m11_et_upload_01,m11_et_name,m11_et_password;
    private RadioGroup m11_rg_upload;
    private ProgressBar m11_progress;
    private ListView m11_listview_load;
    private SlidingDrawer m11_SD;
    private String type="all",filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main11);
        m11_btn_downloadfile=findViewById(R.id.m11_btn_downloadfile);
        m11_btn_loadlist=findViewById(R.id.m11_btn_loadlist);
        m11_text_loadresult=findViewById(R.id.m11_text_loadresult);
        m11_text_progress=findViewById(R.id.m11_text_progress);
        m11_et_upload_01=findViewById(R.id.m11_et_upload_01);
        m11_rg_upload=findViewById(R.id.m11_rg_upload);
        m11_progress=findViewById(R.id.m11_progress);
        m11_listview_load=findViewById(R.id.m11_listview_load);
        m11_et_name=findViewById(R.id.m11_et_name);
        m11_et_password=findViewById(R.id.m11_et_password);
        m11_SD=findViewById(R.id.m11_SD);

        m11_btn_downloadfile.setOnClickListener(this);
        m11_btn_loadlist.setOnClickListener(this);

        m11_rg_upload.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.m11_rd_upload_00:
                        type="all";
                        break;
                    case R.id.m11_rd_upload_01:
                        type="image";
                        break;
                    case R.id.m11_rd_upload_02:
                        type="video";
                        break;
                    case R.id.m11_rd_upload_03:
                        type="audio";
                        break;
                    case R.id.m11_rd_upload_04:
                        type="text";
                        break;
                    case R.id.m11_rd_upload_05:
                        type="application";
                        break;
                    case R.id.m11_rd_upload_06:
                        type="other";
                        break;
                }
            }
        });
        m11_et_upload_01.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = m11_et_upload_01.getText().toString();
                String file_name = name.substring(name.indexOf("//")+2);
                String[] s=file_name.split("/");
                filename=s[s.length-1];
                if (filename.length()>65){
                    filename = filename.substring(filename.length() - 65);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }
    public void getFileList(String url,String username,String password){

        Map<String,String> map=new HashMap<>();
        map.put("username",username);
        map.put("password",password);
        OkHttpUtils
                .post()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        String time = TimeUtil.getTime();
                        m11_text_loadresult.setText(m11_text_loadresult.getText().toString()+"\n"+time+" -- 错误："+e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        List<String> l=new ArrayList<>();
                        try {
                            JSONArray json=new JSONArray(response);
                            for (int i=0;i<json.length();i++){
                                String s=(String) json.get(i);
                                l.add(s.replace("\\","/"));
                            }
                        } catch (JSONException e) {
                            String time = TimeUtil.getTime();
                            e.printStackTrace();
                            m11_text_loadresult.setText(m11_text_loadresult.getText().toString()+"\n"+time+" -- 错误："+e.getMessage());
                        }

                        final String[] strings=new String[l.size()];
                        l.toArray(strings);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                Main11Activity.this, R.layout.item_list_text, strings);
                        m11_listview_load.setAdapter(adapter);
                        m11_listview_load.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                m11_et_upload_01.setText(getResources().getString(R.string.zhangheng_url)+"downloads/show/"+strings[i]);
                            }
                        });
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        int i= (int) (progress*100);
                        m11_progress.setProgress(i);
                        m11_text_progress.setText(i+"%");

                    }
                });
    }
    public void downloadFile(String url,String filename){
//        ActivityCompat.requestPermissions(Main4Activity.this,PERMISSIONS_STORAGE,100);
        boolean b = ReadAndWrite.RequestPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);//写入权限
        if (b) {
            OkHttpUtils
                    .get()
                    .url(url)
                    .build()
                    .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), filename) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            String time = TimeUtil.getTime();
                            e.printStackTrace();
                            m11_text_loadresult.setText(m11_text_loadresult.getText().toString()+"\n"+ time +" -- 错误："+e.getMessage());


                        }

                        @Override
                        public void onResponse(File response, int id) {
                            Log.e("路径", "response:" + response.getAbsolutePath());
                            String time = TimeUtil.getTime();
                            m11_text_loadresult.setText(m11_text_loadresult.getText().toString()+"\n"+time+" -- 下载完成！存储路径：" + response.getAbsolutePath());
                            Toast.makeText(Main11Activity.this, "下载完成", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void inProgress(float progress, long total, int id) {
//                            super.inProgress(progress, total, id);
                            m11_progress.setProgress((int) (progress * 100));
                            m11_text_progress.setText((int) (progress * 100) + "%");
                            if (progress == 1) {
                                Toast.makeText(Main11Activity.this, "下载完成", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            Toast.makeText(Main11Activity.this, "没有权限，请先获取权限", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.m11_btn_downloadfile:
                m11_progress.setProgress(0);
                m11_text_progress.setText(0+"%");
                String downloadfile_url;
                if (m11_et_upload_01.getText().toString().startsWith("http")){
                    downloadfile_url=m11_et_upload_01.getText().toString();
                }else {
                    downloadfile_url=getResources().getString(R.string.zhangheng_url)+"downloads/show/"+m11_et_upload_01.getText().toString();
                }
                if (filename!=null){
                    downloadFile(downloadfile_url,filename);
                }else {
                    Toast.makeText(Main11Activity.this, "请先选择文件", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.m11_btn_loadlist:
                m11_progress.setProgress(0);
                m11_text_progress.setText(0+"%");
                String loadlist_url=getResources().getString(R.string.zhangheng_url)+"filelist/jsonlist/"+type;
                String username=m11_et_name.getText().toString();
                String password=m11_et_password.getText().toString();
                getFileList(loadlist_url,username,password);
                break;
        }
    }
}
