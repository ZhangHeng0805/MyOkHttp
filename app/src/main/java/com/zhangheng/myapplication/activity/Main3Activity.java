package com.zhangheng.myapplication.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.zhangheng.bean.Message;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.bean.app.AppLife;
import com.zhangheng.myapplication.getphoneMessage.GetPhoneInfo;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.permissions.ReadAndWrite;
import com.zhangheng.myapplication.service.LocationService;
import com.zhangheng.myapplication.setting.AppSetting;
import com.zhangheng.myapplication.setting.ServerSetting;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.util.ArrayUtil;
import com.zhangheng.util.FormatUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class Main3Activity extends Activity {

    private final String Tag = this.getClass().getSimpleName();
    private final Context context = Main3Activity.this;

    private String[] permissions = {
            Manifest.permission.INTERNET,//网络
            Manifest.permission.ACCESS_WIFI_STATE,//写入状态
            Manifest.permission.ACCESS_COARSE_LOCATION,//粗略定位
            Manifest.permission.ACCESS_FINE_LOCATION,//精确定位
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,//后台定位
            Manifest.permission.ACCESS_NETWORK_STATE,//网络状态
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//写外部存储器
            Manifest.permission.READ_EXTERNAL_STORAGE,//读外部存储器
            Manifest.permission.READ_PHONE_STATE,//读取手机状态
            Manifest.permission.RECEIVE_BOOT_COMPLETED,//接收启动完成的广播权限
    };
    private String versionCode;
    private ServerSetting setting;

    private ListView listView;
    private TextView m3_tv_service, m3_tv_ipAddress;
    private ImageView m3_iv_setting, m3_iv_service_refersh,m3_iv_logo;

    private AlertDialog.Builder builder;
    private SharedPreferences sharedPreferences;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    public JSONObject LoactionJson = null;
    private DialogUtil dialogUtil = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        checkPermission();
        setting = new ServerSetting(context);
        if (setting.getSetting(setting.flag_timing_upload_location,true)) {
            if (ReadAndWrite.RequestPermissions(context,Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Intent intent = new Intent(this, LocationService.class);
                startService(intent);
            }
        }
        listView = findViewById(R.id.list_view_1);
        m3_tv_service = findViewById(R.id.m3_tv_service);
        m3_tv_ipAddress = findViewById(R.id.m3_tv_ipAddress);
        m3_iv_setting = findViewById(R.id.m3_iv_setting);
        m3_iv_service_refersh = findViewById(R.id.m3_iv_service_refersh);
        m3_iv_logo = findViewById(R.id.m3_iv_logo);
        checkLife();//检查使用期限
//        System.out.println("SHA1："+SettingActivity.getAppSHA1(context));
        m3_iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SettingActivity.class);
                String action = intent.getAction();
                startActivity(intent);
                Map<String, Object> map = new HashMap<>();
                map.put("pageName", "系统设置");
                map.put("pagePath", SettingActivity.class.getName());
                map.put("time", new Date().getTime());
                String path = OkHttpUtil.URL_postPage_Intent_Path;
                try {
                    OkHttpUtil.postPage(context, setting.getMainUrl() + path, JSONUtil.toJsonStr(map));
                } catch (IOException e) {
                    Log.e(Tag + "[" + path + "]", e.toString());
                }
            }
        });
        m3_iv_service_refersh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m3_tv_service.setText("服务器状态加载中...");
                m3_tv_service.setTextColor(getColor(R.color.red));
                if (LoactionJson != null && !LoactionJson.isEmpty()) {
                    getupdatelist(LoactionJson.toStringPretty());
                } else {
                    getLocation();
                }
            }
        });
        m3_iv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder html=new StringBuilder();
                html.append("<p style='text-size:25px'><span style='color:#008577'>星曦向荣</span>的<i>Android</i>工具应用<b>ZH Tools</b></p>");
                html.append("<p>个人开发的工具APP，免费的影视和音乐资源，天气、字典、翻译、画图、新闻、二维码、文字转语音、ChatGPT...等丰富的功能等您来体验.</p>");
                html.append("<ol>");
                String weixin_url="https://mp.weixin.qq.com/s?__biz=MzIwMDQ2OTg4NA==&mid=2247484118&idx=1&sn=30dd3f7f2a4d93a6fdce4fb808e7c506&chksm=96fdfec5a18a77d35645d8e8f55477353aeb9a949fc73a2c302ca336155f8becae635e26f022#rd";
                html.append("<li>微信公众号：<a href='"+weixin_url+"'>星曦向荣</a></li>");
                html.append("<li>服务主页：<a href='"+setting.getMainUrl()+"'>"+setting.getMainUrl()+"</a></li>");
                html.append("</ol>");

                DialogUtil.dialog(context,"APP简介",html.toString(),true);
            }
        });
        setAdapter();
        versionCode = PhoneSystem.getVersionCode(this);
        m3_tv_ipAddress.setText("应用版本号：" + versionCode);
        getupdatelist("");
    }

    private List<String> titleItem;
    private void setAdapter() {
        String[] m3_titles = AppSetting.M3_Titles;
        titleItem = new ArrayList<>();
        List<String> list = new ArrayList<>();
        Map<String, Boolean> map = setting.getDisplayM3Titles();
        for (String title : m3_titles) {
            if (map.get(title)) {
                titleItem.add(title);
                list.add(title.substring(title.indexOf('.')+1));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, R.layout.item_list_text, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = null;

//                ArrayAdapter<String> adapter1 = (ArrayAdapter<String>) adapterView.getAdapter();
//                String item = adapter1.getItem(i);
                String item = titleItem.get(i);
                String[] split = item.split("\\.");
                Integer integer = 0;
                if (split.length >= 2) {
                    integer = Integer.valueOf(split[0]);
                } else {
                    new RuntimeException("列表strTitle标题格式错误！格式为：数字.标题");
                }
                Class<?> aClass = AppSetting.M3_contextMap.get(integer);
                if (aClass != null) {
                    intent = new Intent(context, aClass);
                    Map<String, Object> map = new HashMap<>();
                    map.put("pageName", item);
                    map.put("pagePath", aClass.getName());
                    map.put("time", new Date().getTime());
                    String path = OkHttpUtil.URL_postPage_Intent_Path;
                    try {
                        OkHttpUtil.postPage(context, setting.getMainUrl() + path, JSONUtil.toJsonStr(map));
                    } catch (IOException e) {
                        Log.e(Tag + "[" + path + "]", e.toString());
                    }
                }
                if (intent != null) {
                    if (setting.getSetting(setting.flag_service_life, setting.default_is_service_life)) {
                        String life_info = setting.getService_life_info();
                        AppLife appLife = new Gson().fromJson(life_info, AppLife.class);
                        Integer[] index = appLife.getIndex();
                        if (index!=null) {
                            Log.e(Tag,"index:"+index+",i:"+integer);
                            if (ArrayUtil.exist(index,integer)){
                                startActivity(intent);
                            }else {
                                DialogUtil.dialog(context, "功能使用限制", "对不起，该功能您暂时还无法使用，若有疑问请联系作者！");
                            }
                        }else {
                            startActivity(intent);
                        }
                    } else
                        DialogUtil.dialog(context, "APP使用权限到期", "对不起，您的APP使用权限到期，若需继续使用，请联系作者给您续期！");
                }
            }
        });
    }




    private void getBuild() {
        Log.d(Tag, "主板：" + Build.BOARD);
        Log.d(Tag, "Android系统定制商：" + Build.BRAND);
        Log.d(Tag, "cpu指令集：" + Build.CPU_ABI);
        Log.d(Tag, "设备参数：" + Build.DEVICE);
        Log.d(Tag, "显示屏参数：" + Build.DISPLAY);
        Log.d(Tag, "硬件名称：" + Build.FINGERPRINT);
        Log.d(Tag, "host：" + Build.HOST);
        Log.d(Tag, "修订版本列表：" + Build.ID);
        Log.d(Tag, "硬件制造商：" + Build.MANUFACTURER);
        Log.d(Tag, "版本：" + Build.MODEL);
        Log.d(Tag, "手机制造商：" + Build.PRODUCT);
        Log.d(Tag, "描述build的标签：" + Build.TAGS);
        Log.d(Tag, "时间：" + Build.TIME);
        Log.d(Tag, "builder类型：" + Build.TYPE);
        Log.d(Tag, "用户：" + Build.USER);
    }

    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }


    private void checkLife() {
        String service_life_info = setting.getService_life_info();
        AppLife appLife;
        Gson gson = new Gson();
        if (JSONUtil.isTypeJSON(service_life_info)) {
            appLife = gson.fromJson(service_life_info, AppLife.class);
            long maxDay = DateUtil.between(new Date(), new Date(appLife.getCreateTime()), DateUnit.DAY);
            if (maxDay > appLife.getMaxDay()) {
                setting.setSetting(setting.flag_service_life, false);
            } else {
                setting.setSetting(setting.flag_service_life, true);
            }
        } else {
            appLife = new AppLife();
            appLife.setCreateTime(new Date().getTime());
            appLife.setMaxDay(7);
            setting.setSetting(setting.flag_service_life, true);
            setting.setService_life_info(gson.toJson(appLife));
        }
    }
    /**
     * 查询更新
     * v23.04.10及以下
     */
    public void getupdatelist(String json) {
        if (dialogUtil == null)
            dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog();
        String url = setting.getMainUrl()
                + "config/updateApp/" + getResources().getString(R.string.app_name);
        OkHttpUtils
                .post()
                .addParams("json", json)
                .url(url)
                .addHeader("User-Agent", GetPhoneInfo.getHead(context, true))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialogUtil.closeProgressDialog();
                        String error = OkHttpMessageUtil.error(e);
                        Toast.makeText(context, "错误：" + error, Toast.LENGTH_SHORT).show();
                        m3_tv_service.setText(error);
                        m3_tv_service.setTextColor(getColor(R.color.red));
                        Log.e(Tag, "更新错误：" + e.toString());
                        setting.setMainUrl(null);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (response.indexOf("WEB服务器没有运行") < 1) {
                                Gson gson = new Gson();
                                Message msg = gson.fromJson(response, Message.class);
                                String title = msg.getTitle();
                                int compare = VersionComparator.INSTANCE.compare(title, GetPhoneInfo.versionCode(context));
                                if (compare>0){
                                    showUpdate(msg.getMessage(),appversion(title));
                                }else {
                                    Log.d(Tag, "无需更新");
                                }
                                if (msg.getObj()!=null&& FormatUtil.isWebUrl(StrUtil.toString(msg.getObj()))){
                                    setting.setMainUrl(StrUtil.toStringOrNull(msg.getObj()));
                                }
                                m3_tv_service.setText("服务器已连接");
                                m3_tv_service.setTextColor(getColor(R.color.colorPrimary));
                            }else {
                                m3_tv_service.setText("WEB服务器没有运行");
                                m3_tv_service.setTextColor(getColor(R.color.orange));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            dialogUtil.closeProgressDialog();
                            Log.d(Tag, "更新：" + response);
                        }
                    }
                });
    }


    /**
     * 查询更新
     * v23.04.10及以下
     */
    public void getupdatelist1(String json) {
//        getBuild();
        if (dialogUtil == null)
            dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog();
        String url = setting.getMainUrl()
                + "config/updateApp/" + getResources().getString(R.string.app_name);
        OkHttpUtils
                .post()
                .addParams("json", json)
                .url(url)
                .addHeader("User-Agent", GetPhoneInfo.getHead(context, true))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialogUtil.closeProgressDialog();
                        String error = OkHttpMessageUtil.error(e);
                        Toast.makeText(context, "错误：" + error, Toast.LENGTH_SHORT).show();
                        m3_tv_service.setText(error);
                        m3_tv_service.setTextColor(getColor(R.color.red));
                        Log.e(Tag, "错误：" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        try {
                            if (response.indexOf("WEB服务器没有运行") < 1) {
                                Message msg = null;
                                Gson gson = new Gson();
                                try {
                                    msg = gson.fromJson(response, Message.class);
                                    if (msg.getCode() == 200) {
                                        if (msg.getTitle().equals(getResources().getString(R.string.app_name))) {
                                            sharedPreferences = getSharedPreferences("update", MODE_PRIVATE);
                                            String urlname = sharedPreferences.getString("urlname", "");
                                            String url_name = msg.getMessage();
                                            if (!urlname.equals(url_name)) {
                                                String version = appversion(url_name);
                                                int compare = VersionComparator.INSTANCE.compare(version, versionCode);
                                                if (compare > 0) {
                                                    String url = getResources().getString(R.string.zhangheng_url)
                                                            + "fileload/download/" + url_name;
                                                    showUpdate(url,version);
                                                } else {
                                                    Log.d(Tag, "无需更新");
                                                }
                                            } else {
                                                saveUrlName(url_name);
                                            }
                                        } else {
                                            Log.e(Tag, "更新app名称不一致");
                                        }
                                    } else {
                                        Log.d(Tag, msg.toString());
                                    }
                                    m3_tv_service.setText("服务器已连接");
                                    m3_tv_service.setTextColor(getColor(R.color.colorPrimary));
                                } catch (Exception e) {
                                    Log.e(Tag, "更新错误" + e.toString());
                                }
                            } else {
                                m3_tv_service.setText("WEB服务器没有运行");
                                m3_tv_service.setTextColor(getColor(R.color.orange));
                            }

                            Log.d(Tag, "更新：" + response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            dialogUtil.closeProgressDialog();
                        }
                    }
                });
    }

    /**
     * 应用版本信息格式化
     *
     * @param name
     * @return
     */
    public String appversion(String name) {
        String[] strings = name.split("/");
        String appname = strings[strings.length - 1].replace(".apk", "");
        String[] s = appname.split("_");
        String app;
        if (s.length > 1) {
            app = s[s.length - 1];
        } else {
            app = s[0];
        }
        return app;
    }

    /**
     * 展示更新提示弹窗
     *
     * @param url
     * @param app_version
     */
    public void showUpdate(final String url,final String app_version) {
//        String app_version = appversion(name);
        builder = new AlertDialog.Builder(this)
                .setTitle("更新")
                .setMessage("有新的版本《" + app_version + "》可以更新，是否去下载更新包？" +
                        "\n【链接更新】直接用更新链接下载，【主页更新】去主页链接下载")
                .setPositiveButton("去更新(链接更新)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String url = getResources().getString(R.string.zhangheng_url)
//                                + "fileload/download/" + name;
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("去更新(主页更新)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url = getResources().getString(R.string.zhangheng_url);
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                })
                .setNeutralButton("下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
//                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context)
//                                .setTitle("提示")
//                                .setMessage("忽略此版本代表<相同版本>的更新不在提示，如果有其他版本，还会继续提示更新")
//                                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        saveUrlName(name);
//                                    }
//                                })
//                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.dismiss();
//                                    }
//                                });
//                        builder1.create().show();
                    }
                });
        builder.create().show();
    }

    private void saveUrlName(String name) {
        sharedPreferences = getSharedPreferences("update", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("urlname", name);
        editor.apply();
    }

    //权限检查
    //是否需要检测后台定位权限，设置为true时，如果用户没有给予后台定位权限会弹窗提示
    private boolean needCheckBackLocation = false;
    //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
    private static String BACK_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @Override
    protected void onResume() {
        try {
            super.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (isNeedCheck) {
                    checkPermissions(permissions);
                }
            }
            checkPermission();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //检查申请权限
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
//                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    isNeedCheck = true;
                } else {

                }
            }
        }
    }

    /**
     * @param
     * @since 2.5.0
     */
    @TargetApi(23)
    private void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    try {
                        String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class, int.class});
                        method.invoke(this, array, 0);
                    } catch (Throwable e) {

                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    @TargetApi(23)
    private List<String> findDeniedPermissions(String[] permissions) {
        try {
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                for (String perm : permissions) {
                    if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
                            || shouldShowMyRequestPermissionRationale(perm)) {
                        if (!needCheckBackLocation
                                && BACK_LOCATION_PERMISSION.equals(perm)) {
                            continue;
                        }
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            return needRequestPermissonList;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private int checkMySelfPermission(String perm) {
        try {
            Method method = getClass().getMethod("checkSelfPermission", new Class[]{String.class});
            Integer permissionInt = (Integer) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return -1;
    }

    private boolean shouldShowMyRequestPermissionRationale(String perm) {
        try {
            Method method = getClass().getMethod("shouldShowRequestPermissionRationale", new Class[]{String.class});
            Boolean permissionInt = (Boolean) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        try {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (requestCode == PERMISSON_REQUESTCODE) {
                    if (!verifyPermissions(paramArrayOfInt)) {
                        showMissingPermissionDialog();
                        isNeedCheck = false;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限");

            // 拒绝, 退出应用
            builder.setNegativeButton("退出应用",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                finish();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setPositiveButton("去设置",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                startAppSettings();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setCancelable(false);

            builder.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        try {
            Intent intent = new Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void getLocation() {
        if (dialogUtil == null)
            dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog("初始化加载中...");
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                try {
                    if (amapLocation != null) {
                        if (amapLocation.getErrorCode() == 0) {
                            LoactionJson = JSONUtil.createObj();
                            //获取定位时间
                            String time = com.zhangheng.util.TimeUtil.toTime(new Date(amapLocation.getTime()));
                            LoactionJson.set("time", time);
                            //地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                            String address = amapLocation.getAddress();
                            LoactionJson.set("address", address);
                            //获取纬度
                            double latitude = amapLocation.getLatitude();
                            LoactionJson.set("latitude", latitude);
                            //获取经度
                            double longitude = amapLocation.getLongitude();
                            LoactionJson.set("longitude", longitude);
                            //海拔
                            double altitude = amapLocation.getAltitude();
                            LoactionJson.set("altitude", altitude);
                            //速度 单位：米/秒
                            float speed = amapLocation.getSpeed();
                            LoactionJson.set("speed", speed);
                            //获取方向角信息
                            float bearing = amapLocation.getBearing();
                            LoactionJson.set("bearing", bearing);
                            //获取室内定位楼层
                            String floor = amapLocation.getFloor();
                            LoactionJson.set("floor", floor);
                            String aoiName = amapLocation.getAoiName();
                            LoactionJson.set("aoiName", aoiName);
                            String json = LoactionJson.toString();
                            setting.setFlag_phone_location(json);
                            System.out.println(json);
                            getupdatelist(json);
                        } else {
                            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                            Log.e(Tag + "AmapError", "location Error, ErrCode:"
                                    + amapLocation.getErrorCode() + ", errInfo:"
                                    + amapLocation.getErrorInfo());
                            if (LoactionJson != null)
                                LoactionJson.clear();
                            getupdatelist("");
                        }
                    } else {
                        if (LoactionJson != null)
                            LoactionJson.clear();
                        getupdatelist("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (LoactionJson != null)
                        LoactionJson.clear();
                    getupdatelist("");
                } finally {
//                    dialogUtil.closeProgressDialog();
                }
            }
        });
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 1500) {
                Toast.makeText(context, "再按一次退出！", Toast.LENGTH_LONG).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
