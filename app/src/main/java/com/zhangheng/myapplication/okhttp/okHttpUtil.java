package com.zhangheng.myapplication.okhttp;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class okHttpUtil {

    public static String post(String url,String a,String b) throws IOException {
        Response response = OkHttpUtils
                .post()
                .url(url)
                .addParams("lastName", a)
                .addParams("email", b)
                .build()
                .execute();
        String suc="Response{protocol=http/1.1, code=200, message=, url=http://zhangheng.free.idcfengye.com/user}";
        return response.message();
    }
}
