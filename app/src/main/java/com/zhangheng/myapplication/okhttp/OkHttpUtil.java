package com.zhangheng.myapplication.okhttp;

import android.content.Context;
import android.util.Log;

import com.zhangheng.myapplication.getphoneMessage.GetPhoneInfo;
import com.zhangheng.myapplication.setting.ServerSetting;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.Map;

import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class OkHttpUtil {


    public final static String URL_postPage_Intent_Path = "android_listener/intent";
    public final static String URL_postPage_Function_Path = "android_listener/function";
    public final static String URL_postMessage_M16_Path = "android_listener/m16";
    public final static String URL_postMessage_M3_GetUpload = "android_listener/m3_getUpload";
    public final static String URL_postMessage_M3_PostUpload = "android_listener/m3_postUpload";

    private static ServerSetting setting;

    private static ServerSetting getServerSetting(Context context) {
        if (setting == null) {
            setting = new ServerSetting(context);
        }
        return setting;
    }


    public static void postPage(Context context, String url, String json) throws IOException {
        OkHttpUtils
                .post()
                .url(url)
                .addParams("json", json)
                .addHeader("User-Agent", GetPhoneInfo.getHead(context))
                .build()
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

    public static void postMessage(Context context, String url, Map<String, Object> msg) {
        setting = getServerSetting(context);
        if (msg != null) {
            OkHttpUtils
                    .post()
                    .url(setting.getMainUrl() + url)
                    .addParams("json", JSONUtil.toJsonStr(msg))
                    .addHeader("User-Agent", GetPhoneInfo.getHead(context))
                    .build()
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
    public static void postFile(Context context, String url, String json) {
        setting = getServerSetting(context);
        if (json != null) {
            OkHttpUtils
                    .post()
                    .url(setting.getMainUrl() + url)
                    .addParams("json", json)
                    .addHeader("User-Agent", GetPhoneInfo.getHead(context))
                    .build()
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
