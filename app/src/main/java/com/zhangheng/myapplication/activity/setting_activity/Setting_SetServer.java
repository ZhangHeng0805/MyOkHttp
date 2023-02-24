package com.zhangheng.myapplication.activity.setting_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.EncryptUtil;
import com.zhangheng.util.FormatUtil;
import com.zhangheng.util.TimeUtil;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.json.JSONUtil;

public class Setting_SetServer extends SettingActivity {
    public Setting_SetServer(Context context) {
        super(context);
    }

    @Override
    public void OnItemClick() {
        checkingPwdDialog("管理员验证", "请输入【服务地址设置】密码进行验证");
    }

    @Override
    protected String makePwd() throws Exception {
        String versionCode = PhoneSystem.getVersionCode(context);
        return EncryptUtil.getMyMd5(versionCode + TimeUtil.getTime(TimeUtil.Hour));
    }

    @Override
    protected void checkingPwdCallBack() throws Exception {
        setServerAddress();
    }

    /**
     * 服务器地址设置
     */
    private void setServerAddress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(context, R.layout.item_server_setting, null);
        dialog.setView(dialogView);
        //初始化view
        final EditText mainUrl = dialogView.findViewById(R.id.et_update_server_main_url);
        Button submit = dialogView.findViewById(R.id.btn_update_server_submit);
        Button cancel = dialogView.findViewById(R.id.btn_update_server_cancel);
        //初始化数据
        final String url = setting.getMainUrl();
        mainUrl.setText(url);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String main_url = mainUrl.getText().toString();

                if (main_url.length() > 0) {
                    if (!url.equals(main_url)) {
                        if (FormatUtil.isWebUrl(main_url)) {
                            if (setting.setMainUrl(main_url)) {
                                DialogUtil.dialog(context, "服务器配置设置成功", "请重启App使设置生效!");
                                dialog.dismiss();
                                Map<String, Object> map = new HashMap<>();
                                map.put("funName", "服务器地址修改[" + Tag + ".setServerAddress()]成功");
                                map.put("funPath", main_url);
                                map.put("time", new Date().getTime());
                                String path = OkHttpUtil.URL_postPage_Function_Path;
                                try {
                                    OkHttpUtil.postPage(context, url + path, JSONUtil.toJsonStr(map));
                                } catch (IOException e) {
                                    Log.e(Tag + "[" + path + "]", e.toString());
                                }
                            } else {
                                Toast.makeText(context, "服务器地址设置失败！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "请正确的服务器地址！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "请先修改内容！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "输入框不能为空！", Toast.LENGTH_SHORT).show();
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
}
