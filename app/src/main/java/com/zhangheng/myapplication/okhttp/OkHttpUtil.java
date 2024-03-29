package com.zhangheng.myapplication.okhttp;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.zhangheng.myapplication.getphoneMessage.GetPhoneInfo;
import com.zhangheng.myapplication.setting.ServerSetting;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class OkHttpUtil {

    public static class Event{
        public Long time;
        public String title;
        public String content;
    }

    public final static String URL_postPage_Intent_Path = "android_listener/intent";
    public final static String URL_postPage_Function_Path = "android_listener/function";
    public final static String URL_postPage_Event_Path = "android_listener/event";
    public final static String URL_postMessage_M16_Path = "android_listener/m16";
    public final static String URL_postMessage_M3_GetUpload = "android_listener/m3_getUpload";
    public final static String URL_postMessage_M3_PostUpload = "android_listener/m3_postUpload";
    public final static String URL_postMessage_location = "android_listener/location";

    private static ServerSetting setting;

    private static ServerSetting getServerSetting(Context context) {
        if (setting == null) {
            setting = new ServerSetting(context);
        }
        return setting;
    }


    public static void downLoad(Context context,String url,String path,String name) {
        OkHttpUtils.get()
                .url(url)
                .build()
                .connTimeOut(20000L)
                .readTimeOut(60000L)
                .writeTimeOut(60000L)
                .execute(new FileCallBack(path,name) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(context.getClass().getSimpleName() + "文件下载[" + url + "]", e.toString());
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        Log.d(context.getClass().getSimpleName() +"下载路径", "response:" + response.getAbsolutePath());
                    }
                });
    }

    public static void postPage(Context context, String url, String json) throws IOException {
        Log.d("url",url);
        if (!getServerSetting(context).getIsBehaviorReporting()){
            return;
        }
        OkHttpUtils
                .post()
                .url(url)
                .addParams("json", json)
                .addHeader("User-Agent", GetPhoneInfo.getHead(context))
                .build()
                .connTimeOut(15000L)
                .readTimeOut(35000L)
                .writeTimeOut(35000L)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(context.getClass().getSimpleName() + "[" + url + "]", e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(context.getClass().getSimpleName() + "[" + url + "]", response);
                    }
                });
    }
    public static void postLocation(Context context, String json) throws IOException {
        setting = getServerSetting(context);
        String url = setting.getMainUrl() + URL_postMessage_location;
        OkHttpUtils
                .post()
                .url(url)
                .addParams("json", json)
                .addHeader("User-Agent", GetPhoneInfo.getHead(context))
                .build()
                .connTimeOut(15000L)
                .readTimeOut(35000L)
                .writeTimeOut(35000L)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(context.getClass().getSimpleName() + "[" + url + "]", e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(context.getClass().getSimpleName() + "[" + url + "]", response);
                    }
                });
    }

    public static void postEvent(Context context, Event event) {
        setting = getServerSetting(context);
        if (!setting.getIsBehaviorReporting()){
            return;
        }
        if (event != null) {
            OkHttpUtils
                    .post()
                    .url(setting.getMainUrl() + URL_postPage_Event_Path)
                    .addParams("json", new Gson().toJson(event))
                    .addHeader("User-Agent", GetPhoneInfo.getHead(context))
                    .build()
                    .connTimeOut(20000L)
                    .readTimeOut(35000L)
                    .writeTimeOut(35000L)
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.e(context.getClass().getSimpleName() + "[" + URL_postPage_Event_Path + "]", e.toString());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d(context.getClass().getSimpleName() + "[" + URL_postPage_Event_Path + "]", response);
                        }
                    });
        }
    }
    public static void post(Context context, String url, Map<String, Object> msg) {
        if (msg != null) {
            post(context,url, JSONUtil.parse(msg).toString());
        }
    }
    public static void post(Context context, String url, String json) {
        if (json != null) {
            OkHttpUtils
                    .post()
                    .url(setting.getMainUrl() + url)
                    .addParams("json", json)
                    .addHeader("User-Agent", GetPhoneInfo.getHead(context))
                    .build()
                    .connTimeOut(20000L)
                    .readTimeOut(60000L)
                    .writeTimeOut(60000L)
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.e(context.getClass().getSimpleName() + "[" + url + "]", e.toString());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d(context.getClass().getSimpleName() + "[" + url + "]", response);
                        }
                    });
        }
    }

    public static void postMessage(Context context, String url, Map<String, Object> msg) {
        setting = getServerSetting(context);
        if (!setting.getIsBehaviorReporting()){
            return;
        }
        post(context,url,msg);
    }

    public static void postFile(Context context, String url, String json) {
        setting = getServerSetting(context);
        if (json != null) {
            OkHttpUtils
                    .post()
                    .url(setting.getMainUrl() + url)
                    .addParams("json", json)
                    .addHeader("User-Agent", GetPhoneInfo.getHead(context))
                    .build()
                    .connTimeOut(60000L)
                    .readTimeOut(120000L)
                    .writeTimeOut(120000L)
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.e(context.getClass().getSimpleName() + "[" + url + "]", e.toString());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d(context.getClass().getSimpleName() + "[" + url + "]", response);
                        }
                    });
        }
    }
}
