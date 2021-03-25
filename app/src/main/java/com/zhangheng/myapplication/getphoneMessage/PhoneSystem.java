package com.zhangheng.myapplication.getphoneMessage;

import android.content.Context;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class PhoneSystem {
    /***获取系统版本***/
    public static String getSystemBuild() {
        String str = "", strCPU = "", cpuAddress = "0000000000000000";
        String strss = "";
        try {
            // 读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /system/build.prop");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // 查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {

                    // 查找到序列号所在行
                    if (str.indexOf("ro.build.display.id") > -1) {
                        // 提取序列号
                        strCPU = str.substring(str.indexOf("=") + 1,
                                str.length());
                        // 去空格
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    // 文件结尾
                    break;
                }
            }
            String []strs = cpuAddress.split(" ");
            strss = strs[3];
            return strss;
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return strss;
    }

    /*****获取版本号****/
    public static String getVersionCode(Context context){
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionCode;
            return "V" + versionCode + "-" + versionName;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }

}
