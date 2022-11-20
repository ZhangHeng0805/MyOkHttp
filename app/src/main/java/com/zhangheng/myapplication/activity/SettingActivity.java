package com.zhangheng.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.setting.ServerSetting;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.util.TimeUtil;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.json.JSONUtil;

public class SettingActivity extends Activity {
    private final String Tag = getClass().getSimpleName();

    private ImageView setting_iv_back;
    private ListView setting_lv_meun;

    private ServerSetting setting;

    private final String[] setting_meun = {
            "服务器设置",
            "意见反馈",
            "捐赠支持",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setting = new ServerSetting(this);
        initControl();
        Listener();

    }

    /**
     * 初始化控件
     */
    private void initControl() {
        setting_iv_back = findViewById(R.id.setting_iv_back);
        setting_lv_meun = findViewById(R.id.setting_lv_meun);
    }

    /**
     * 监听事件
     */
    private void Listener() {
        //返回图标
        setting_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setAdapter();
        setting_lv_meun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(SettingActivity.this,setting_meun[i],Toast.LENGTH_SHORT).show();
                Map<String, Object> map = new HashMap<>();
                String funTil = setting_meun[i];
                String funName = Tag + "";
                switch (setting_meun[i]) {
                    case "服务器设置":
                        setServer();
                        funName += ".setServer()";
                        break;
                    case "意见反馈":
                        setFeedback();
                        funName += ".setFeedback()";
                        break;
                    case "捐赠支持":
                        setDonation();
                        funName += ".setDonation()";
                        break;
                    default:
                        Toast.makeText(SettingActivity.this, setting_meun[i], Toast.LENGTH_SHORT).show();
                        break;
                }
                map.put("funName", funTil);
                map.put("funPath", funName);
                map.put("time", new Date().getTime());
                String path = OkHttpUtil.URL_postPage_Function_Path;
                try {
                    OkHttpUtil.postPage(SettingActivity.this, setting.getMainUrl() + path, JSONUtil.toJsonStr(map));
                } catch (IOException e) {
                    Log.e(Tag + "[" + path + "]", e.toString());
                }
            }
        });

    }

    //捐赠
    private void setDonation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        AlertDialog dialog = builder.create();

        ImageView img = new ImageView(SettingActivity.this);
        img.setImageResource(R.drawable.collection_code);

        LinearLayout layout = new LinearLayout(SettingActivity.this);
        LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        img_params.setMargins(10, 30, 10, 30);
        layout.addView(img, img_params);

        dialog.setView(layout);
        dialog.setCancelable(true);
        dialog.setTitle("感谢捐赠");
        dialog.setMessage("您的捐赠是我前进的动力！");
        dialog.show();
    }

    //意见反馈
    private void setFeedback() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
        dialog.setTitle("意见反馈");

        dialog.setMessage("您可以通过邮箱向[zhangheng_0805@163.com]发送反馈邮件");
        dialog.setPositiveButton("打开邮箱发送", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //<a target="_blank" href="http://mail.qq.com/cgi-bin/qm_share?t=qm_mailme&email=wriqo6ylqqespezy_vL3grOz7KGtrw" style="text-decoration:none;"><img src="http://rescdn.qqmail.com/zh_CN/htmledition/images/function/qm_open/ico_mailme_12.png"/></a>
                Uri uri = Uri.parse("mailto:zhangheng_0805@163.com");
                String[] email = {"zhangheng.0805@qq.com"};
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                intent.putExtra(Intent.EXTRA_SUBJECT, "《" + getString(R.string.app_name) + "》android app BUG反馈"); // 主题
                intent.putExtra(Intent.EXTRA_TEXT, "bug描述（可配图）："); // 正文
                startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        if (dialog != null) {
            dialog.show();
        }
    }

    //服务器设置管理员验证
    private void setServer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        AlertDialog dialog = builder.create();
        int width = dialog.getWindow().getWindowManager().getDefaultDisplay().getWidth();
        EditText pwd = new EditText(SettingActivity.this);
        pwd.setBackground(getDrawable(R.drawable.btn));
        pwd.setHint("请输入管理员密码:");
        pwd.setWidth((int) (width * 0.7));
        pwd.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button btn1 = new Button(SettingActivity.this);
        btn1.setText("验证");
        btn1.setBackground(getDrawable(R.drawable.btn1));

        LinearLayout layout = new LinearLayout(SettingActivity.this);
        layout.setPadding(8, 5, 8, 5);
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams pwd_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pwd_params.setMargins(0, 10, 0, 30);
        LinearLayout.LayoutParams btn1_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btn1_params.setMargins(0, 30, 0, 30);
        layout.addView(pwd, pwd_params);
        layout.addView(btn1, btn1_params);
        dialog.setView(layout);
        dialog.setTitle("管理员验证");
        dialog.setMessage("该设置需要进行管理员验证");


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = PhoneSystem.getVersionCode(SettingActivity.this)+ TimeUtil.getTime(TimeUtil.Day);
                String s = pwd.getText().toString();
                if (code.equals(s)) {
                    setServerAddress();
                } else {
                    Toast.makeText(SettingActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (dialog != null) {
            dialog.show();
        }
    }

    //服务器地址设置
    private void setServerAddress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(SettingActivity.this, R.layout.item_server_setting, null);
        dialog.setView(dialogView);
        final EditText mainUrl = dialogView.findViewById(R.id.et_update_server_main_url);
        final String url = setting.getMainUrl();
        mainUrl.setText(url);
        Button submit = dialogView.findViewById(R.id.btn_update_server_submit);
        Button cancel = dialogView.findViewById(R.id.btn_update_server_cancel);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String main_url = mainUrl.getText().toString();

                if (main_url.length() > 0) {
                    if (!url.equals(main_url)) {
                        if (setting.setMainUrl(main_url)) {
                            DialogUtil.dialog(SettingActivity.this, "服务器配置设置成功", "请重启App使设置生效!");
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("funName", "服务器地址修改["+Tag+".setServerAddress()]成功");
                            map.put("funPath", main_url);
                            map.put("time", new Date().getTime());
                            String path = OkHttpUtil.URL_postPage_Function_Path;
                            try {
                                OkHttpUtil.postPage(SettingActivity.this, url + path, JSONUtil.toJsonStr(map));
                            } catch (IOException e) {
                                Log.e(Tag + "[" + path + "]", e.toString());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "服务器地址设置失败！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请先修改内容！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "输入框不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setAdapter() {
        MeunAdatper adatper = new MeunAdatper(setting_meun, SettingActivity.this);
        setting_lv_meun.setAdapter(adatper);
    }

    private class MeunAdatper extends BaseAdapter {

        private String[] data;
        private Context context;

        public MeunAdatper(String[] data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            String d = data[i];
            Hodler hodler;
            if (view == null) {
                hodler = new Hodler();
                view = View.inflate(context, R.layout.item_setting_meun, null);
                hodler.item_setting_tv_meun = view.findViewById(R.id.item_setting_tv_meun);
                view.setTag(hodler);
            } else {
                hodler = (Hodler) view.getTag();
            }
            hodler.item_setting_tv_meun.setText(d);
            return view;
        }

        class Hodler {
            TextView item_setting_tv_meun;
        }
    }
}
