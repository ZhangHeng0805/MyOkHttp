package com.zhangheng.myapplication.activity.setting_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.zhangheng.myapplication.setting.AppSetting;
import com.zhangheng.myapplication.util.DialogUtil;

import java.util.HashMap;
import java.util.Map;

public class Setting_FunctionList extends SettingActivity {


    public Setting_FunctionList(Context context) {
        super(context);
    }

    @Override
    public void OnItemClick() {
        FunctionList();
    }

    @Override
    protected String makePwd() throws Exception {
        return null;
    }

    @Override
    protected void checkingPwdCallBack() throws Exception {

    }

    /**
     * 功能列表设置
     */
    private void FunctionList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("主页功能列表显示设置");
        String[] items = AppSetting.M3_Titles;
        Map<String, Boolean> map = setting.getDisplayM3Titles();
        boolean[] checkedItems = new boolean[items.length];
        for (int i = 0; i < items.length; i++) {
            checkedItems[i] = map.get(items[i]);
        }
        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                checkedItems[i] = b;
                Log.d(Tag + items[i], b + "");
            }
        });
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String, Boolean> map = new HashMap<>();
                for (int n = 0; n < items.length; n++) {
                    map.put(items[n], checkedItems[n]);
                }
                boolean b = setting.setDisplayM3Titles(map);
                if (b) {
                    DialogUtil.dialog(context, "保存成功", "重启APP即可生效");
                } else {
                    DialogUtil.dialog(context, "保存失败", "设置保存失败！");
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
