package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangheng.myapplication.permissions.ReadAndWrite;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

public class Main4Activity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText,editText_name;
    private Button button;
    private ProgressBar progressBar;
    private TextView textView,textView_pro;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        editText= findViewById(R.id.et_downfile_url);
        editText_name= findViewById(R.id.et_downfile_name);
        button= findViewById(R.id.btn_downfile);
        progressBar= findViewById(R.id.progress_downfile);
        textView= findViewById(R.id.text_downfile);
        textView_pro= findViewById(R.id.text_progress_downfile);
        button.setOnClickListener(this);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String url = editText.getText().toString();
                String filename = url.substring(url.indexOf("//")+2)/*.replace("/","_")*/;
                String[] s=filename.split("/");
                editText_name.setText(s[s.length-1]);
                if (editText_name.getText().toString().length()>65){
                    editText_name.setText("");
                    Toast ts=null;
                    Toast toast=Toast.makeText(Main4Activity.this, "???????????????", Toast.LENGTH_SHORT);
                    if (ts==null){
                        ts=toast;
                    }else {
                        ts.cancel();
                        ts=toast;
                    }
                    ts.show();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /*
    * ???????????????
    * */

    public void downloadFile(String url,String filename){
//        ActivityCompat.requestPermissions(Main4Activity.this,PERMISSIONS_STORAGE,100);
        boolean b = ReadAndWrite.RequestPermissions(this, PERMISSIONS_STORAGE[1]);//????????????
        if (b) {
            OkHttpUtils
                    .get()
                    .url(url)
                    .build()
                    .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), filename) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            e.printStackTrace();
                            textView.setText("?????????" + e.getMessage());

                            
                        }

                        @Override
                        public void onResponse(File response, int id) {
                            Log.e("??????", "response:" + response.getAbsolutePath());
                            textView.setText("????????????\n???????????????" + response.getAbsolutePath());
                        }

                        @Override
                        public void inProgress(float progress, long total, int id) {
//                            super.inProgress(progress, total, id);
                            progressBar.setProgress((int) (progress * 100));
                            textView_pro.setText((int) (progress * 100) + "%");
                            if (progress == 1) {
                                Toast.makeText(Main4Activity.this, "????????????", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            Toast.makeText(Main4Activity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_downfile:
                progressBar.setProgress(0);
                String url = editText.getText().toString();
                if (url.length()<4){
                    Toast.makeText(Main4Activity.this,"??????????????????",Toast.LENGTH_SHORT).show();

                }
                String filename=editText_name.getText().toString();
                if (filename.length()<3) {
                    filename= url.substring(url.indexOf("//") + 2).replace("/", "_");
                }
                //                String filename = "1.png";
                downloadFile(url,filename);
                break;
        }
    }
    private void requestMyPermissions() {//????????????????????????

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //???????????????????????????????????????
            ActivityCompat.requestPermissions(Main4Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d("??????", "requestMyPermissions: ??????SD??????");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //???????????????????????????????????????
            ActivityCompat.requestPermissions(Main4Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d("??????", "requestMyPermissions: ??????SD??????");
        }
    }
}
