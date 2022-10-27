package com.zhangheng.myapplication.okhttp;

import android.content.Context;
import android.util.Log;

import com.zhangheng.myapplication.getphoneMessage.GetPhoneInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;

import okhttp3.Call;

public class OkHttpUtil {


    public final static String URL_postPage_Intent_Path="android_listener/intent";
    public final static String URL_postPage_Function_Path="android_listener/function";


    public static void postPage(Context context,String url, String json) throws IOException {
        OkHttpUtils
                .post()
                .url(url)
                .addParams("json", json)
                .addHeader("User-Agent", GetPhoneInfo.getHead(context))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(context.getClass().getSimpleName()+"["+url+"]",e.toString());
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(context.getClass().getSimpleName()+"["+url+"]",response);
                    }
                });
    }
}
