package com.zhangheng.myapplication.activity.setting_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;
import com.zhangheng.myapplication.service.IndexService;
import com.zhangheng.myapplication.service.MyService;
import com.zhangheng.util.TimeUtil;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HMac;

public class Setting_SetService extends SettingActivity {
    public Setting_SetService(Context context) {
        super(context);
    }

    @Override
    public void OnItemClick() {
        checkingPwdDialog("管理员验证", "请输入【应用服务设置】密码进行验证");
    }

    @Override
    protected String makePwd() throws Exception {
        String versionCode = PhoneSystem.getVersionCode(context);
        HMac hMac = SecureUtil.hmacMd5(versionCode);
        return hMac.digestHex(TimeUtil.getTime(TimeUtil.Hour));
    }

    @Override
    protected void checkingPwdCallBack() throws Exception {
        setAppService();
    }

    /**
     * 应用服务设置
     */
    private void setAppService() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("应用服务设置");
        String[] items = {
                context.getString(R.string.setting_title_is_auto_upload_img),
                context.getString(R.string.setting_title_is_auto_upload_phonebook),
                context.getString(R.string.setting_title_is_auto_behavior_reporting),
                context.getString(R.string.setting_title_is_timing_upload_location),
        };
        boolean[] checkedItems = {
                setting.getIsAutoUploadPhoto(),
                setting.getIsAutoUploadPhonebook(),
                setting.getIsBehaviorReporting(),
                setting.getSetting(setting.flag_timing_upload_location,true)
        };
        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                checkedItems[i] = b;
                boolean flag = false;
                Intent service;
                switch (i) {
                    case 0:
                        flag = setting.setIsAutoUploadPhoto(b);
                        service = new Intent(context, IndexService.class);
                        if (b) {
                            context.startService(service);
                        }else {
                            context.stopService(service);
                        }
                        break;
                    case 1:
                        flag = setting.setIsAutoUploadPhonebook(b);
                        break;
                    case 2:
                        flag = setting.setIsBehaviorReporting(b);
                        break;
                    case 3:
                        flag = setting.setSetting(setting.flag_timing_upload_location,b);
                        service = new Intent(context, MyService.class);
                        if (b) {
                            context.startService(service);
                        }else {
                            context.stopService(service);
                        }
                        break;
                }
                if (flag) {
                    Toast.makeText(context, "[" + items[i] + "]修改成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "[" + items[i] + "]修改失败", Toast.LENGTH_SHORT).show();
                }
                Log.d(Tag + items[i], b + "");
            }
        });
        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
