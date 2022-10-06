package com.zhangheng.myapplication.reptile;

import android.content.Context;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

/**
 * 音乐API
 *
 * @author 张恒
 * @program: reptile
 * @email zhangheng.0805@qq.com
 * @date 2022-09-30 08:26
 */
public class MusicAPI {

    public static void main(String[] args) {
//        List<Map<String, Object>> musicByLiuzhijin = MusicAPI.getMusicByLiuzhijin(null, "你好", "qq");
//        System.out.println(musicByLiuzhijin);
        System.out.println(getMusics("张杰","qq",1));
    }
//    static {
//        System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3");
//    }

    /**
     * 音乐API
     * 爬取：音乐直链搜索
     *
     * @param name 音乐名
     * @param Type 类型[qq：QQ音乐；netease：网易云]
     * @param page 页码
     * @return [{ title:歌名, author:歌手, type:平台 ， pic:封面 ， url:文件地址 ， link:来源地址, lrc:歌词 }]
     */
    public static List<Map<String, Object>> getMusicByLiuzhijin(Context context, String name, String Type, Integer page) {
        List<Map<String, Object>> list = new ArrayList<>();
        Type = Type.replace("网易云", "netease");
        String Url = "https://music.liuzhijin.cn/";
        Map<String, Object> body = new HashMap<>();
        body.put("input", name);
        if (page == null || page < 1) {
            page = 1;
        }
        body.put("page", page);
        body.put("filter", "name");
        //qq：QQ音乐；netease：网易云
        body.put("type", Type);

        String json = HttpRequest.post(Url)
                .form(body)
                .header("X-Requested-With", "XMLHttpRequest")
                .contentType("application/x-www-form-urlencoded;charset=UTF-8")
                .execute().body();
        //Unicode转字符串
//        String json=res[0];
        json = UnicodeUtil.toString(json);
//        System.out.println(JSONUtil.formatJsonStr(json));
        JSONObject jsonObject = JSONUtil.parseObj(json);
        Integer code = jsonObject.getInt("code");
        if (code.equals(200)) {
            JSONArray data = jsonObject.getJSONArray("data");
            for (Object d : data) {
                String s = JSONUtil.toJsonStr(d);
                JSONObject obj = JSONUtil.parseObj(s);
                Map<String, Object> music = new HashMap<>();
                //歌名
                String title = obj.getStr("title");
                music.put("title", StrUtil.isEmptyIfStr(title)?"":title);
                //歌手
                String author = obj.getStr("author");
                music.put("author", StrUtil.isEmptyIfStr(author)?"":author);
                //平台
                String type = obj.getStr("type");
                music.put("type", type!=null?type:"");
                //封面
                String pic = obj.getStr("pic");
                music.put("pic", pic!=null?pic.replace("\\", ""):"");
                //文件地址
                String url = obj.getStr("url");
                music.put("url", url!=null?url.replace("\\", ""):"");
                //来源地址
                String link = obj.getStr("link");
                music.put("link", link!=null?link.replace("\\", ""):"");
                //歌词
                String lrc = obj.getStr("lrc");
                music.put("lrc", lrc!=null?lrc.replace("\\", ""):"");
                list.add(music);
            }

        } else {
            Log.e("音乐爬虫错误：","第三方音乐[" + Url + "]API错误：" + jsonObject.getStr("error"));
        }
        return list;
    }

    public static List<Map<String, Object>> getMusics(String name,String type,Integer page) throws RuntimeException{
        List<Map<String, Object>> list = new ArrayList<>();
        String Url = "https://music.liuzhijin.cn/";
        Map<String, String> body = new HashMap<>();
        body.put("input", name);
        if (page == null || page < 1) {
            page = 1;
        }
        body.put("page", page.toString());
        body.put("filter", "name");
        //qq：QQ音乐；netease：网易云
        body.put("type", type);

        OkHttpUtils.post()
                .url(Url)
                .params(body)
                .addHeader("X-Requested-With","XMLHttpRequest")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("音乐爬虫错误",e.toString());
                throw new RuntimeException("第三方音乐[" + Url + "]API请求错误：" + e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                String json=UnicodeUtil.toString(response);
                System.out.println(JSONUtil.formatJsonStr(json));
                JSONObject jsonObject = JSONUtil.parseObj(json);
                Integer code = jsonObject.getInt("code");
                if (code.equals(200)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (Object d : data) {
                        String s = JSONUtil.toJsonStr(d);
                        JSONObject obj = JSONUtil.parseObj(s);
                        Map<String, Object> music = new HashMap<>();
                        //歌名
                        String title = obj.getStr("title");
                        music.put("title", StrUtil.isEmptyIfStr(title)?"":title);
                        //歌手
                        String author = obj.getStr("author");
                        music.put("author", StrUtil.isEmptyIfStr(author)?"":author);
                        //平台
                        String type = obj.getStr("type");
                        music.put("type", type!=null?type:"");
                        //封面
                        String pic = obj.getStr("pic");
                        music.put("pic", pic!=null?pic.replace("\\", ""):"");
                        //文件地址
                        String url = obj.getStr("url");
                        music.put("url", url!=null?url.replace("\\", ""):"");
                        //来源地址
                        String link = obj.getStr("link");
                        music.put("link", link!=null?link.replace("\\", ""):"");
                        //歌词
                        String lrc = obj.getStr("lrc");
                        music.put("lrc", lrc!=null?lrc.replace("\\", ""):"");
                        list.add(music);
                    }
                }else {
                    Log.e("音乐爬虫错误","第三方音乐[" + Url + "]API错误：" + jsonObject.getStr("error"));
                    throw new RuntimeException("第三方音乐[" + Url + "]API错误：" + jsonObject.getStr("error"));
                }
            }
        });

        return list;
    }
    /**
     * 音乐API
     * 爬取：音乐直链搜索
     *
     * @param name 音乐名
     * @param type 类型[qq：QQ音乐；netease：网易云]
     * @return [{ title:歌名, author:歌手, type:平台 ， pic:封面 ， url:文件地址 ， link:来源地址, lrc:歌词 }]
     */
    public static List<Map<String, Object>> getMusicByLiuzhijin(Context context, String name, String type) {
        return getMusicByLiuzhijin(context, name, type, null);
    }
}
