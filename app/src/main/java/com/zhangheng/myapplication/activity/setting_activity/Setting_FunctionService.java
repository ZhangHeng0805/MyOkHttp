package com.zhangheng.myapplication.activity.setting_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.zhangheng.myapplication.setting.AppSetting;

import java.util.ArrayList;
import java.util.Map;

import cn.hutool.core.convert.Convert;

public class Setting_FunctionService extends SettingActivity {

    public Setting_FunctionService(Context context) {
        super(context);
    }

    @Override
    public void OnItemClick() {
        FunctionService();
    }

    @Override
    protected String makePwd() throws Exception {
        return null;
    }

    @Override
    protected void checkingPwdCallBack() throws Exception {

    }

    /**
     * 服务功能设置
     */
    private void FunctionService() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("APP服务功能设置");
        ArrayList<Map<String, Object>> serviceSetting = AppSetting.serviceSetting;
        String[] items = new String[serviceSetting.size()];
        boolean[] checkedItems = new boolean[serviceSetting.size()];
        for (int i = 0; i < serviceSetting.size(); i++) {
            Map<String, Object> map = serviceSetting.get(i);
            items[i] = Convert.toStr(map.get("name"));
            String flag = Convert.toStr(map.get("flag"));
            Boolean defaultValue = Convert.toBool(map.get("default"));
            Boolean setting = this.setting.getSetting(flag, defaultValue);
            checkedItems[i] = setting;

        }

        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                checkedItems[i] = b;
                boolean flag = false;
                Map<String, Object> map = serviceSetting.get(i);
                String f = Convert.toStr(map.get("flag"));
                flag = setting.setSetting(f, b);
                String name = Convert.toStr(map.get("name"));
                if (flag) {
                    Toast.makeText(context, "[" + name + "]修改成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "[" + name + "]修改失败", Toast.LENGTH_SHORT).show();
                }
                Log.d(Tag + name, b + "");
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
