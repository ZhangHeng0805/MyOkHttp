package com.zhangheng.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.activity.setting_activity.Setting_AppVerificationCode;
import com.zhangheng.myapplication.activity.setting_activity.Setting_Donation;
import com.zhangheng.myapplication.activity.setting_activity.Setting_FeedBack;
import com.zhangheng.myapplication.activity.setting_activity.Setting_FunctionList;
import com.zhangheng.myapplication.activity.setting_activity.Setting_FunctionService;
import com.zhangheng.myapplication.activity.setting_activity.Setting_QQGroup;
import com.zhangheng.myapplication.activity.setting_activity.Setting_SetServer;
import com.zhangheng.myapplication.activity.setting_activity.Setting_SetService;
import com.zhangheng.myapplication.activity.setting_activity.Setting_WXOfficialAccount;
import com.zhangheng.myapplication.getphoneMessage.GetPhoneInfo;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.setting.ServerSetting;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.hutool.json.JSONUtil;

public class SettingActivity extends Activity {
    private final String Tag = getClass().getSimpleName();

    private ImageView setting_iv_back;
    private ListView setting_lv_meun;
    private TextView setting_tv_phoneId;

    private ServerSetting setting;

    private final Context context = SettingActivity.this;

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
        setting_tv_phoneId = findViewById(R.id.setting_tv_phoneId);
        setting_tv_phoneId.setText("ID码：" + GetPhoneInfo.getID(context));
    }

    private final String[] setting_meun = {
            "功能列表设置",//0
            "服务功能设置",//1
            "服务地址设置(密码)",//2
            "应用服务设置(密码)",//3
            "意见反馈",//4
            "捐赠支持",//5
            "微信公众号",//6
            "APP交流QQ群",//7
            "口令秘钥",//8
    };
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
//                Toast.makeText(context,setting_meun[i],Toast.LENGTH_SHORT).show();
                Map<String, Object> map = new HashMap<>();
                String funTil = setting_meun[i];
                String funName = Tag + "";
                com.zhangheng.myapplication.activity.setting_activity.SettingActivity activity=null;
                switch (i) {
                    case 0:
//                        FunctionList();
                        activity = new Setting_FunctionList(context);
                        funName += ".FunctionList()";
                        break;
                    case 1:
//                        FunctionService();
                        activity = new Setting_FunctionService(context);
                        funName += ".FunctionService()";
                        break;
                    case 2:
                        activity = new Setting_SetServer(context);
                        funName += ".setServer()";
                        break;
                    case 3:
                        activity = new Setting_SetService(context);
                        funName += ".setService()";
                        break;
                    case 4:
                        activity = new Setting_FeedBack(context);
                        funName += ".setFeedback()";
                        break;
                    case 5:
                        activity = new Setting_Donation(context);
                        funName += ".setDonation()";
                        break;
                    case 6:
                        activity = new Setting_WXOfficialAccount(context);
                        funName += ".WXOfficialAccount()";
                        break;
                    case 7:
                        activity = new Setting_QQGroup(context);
                        funName += ".QQGroup()";
                        break;
                    case 8:
                        activity = new Setting_AppVerificationCode(context);
                        funName += ".appVerificationCode()";
                        break;
                    default:
                        Toast.makeText(context, setting_meun[i], Toast.LENGTH_SHORT).show();
                        break;
                }
                onClick(activity);
                map.put("funName", funTil);
                map.put("funPath", funName);
                map.put("time", new Date().getTime());
                String path = OkHttpUtil.URL_postPage_Function_Path;
                try {
                    OkHttpUtil.postPage(context, setting.getMainUrl() + path, JSONUtil.toJsonStr(map));
                } catch (IOException e) {
                    Log.e(Tag + "[" + path + "]", e.toString());
                }
            }
        });
    }

    private void onClick(com.zhangheng.myapplication.activity.setting_activity.SettingActivity activity) {
        if (activity != null)
            activity.OnItemClick();
    }

    /**
     * 获取APP的唯一SHA码
     * @param context
     * @return
     */
    public static String getAppSHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置更新提示
     *
     * @param f
     * @param tips
     */
    private void toastUpdate(boolean f, String tips) {
        if (f) {
            Toast.makeText(context, "[" + tips + "]设置修改成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "[" + tips + "]设置修改失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAdapter() {
        MeunAdatper adatper = new MeunAdatper(setting_meun, context);
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
