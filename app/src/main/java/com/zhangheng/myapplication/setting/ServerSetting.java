package com.zhangheng.myapplication.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;

import static android.content.Context.MODE_PRIVATE;

/**
 * 服务器设置
 */
public class ServerSetting {
    private Context context;
    private SharedPreferences preferences;


    public ServerSetting(Context context) {
        this.context = context;
        preferences= context.getSharedPreferences("Server_Setting", MODE_PRIVATE);
    }

    /**
     * 设置服务地址
     * @param main_url
     * @return
     */
    public boolean setMainUrl(String main_url){
        if (main_url==null){
            main_url=context.getResources().getString(R.string.zhangheng_url);
        }
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(PhoneSystem.getVersionCode(context)+context.getString(R.string.server_setting_flag),main_url);
        boolean commit = editor.commit();
        return commit;
    }


    /**
     * 获取服务器地址
     * @return
     */
    public String getMainUrl(){
        String main_url = preferences.getString(PhoneSystem.getVersionCode(context)+context.getString(R.string.server_setting_flag), null);
        if (main_url==null){
            main_url=context.getResources().getString(R.string.zhangheng_url);
        }
        return main_url;
    }


}
