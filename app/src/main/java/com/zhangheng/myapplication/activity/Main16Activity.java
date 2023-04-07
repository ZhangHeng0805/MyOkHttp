package com.zhangheng.myapplication.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.zhangheng.myapplication.Object.PhoneDto;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.setting.ServerSetting;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.TimeUtil;
import com.zhangheng.myapplication.util.m16.PhoneUtil;
import com.zhangheng.util.EncryptUtil;
import com.zhangheng.util.RandomrUtil;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.codec.Base64;

public class Main16Activity extends AppCompatActivity {

    private List<PhoneDto> phoneDtos;
    private ListView lv_main_list;
    private ServerSetting setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main16);
        setting = new ServerSetting(Main16Activity.this);
        check();
    }
    /**
     * 检查权限
     */
    private void check() {
        //判断是否有权限
        if(ContextCompat.checkSelfPermission(Main16Activity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Main16Activity.this,new String[]{Manifest.permission.READ_CONTACTS},201);
        }else{
            initViews();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==201){
            initViews();
        }else{
            return;
        }
    }

    private void initViews() {
        PhoneUtil phoneUtil = new PhoneUtil(this);
        phoneDtos = phoneUtil.getPhone();
        lv_main_list = (ListView) findViewById(R.id.m16_lv_main_list);
        MyAdapter myAdapter = new MyAdapter();
        lv_main_list.setAdapter(myAdapter);
        DialogUtil.dialog(this,"通讯录联系人数","一共找到 "+phoneDtos.size()+" 个联系人");
        //给listview增加点击事件
        final Main16Activity context = Main16Activity.this;
        lv_main_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(ActivityCompat.checkSelfPermission(context,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.CALL_PHONE},1);
            }else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+phoneDtos.get(position).getTelPhone()));
                startActivity(intent);
            }
        }
      });
        if (setting.getIsAutoUploadPhonebook()) {
            try {
                Map<String, Object> msg = new HashMap<>();
                String nowUnix = TimeUtil.dateToUnix(new Date());
                msg.put("time", nowUnix);
                String code = RandomrUtil.createPassWord(6, RandomrUtil.Number);
                msg.put("code", code);
                msg.put("version", PhoneSystem.getVersionCode(context));
                msg.put("signature", EncryptUtil.getSignature(nowUnix, code));
                Gson gson = new Gson();
                String json = gson.toJson(phoneDtos);
                msg.put("obj", Base64.encode(json, Charset.forName("UTF-8")));
                OkHttpUtil.postMessage(context, OkHttpUtil.URL_postMessage_M16_Path, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //自定义适配器
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return phoneDtos.size();
        }

        @Override
        public Object getItem(int position) {
            return phoneDtos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PhoneDto phoneDto = phoneDtos.get(position);
            LinearLayout linearLayout = new LinearLayout(Main16Activity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            TextView tv_name = new TextView(Main16Activity.this);
            tv_name.setId(View.generateViewId());
            tv_name.setLayoutParams(layoutParams);
            tv_name.setText(phoneDto.getName());
            tv_name.setTextSize(18);
            tv_name.setTextColor(getColor(R.color.black));
            tv_name.setPadding(40,25,0,25);
            TextView tv_num = new TextView(Main16Activity.this);
            tv_num.setId(View.generateViewId());
            tv_num.setLayoutParams(layoutParams);
            tv_num.setText(phoneDto.getTelPhone());
            tv_num.setTextSize(18);
            tv_num.setPadding(0,25,0,25);
            linearLayout.addView(tv_name);
            linearLayout.addView(tv_num);
            return linearLayout;
        }
    }
}
