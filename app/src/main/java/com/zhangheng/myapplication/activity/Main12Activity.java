package com.zhangheng.myapplication.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;
import com.zhangheng.myapplication.util.EncryptUtil;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.digest.HMac;

public class Main12Activity extends Activity {

    private EditText m12_et_md5_content,m12_et_sha_content;
    private RadioGroup m12_RG_md5,m12_RG_sha;
    private Button m12_btn_md5_encode,m12_btn_sha_encode;
    private TextView m12_txt_md5_result,m12_txt_md5_explain,m12_txt_sha_result,m12_txt_sha_explain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main12);
        initView();
    }

    private void initView() {
        m12_et_md5_content=findViewById(R.id.m12_et_md5_content);
        m12_RG_md5=findViewById(R.id.m12_RG_md5);
        m12_btn_md5_encode=findViewById(R.id.m12_btn_md5_encode);
        m12_txt_md5_result=findViewById(R.id.m12_txt_md5_result);
        m12_et_sha_content=findViewById(R.id.m12_et_sha_content);
        m12_RG_sha=findViewById(R.id.m12_RG_sha);
        m12_btn_sha_encode=findViewById(R.id.m12_btn_sha_encode);
        m12_txt_sha_result=findViewById(R.id.m12_txt_sha_result);
        m12_txt_md5_explain=findViewById(R.id.m12_txt_md5_explain);
        m12_txt_sha_explain=findViewById(R.id.m12_txt_sha_explain);
        viewListener();
    }

    private void viewListener() {
        String versionCode = PhoneSystem.getVersionCode(Main12Activity.this);
        m12_btn_md5_encode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = m12_et_md5_content.getText().toString();
                if (!StrUtil.isEmpty(s)) {
                    setResultDefaultState();
                    String result = null;
                    String explain = null;
                    switch (m12_RG_md5.getCheckedRadioButtonId()) {
                        case R.id.m12_rb_md5:
                            result = EncryptUtil.getMd5(s);
                            explain = "常规md5加密";
                            break;
                        case R.id.m12_rb_mymd5:
                            result = EncryptUtil.getMyMd5(s);
                            explain = "自制改造md5加密";
                            break;
                        case R.id.m12_rb_hmacmd5:
                            HMac hMac = SecureUtil.hmacMd5(versionCode);
                            result = hMac.digestHex(s);
                            explain = "密钥md5加密,注意不同app版本的加密结果不同";
                            break;
                    }
                    if (!StrUtil.isEmpty(result)){
                        m12_txt_md5_result.setVisibility(View.VISIBLE);
                        m12_txt_md5_result.setText(result);
                        m12_txt_md5_explain.setVisibility(View.VISIBLE);
                        m12_txt_md5_explain.setText(explain);
                    }
                }
            }
        });
        m12_btn_sha_encode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = m12_et_sha_content.getText().toString();
                if (!StrUtil.isEmpty(s)) {
                    setResultDefaultState();
                    String result = null;
                    String explain = null;
                    switch (m12_RG_sha.getCheckedRadioButtonId()) {
                        case R.id.m12_rb_hmacsha:
                            result = SecureUtil.hmacSha1(versionCode).digestHex(s);
                            explain="密钥sha1加密,注意不同app版本的加密结果不同";
                            break;
                        case R.id.m12_rb_sha1:
                            result = SecureUtil.sha1(s);
                            explain="常规sha1加密";
                            break;
                        case R.id.m12_rb_sha256:
                            Digester sha256 = new Digester(DigestAlgorithm.SHA256);
                            result = sha256.digestHex(s);
                            explain="常规sha256加密";
                            break;
                        case R.id.m12_rb_sha384:
                            Digester sha384 = new Digester(DigestAlgorithm.SHA384);
                            result = sha384.digestHex(s);
                            explain="常规sha384加密";
                            break;
                        case R.id.m12_rb_sha512:
                            Digester sha512 = new Digester(DigestAlgorithm.SHA512);
                            result = sha512.digestHex(s);
                            explain="常规sha512加密";
                            break;
                    }
                    if (!StrUtil.isEmpty(result)){
                        m12_txt_sha_result.setVisibility(View.VISIBLE);
                        m12_txt_sha_result.setText(result);
                        m12_txt_sha_explain.setVisibility(View.VISIBLE);
                        m12_txt_sha_explain.setText(explain);
                    }
                }
            }
        });
    }

    private void setResultDefaultState(){
        if (m12_txt_md5_result.getVisibility() == View.VISIBLE){
            m12_txt_md5_result.setVisibility(View.GONE);
        }
        if (m12_txt_md5_explain.getVisibility() == View.VISIBLE){
            m12_txt_md5_explain.setVisibility(View.GONE);
        }
        if (m12_txt_sha_result.getVisibility() == View.VISIBLE){
            m12_txt_sha_result.setVisibility(View.GONE);
        }
        if (m12_txt_sha_explain.getVisibility() == View.VISIBLE){
            m12_txt_sha_explain.setVisibility(View.GONE);
        }
    }
}
