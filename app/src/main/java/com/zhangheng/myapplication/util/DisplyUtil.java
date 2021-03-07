package com.zhangheng.myapplication.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by 张恒 on 2020/10/26 0026.
 */

public class DisplyUtil {
    public static int getScreenWidth(Context ctx){
        WindowManager wn= (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm=new DisplayMetrics();
        wn.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
    public static int getScreenHeight(Context ctx){
        WindowManager wn= (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm=new DisplayMetrics();
        wn.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
    public static float getScreenDensity(Context ctx){
        WindowManager wn= (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm=new DisplayMetrics();
        wn.getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }
}
