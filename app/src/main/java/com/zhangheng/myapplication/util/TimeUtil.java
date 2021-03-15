package com.zhangheng.myapplication.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public static String getSystemTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time=df.format(new Date());// new Date()为获取当前系统时间
//        System.out.println();
        return time;
    }
    public static String getTime(){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
        String time=df.format(new Date());// new Date()为获取当前系统时间
//        System.out.println();
        return time;
    }
}
