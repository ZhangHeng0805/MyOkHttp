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
import com.zhangheng.myapplication.activity.Main2Activity;
import com.zhangheng.myapplication.activity.Main4Activity;
import com.zhangheng.myapplication.activity.Main5Activity;
import com.zhangheng.myapplication.activity.Main6Activity;
import com.zhangheng.myapplication.activity.Main7Activity;
import com.zhangheng.myapplication.activity.Main8Activity;
import com.zhangheng.myapplication.activity.Main9Activity;
import com.zhangheng.myapplication.activity.MainActivity;
import com.zhangheng.myapplication.activity.Test1Activity;

import java.util.HashMap;
import java.util.Map;

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
            "13.精选文案",//Main14Activity（爬虫）
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
    }};
}
