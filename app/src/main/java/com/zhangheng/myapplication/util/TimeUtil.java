package com.zhangheng.myapplication.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.hutool.core.date.DateUtil;

public class TimeUtil {
    public static String getSystemTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");//设置日期格式
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

    /**
     * 将毫秒格式化
     * @param t
     * @return
     */
    public static String format(int t){
        if(t<60000){
            return "00:"+getString((t % 60000 )/1000);
        }else if((t>=60000)&&(t<3600000)){
            return getString((t % 3600000)/60000)+":"+getString((t % 60000 )/1000);
        }else {
            return getString(t / 3600000)+":"+getString((t % 3600000)/60000)+":"+getString((t % 60000 )/1000);
        }
    }
    private static String getString(int t){
        String m="";
        if(t>0){
            if(t<10){
                m="0"+t;
            }else{
                m=t+"";
            }
        }else{
            m="00";
        }
        return m;
    }

    /**
     * 将Date日期转换为Unix时间戳
     * @param date 日期
     * @return 转化的Unix时间戳
     */
    public static String dateToUnix(Date date){
        return Long.toString(date.getTime() / 1000L);
    }
    /**
     * 将Unix时间戳转换为日期Date
     * @param unix Unix时间戳
     * @return 转换的日期
     */
    public static Date UnixToDate(String unix){
        Long unixLong = Long.valueOf(unix)*1000;
        Date UnixDate = DateUtil.date(unixLong);
        return UnixDate;
    }
}
