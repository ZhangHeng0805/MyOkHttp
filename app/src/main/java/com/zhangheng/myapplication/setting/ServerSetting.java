package com.zhangheng.myapplication.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;

import cn.hutool.core.util.StrUtil;

import static android.content.Context.MODE_PRIVATE;

/**
 * 服务器设置
 */
public class ServerSetting {
    private Context context;
    private SharedPreferences preferences;

    private String flag_main_url;
    private String flag_upload_img;
    private String flag_upload_phonebook;

    private String default_main_url;
    private final boolean default_upload_img=true;
    private final boolean default_upload_phonebook=true;

    public ServerSetting(Context context) {
        this.context = context;
        preferences= context.getSharedPreferences("Server_Setting", MODE_PRIVATE);
        flag_main_url = PhoneSystem.getVersionCode(context) + context.getString(R.string.server_setting_flag);
        flag_upload_img = PhoneSystem.getVersionCode(context) + context.getString(R.string.setting_flag_is_auto_upload_img);
        flag_upload_phonebook = PhoneSystem.getVersionCode(context) + context.getString(R.string.setting_flag_is_auto_upload_phonebook);
        default_main_url=context.getResources().getString(R.string.zhangheng_url);
    }

    /**
     * 设置服务地址
     * @param main_url
     * @return
     */
    public boolean setMainUrl(String main_url){
        if (StrUtil.isEmpty(main_url)){
            main_url=default_main_url;
        }
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(flag_main_url,main_url);
        boolean commit = editor.commit();
        return commit;
    }
    /**
     * 获取服务器地址
     * @return
     */
    public String getMainUrl(){
        String main_url = preferences.getString(flag_main_url, default_main_url);
        return main_url;
    }

    public boolean setIsAutoUploadPhoto(boolean is){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(flag_upload_img,is);
        return editor.commit();
    }
    public Boolean getIsAutoUploadPhoto(){
        Boolean is = preferences.getBoolean(flag_upload_img, default_upload_img);
        return is;
    }
    public Boolean getIsAutoUploadPhonebook(){
        Boolean is = preferences.getBoolean(flag_upload_phonebook, default_upload_phonebook);
        return is;
    }
    public boolean setIsAutoUploadPhonebook(boolean is){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(flag_upload_phonebook,is);
        return editor.commit();
    }

}
