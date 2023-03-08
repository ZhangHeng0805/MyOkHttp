package com.zhangheng.myapplication.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.zhangheng.myapplication.R;

public class SystemUtil {
    /**
     * 复制内容到剪切板
     *
     * @param copyStr
     * @return
     */
    public static boolean copyStr(Context context,String copyStr) {
        try {
            //获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText(context.getResources().getString(R.string.app_name)+"_Label", copyStr);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 关闭输入法
     * @param activity
     */
    public static void closeInput(Activity activity) {

        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isOpen = imm.isActive();
            if (isOpen) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                IBinder windowToken = activity.getCurrentFocus().getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken
                        , InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }catch (Exception e){
            Log.e(activity.getClass().getSimpleName()+"关闭输入错误",e.toString());
        }
    }
}
