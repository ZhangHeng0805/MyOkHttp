package com.zhangheng.myapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zhangheng.util.FormatUtil;

/**
 * 手机扫码
 */
public class Main17Activity extends AppCompatActivity {

    private TextView tv_result;
    private Button btn_cope;
    private Button btn_scan,btn_visit;
    private ImageView iv_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main17);
        tv_result=findViewById(R.id.m17_tv_result);
        btn_cope=findViewById(R.id.m17_btn_cope);
        btn_scan=findViewById(R.id.m17_btn_scan);
        iv_img=findViewById(R.id.m17_iv_img);
        btn_visit=findViewById(R.id.m17_btn_visit);

        listener();
        scan();
    }

    private void scan(){
        btn_cope.setVisibility(View.GONE);
        // 创建IntentIntegrator对象
        IntentIntegrator intentIntegrator = new IntentIntegrator(Main17Activity.this);
        // 开始扫描
        intentIntegrator.initiateScan();
    }

    private void listener(){
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
        btn_cope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = tv_result.getText().toString();
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("扫描结果", result);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(Main17Activity.this, "复制成功！", Toast.LENGTH_LONG).show();
                /*try {
                    String image = QRCodeUtil.encode(result);
                    byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    iv_img.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            }
        });
        btn_visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("访问网址".equals(btn_visit.getText())){
                    Uri uri = Uri.parse((String) btn_visit.getTag());
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                }else {
                    btn_visit.setVisibility(View.GONE);
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 获取解析结果
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String contents = result.getContents();
            if (contents == null) {
                Toast.makeText(this, "取消扫描", Toast.LENGTH_LONG).show();
                tv_result.setText("");
            } else {
                Toast.makeText(this, "扫描内容:" + contents, Toast.LENGTH_LONG).show();
                tv_result.setText(contents);
                btn_cope.setVisibility(View.VISIBLE);
            }

            if (FormatUtil.isWebUrl(contents)){
                btn_visit.setText("访问网址");
                btn_visit.setTag(contents);
                btn_visit.setVisibility(View.VISIBLE);
            }else {
                btn_visit.setVisibility(View.GONE);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
