package com.zhangheng.myapplication.setting;

import com.zhangheng.myapplication.activity.Main10Activity;
import com.zhangheng.myapplication.activity.Main11Activity;
import com.zhangheng.myapplication.activity.Main12Activity;
import com.zhangheng.myapplication.activity.Main13Activity;
import com.zhangheng.myapplication.activity.Main14Activity;
import com.zhangheng.myapplication.activity.Main15Activity;
import com.zhangheng.myapplication.activity.Main16Activity;
import com.zhangheng.myapplication.activity.Main17Activity;
import com.zhangheng.myapplication.activity.Main18Activity;
import com.zhangheng.myapplication.activity.Main19_1Activity;
import com.zhangheng.myapplication.activity.Main20Activity;
import com.zhangheng.myapplication.activity.Main21Activity;
import com.zhangheng.myapplication.activity.Main22Activity;
import com.zhangheng.myapplication.activity.Main23Activity;
import com.zhangheng.myapplication.activity.Main24Activity;
import com.zhangheng.myapplication.activity.Main25Activity;
import com.zhangheng.myapplication.activity.Main26Activity;
import com.zhangheng.myapplication.activity.Main27Activity;
import com.zhangheng.myapplication.activity.Main2Activity;
import com.zhangheng.myapplication.activity.Main4Activity;
import com.zhangheng.myapplication.activity.Main5Activity;
import com.zhangheng.myapplication.activity.Main6Activity;
import com.zhangheng.myapplication.activity.Main7Activity;
import com.zhangheng.myapplication.activity.Main8Activity;
import com.zhangheng.myapplication.activity.Main9Activity;
import com.zhangheng.myapplication.activity.MainActivity;
import com.zhangheng.myapplication.activity.Test1Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * APP常量设置
 */
public class AppSetting {
    /**
     * 主页功能列表
     */
    public static final String[] M3_Titles = new String[]{
//            "1.原生OkHttp的Get和Post请求文本数据",//MainActivity
//            "2.使用OkHttpUtil的Post提交文本数据",//Main2Activity
            "3.文件下载工具",//Main4Activity
//            "4.上传文件和检索本地文件",//Main5Activity
            "5.图片显示工具",//Main6Activity
            "6.天气查询工具",//Main7Activity（API）
            "7.二维码生成工具",//Main8Activity
            "8.新华字典查询工具",//Main9Activity（API）
            "9.图书电商查询工具",//Main10Activity（API）
//            "10.查询文件列表并下载（自制服务器）",//Main11Activity
            "11.密码工具",//Main12Activity
            "12.画板工具",//Main13Activity
            "13.精选文案",//Main14Activity（爬虫,API）
            "14.每天60秒读懂世界",//Main15Activity（API）
//            "15.自制下拉刷新的ListView（测试）",//Test1Activity
            "16.电话本工具",//Main16Activity
            "17.手机扫码工具",//Main17Activity
            "18.音乐工具(不同平台的免费音乐)",//Main18Activity(爬虫)
            "19.影视资源(全网影视资源搜索播放)",//Main19Activity(爬虫)
            "20.翻译工具",//Main20Activity(爬虫)
            "21.全国疫情实时大数据",//Main21Activity(爬虫)
            "22.历史上的今天",//Main22Activity(API)
            "23.热搜热榜单",//Main23Activity(API)
            "24.小爱AI聊天",//Main24Activity(API)
            "25.视频解析下载(抖音/快手/微视...)",//Main25Activity(API)
            "26.文字转语音",//Main26Activity(API)
            "27.AI机器人ChatGPT",//Main27Activity(API)
    };
    public final static Map<Integer, Class<?>> M3_contextMap = new HashMap<Integer, Class<?>>() {{
        put(1, MainActivity.class);
        put(2, Main2Activity.class);
        put(3, Main4Activity.class);
        put(4, Main5Activity.class);
        put(5, Main6Activity.class);
        put(6, Main7Activity.class);
        put(7, Main8Activity.class);
        put(8, Main9Activity.class);
        put(9, Main10Activity.class);
        put(10, Main11Activity.class);
        put(11, Main12Activity.class);
        put(12, Main13Activity.class);
        put(13, Main14Activity.class);
        put(14, Main15Activity.class);
        put(15, Test1Activity.class);
        put(16, Main16Activity.class);
        put(17, Main17Activity.class);
        put(18, Main18Activity.class);
        put(19, Main19_1Activity.class);
        put(20, Main20Activity.class);
        put(21, Main21Activity.class);
        put(22, Main22Activity.class);
        put(23, Main23Activity.class);
        put(24, Main24Activity.class);
        put(25, Main25Activity.class);
        put(26, Main26Activity.class);
        put(27, Main27Activity.class);
    }};

    public final static ArrayList<Map<String, Object>> serviceSetting = new ArrayList<Map<String, Object>>() {{
        add(new HashMap<String, Object>() {{
            put("name", "进入APP报时问候服务功能");//名称
            put("flag", "is_m3_voice_time");//标志
            put("default", true);//默认值
        }});
    }};
    /**
     * 讯飞语音种类
     */
    public final static Map<String,String> XunFei_Voice_Map=new HashMap<String, String>(){{
        //id,名称
        put("1","讯飞-七哥（男声）");
        put("2","讯飞-子晴（女声）");
        put("3","讯飞-一菲（女声）");
        put("4","讯飞-小露（女声）");
        put("5","讯飞-小鹏（男声）");
        put("6","讯飞-萌小新（男声）");
        put("7","讯飞-小雪（女声）");
        put("8","讯飞-超哥（男声）");
        put("9","讯飞-小媛（女声）");
        put("10","讯飞-叶子（女声）");
        put("11","讯飞-千雪（女声）");
        put("12","讯飞-小忠（男声）");
        put("13","讯飞-万叔（男声）");
        put("14","讯飞-虫虫（女声）");
        put("15","讯飞-楠楠（儿童-男）");
        put("16","讯飞-晓璇（女声）");
        put("17","讯飞-芳芳（儿童-女）");
        put("18","讯飞-嘉嘉（女声）");
        put("19","讯飞-小倩（女声）");
        put("20","讯飞-Catherine（女声-英文专用）");
    }};
    /**
     * 讯飞语音种类
     */
    public final static Map<String,String> BaiDu_Voice_Map=new HashMap<String, String>(){{
        //id,名称
        put("1","度逍遥-磁性男声");
        put("2","度博文-情感男声");
        put("3","度小贤-情感男声");
        put("4","度小鹿-甜美女声");
        put("5","度灵儿-清澈女声");
        put("6","度小乔-情感女声");
        put("7","度小雯-成熟女声");
        put("8","度米朵-可爱女童");

    }};
}
