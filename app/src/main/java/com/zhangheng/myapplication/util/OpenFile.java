package com.zhangheng.myapplication.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class OpenFile {
    /**
     * android 打开本地文件
     *
     * @param path    本地文件路径
     * @param context 上下文
     */
    public static void openFile(String path, Context context) {
        try {
            File file = new File(path);
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//24 android7
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            context.grantUriPermission(context.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent intent2 = new Intent("android.intent.action.VIEW");
            intent2.addCategory("android.intent.category.DEFAULT");
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Log.d("sss", "opneFile: uri " + uri.toString());
            String type = AndroidFileUtil.getMIMEtype(path);
            intent2.setDataAndType(uri, type);
            context.startActivity(intent2);
        }
             catch (Exception e) {
            Log.d("sss", "loadAccessorySuccess: error " + e.toString());
        }
    }
}
