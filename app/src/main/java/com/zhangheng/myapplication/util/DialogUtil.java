package com.zhangheng.myapplication.util;

import android.app.AlertDialog;
import android.content.Context;

public class DialogUtil {
    public static void dialog(Context context,String title, String message){
        AlertDialog.Builder d=new AlertDialog.Builder(context);
        d.setTitle(title);
        d.setMessage(message);
        d.create().show();
    }
}
