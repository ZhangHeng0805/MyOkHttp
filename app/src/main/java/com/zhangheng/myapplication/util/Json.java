package com.zhangheng.myapplication.util;

import com.zhangheng.myapplication.Object.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Json {


    //解析单个对象
    public static User jsonUser(String json) throws JSONException {
        User user=new User();
        JSONObject json1 = new JSONObject(json);
        user.setName((String) json1.opt("name"));
        user.setId((Integer) json1.opt("id"));
        user.setEmail((String) json1.opt("email"));
        return user;
    }
    //解析数组集合对象
    public static List<User> jsonArray(String json) throws JSONException {
        List<User> list=new ArrayList<>();
        JSONArray json2=new JSONArray(json);
        for (int i=0;i<json2.length();i++){
            JSONObject jsonObject = json2.getJSONObject(i);
            if (jsonObject!=null){
                User user=new User();
                user.setName(jsonObject.optString("name"));
                user.setId(jsonObject.optInt("id"));
                user.setEmail(jsonObject.optString("email"));
                list.add(user);
            }
        }
        return list;
    }
}
