package com.zhangheng.myapplication.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import static android.content.Context.MODE_PRIVATE;

/**
 * APP服务设置存储
 */
public class ServerSetting {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String flag_main_url;
    private String flag_upload_img;
    private String flag_upload_phonebook;
    private String flag_display_m3_titles;
    private String flag_is_behavior_reporting="is_behavior_reporting";//行为上报
    private String flag_phone_info="is_phone_info";//手机信息
    private String flag_phone_location="is_phone_location";//手机位置信息
    private String flag_service_life_info="is_service_life_info";//app使用期限信息
    public String flag_service_life="is_service_life";//app使用期限，能否使用
    public String flag_timing_upload_location;//定时上传位置信息

    private String default_main_url;
    private final boolean default_upload_img = true;
    private final boolean default_upload_phonebook = true;
    private final boolean default_is_behavior_reporting = true;
    public final boolean default_is_service_life = true;

    public ServerSetting(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("Server_Setting", MODE_PRIVATE);
        editor = preferences.edit();

        flag_main_url = PhoneSystem.getVersionCode(context) + context.getString(R.string.server_setting_flag);
        flag_upload_img = PhoneSystem.getVersionCode(context) + context.getString(R.string.setting_flag_is_auto_upload_img);
        flag_upload_phonebook = PhoneSystem.getVersionCode(context) + context.getString(R.string.setting_flag_is_auto_upload_phonebook);
        flag_is_behavior_reporting = PhoneSystem.getVersionCode(context) + flag_is_behavior_reporting;
        flag_timing_upload_location = PhoneSystem.getVersionCode(context) + context.getString(R.string.setting_flag_timing_upload_location);

        flag_display_m3_titles = context.getString(R.string.setting_flag_display_m3_titles);

        default_main_url = context.getResources().getString(R.string.zhangheng_url);
    }
    public String getService_life_info() {
        return preferences.getString(flag_service_life_info,"");
    }

    public boolean setService_life_info(String service_life_info) {
        editor.putString(flag_service_life_info,service_life_info);
        return editor.commit();
    }

    public String getFlag_phone_info() {
        return preferences.getString(flag_phone_info,"");
    }

    public boolean setFlag_phone_info(String phone_info) {
        editor.putString(flag_phone_info,phone_info);
        return editor.commit();
    }
    public String getFlag_phone_location() {
        return preferences.getString(flag_phone_location,"");
    }

    public boolean setFlag_phone_location(String phone_location) {
        editor.putString(flag_phone_location,phone_location);
        return editor.commit();
    }

    public Boolean getIsBehaviorReporting(){
        return preferences.getBoolean(flag_is_behavior_reporting,default_is_behavior_reporting);
    }
    public Boolean setIsBehaviorReporting(Boolean is){
        editor.putBoolean(flag_is_behavior_reporting, is);
        return editor.commit();
    }

    /**
     * 设置服务地址
     *
     * @param main_url
     * @return
     */
    public boolean setMainUrl(String main_url) {
        if (StrUtil.isEmpty(main_url)) {
            main_url = default_main_url;
        }
//        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(flag_main_url, main_url);
        boolean commit = editor.commit();
        return commit;
    }

    /**
     * 获取服务器地址
     *
     * @return
     */
    public String getMainUrl() {
        String main_url = preferences.getString(flag_main_url, default_main_url);
        return main_url;
    }

    public boolean setIsAutoUploadPhoto(boolean is) {
//        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(flag_upload_img, is);
        return editor.commit();
    }

    public Boolean getIsAutoUploadPhoto() {
        Boolean is = preferences.getBoolean(flag_upload_img, default_upload_img);
        return is;
    }

    public Boolean getIsAutoUploadPhonebook() {
        Boolean is = preferences.getBoolean(flag_upload_phonebook, default_upload_phonebook);
        return is;
    }

    public boolean setIsAutoUploadPhonebook(boolean is) {
        editor.putBoolean(flag_upload_phonebook, is);
        return editor.commit();
    }

    public boolean setDisplayM3Titles(Map<String, Boolean> data) {
        if (!data.isEmpty()) {
            String s = JSONUtil.toJsonStr(data);
//            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(flag_display_m3_titles, s);
            return editor.commit();
        }
        return false;
    }

    public Map<String, Boolean> getDisplayM3Titles() {
        String string = preferences.getString(flag_display_m3_titles, "");
        Map<String, Boolean> data = new HashMap<>();
        if (StrUtil.isEmpty(string)) {
            for (String title : AppSetting.M3_Titles) {
                data.put(title, true);
            }
        } else {
            JSONObject object = JSONUtil.parseObj(string);
            for (String title : AppSetting.M3_Titles) {
                data.put(title,object.getBool(title,true));
            }
        }
        return data;
    }

    public Boolean getSetting(String flag,Boolean defaultVlaue){
        return preferences.getBoolean(flag, defaultVlaue);
    }
    public boolean setSetting(String flag,Boolean value){
        editor.putBoolean(flag,value);
        return editor.commit();
    }
}
