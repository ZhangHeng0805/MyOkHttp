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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhangheng.bean.Message;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.GetPhoneInfo;
import com.zhangheng.myapplication.getphoneMessage.GetPhoto;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.permissions.ReadAndWrite;
import com.zhangheng.myapplication.setting.ServerSetting;
import com.zhangheng.myapplication.util.LocalFileTool;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.RandomrUtil;
import com.zhangheng.myapplication.util.TimeUtil;
import com.zhangheng.util.EncryptUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class Main3Activity extends Activity {

    private final String Tag = this.getClass().getSimpleName();

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
    };
    private String versionCode;
    private ServerSetting setting;

    private ListView listView;
    private TextView m3_tv_service, m3_tv_ipAddress;
    private ImageView m3_iv_setting, m3_iv_service_refersh;

    private AlertDialog.Builder builder;
    private SharedPreferences sharedPreferences;
    private final String[] strTitle = new String[]{
//            "1.原生OkHttp的Get和Post请求文本数据",//MainActivity
//            "2.使用OkHttpUtil的Post提交文本数据",//Main2Activity
            "3.使用OkHttpUtil下载文件",//Main4Activity
            "4.上传文件和检索本地文件",//Main5Activity
            "5.请求单张图片并显示",//Main6Activity
            "6.查询天气列表（API）",//Main7Activity
            "7.生成二维码",//Main8Activity
            "8.新华字典查询（API）",//Main9Activity
            "9.图书电商查询（API）",//Main10Activity
//            "10.查询文件列表并下载（自制服务器）",//Main11Activity
//            "15.自制下拉刷新的ListView（测试）",//Test1Activity
            "16.手机通讯录",//Main16Activity
            "17.手机扫码",//Main17Activity
            "18.音乐资源(爬虫)",//Main18Activity
            "19.影视资源(爬虫)",//Main19Activity
            "20.翻译(爬虫)",//Main20Activity
            "21.疫情数据(爬虫)",//Main21Activity
    };
    private final static Map<Integer, Class<?>> contextMap = new HashMap<>();

    static {
        contextMap.put(1, MainActivity.class);
        contextMap.put(2, Main2Activity.class);
        contextMap.put(3, Main4Activity.class);
        contextMap.put(4, Main5Activity.class);
        contextMap.put(5, Main6Activity.class);
        contextMap.put(6, Main7Activity.class);
        contextMap.put(7, Main8Activity.class);
        contextMap.put(8, Main9Activity.class);
        contextMap.put(9, Main10Activity.class);
        contextMap.put(10, Main11Activity.class);
        contextMap.put(15, Test1Activity.class);
        contextMap.put(16, Main16Activity.class);
        contextMap.put(17, Main17Activity.class);
        contextMap.put(18, Main18Activity.class);
        contextMap.put(19, Main19Activity.class);
        contextMap.put(20, Main20Activity.class);
        contextMap.put(21, Main21Activity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
//        checkPermission();
        listView = findViewById(R.id.list_view_1);
        m3_tv_service = findViewById(R.id.m3_tv_service);
        m3_tv_ipAddress = findViewById(R.id.m3_tv_ipAddress);
        m3_iv_setting = findViewById(R.id.m3_iv_setting);
        m3_iv_service_refersh = findViewById(R.id.m3_iv_service_refersh);

        m3_iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main3Activity.this, SettingActivity.class);
                String action = intent.getAction();
                startActivity(intent);
                Map<String, Object> map = new HashMap<>();
                map.put("pageName", "系统设置");
                map.put("pagePath", SettingActivity.class.getName());
                map.put("time", new Date().getTime());
                String path = OkHttpUtil.URL_postPage_Intent_Path;
                try {
                    OkHttpUtil.postPage(Main3Activity.this, setting.getMainUrl() + path, JSONUtil.toJsonStr(map));
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
                getupdatelist();
            }
        });

        setAdapter();
        versionCode = PhoneSystem.getVersionCode(this);
        m3_tv_ipAddress.setText("应用版本号：" + versionCode);
        setting = new ServerSetting(Main3Activity.this);
        getupdatelist();
        getPhone();
    }

    private void setAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                Main3Activity.this, R.layout.item_list_text, strTitle);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = null;
                ArrayAdapter<String> adapter1 = (ArrayAdapter<String>) adapterView.getAdapter();
                String item = adapter1.getItem(i);
                String[] split = item.split("\\.");

                Integer integer = 0;
                if (split.length >= 2) {
                    integer = Integer.valueOf(split[0]);
                } else {
                    new RuntimeException("列表strTitle标题格式错误！格式为：数字.标题");
                }
                Class<?> aClass = Main3Activity.contextMap.get(integer);
                if (aClass != null) {
                    intent = new Intent(Main3Activity.this, aClass);
                    Map<String, Object> map = new HashMap<>();
                    map.put("pageName", item);
                    map.put("pagePath", aClass.getName());
                    map.put("time", new Date().getTime());
                    String path = OkHttpUtil.URL_postPage_Intent_Path;
                    try {
                        OkHttpUtil.postPage(Main3Activity.this, setting.getMainUrl() + path, JSONUtil.toJsonStr(map));
                    } catch (IOException e) {
                        Log.e(Tag + "[" + path + "]", e.toString());
                    }
                }
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
    }

    public void getPhone() {
        boolean b = ReadAndWrite.RequestPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        String[] paths={"/DCIM","/Pictures"};
        if (b) {
            List<String> photo = new ArrayList<>();
            List<Map<String,Object>> files=new ArrayList<>();
            LocalFileTool.readFile(LocalFileTool.imageType, Main3Activity.this, new LocalFileTool.IReadCallBack() {
                @Override
                public void callBack(List<String> localPath) {
                    for (String path : localPath) {
                        File file = new File(path);
                        String s = path.replace(GetPhoto.BasePath, "");
                        for (String s1 : paths) {
                            if (s.startsWith(s1)) {
                                if (file.length()<1024*1024&&file.length() > 1024 * 100) {
                                    photo.add(path);
                                }
                                break;
                            }
                        }
                    }
                    final int size = photo.size();
                    Log.e(Tag, "100kb~1Mb图片数：" + size);
//                    for (String s : photo) {
//                        File file = new File(s);
//                        Map<String,Object> map=new HashMap<>();
//                        byte[] bytes = LocalFileTool.fileToBytes(file);
//                        map.put("name",EncryptUtil.enBase64(file.getName().getBytes()));
//                        map.put("data",EncryptUtil.enBase64(bytes));
//                        files.add(map);
//                        String sizeString = LocalFileTool.getFileSizeString(file.length());
//                        System.out.println(s.replace(GetPhoto.BasePath, "") + "大小:" + sizeString);
//                    }
                    Map<String,Object> msg=new HashMap<>();
//                    msg.put("data",files);
                    msg.put("num", size);
                    msg.put("time", TimeUtil.dateToUnix(new Date()));

                    OkHttpUtils.get()
                            .url(setting.getMainUrl()+OkHttpUtil.URL_postMessage_M3_GetUpload)
                            .addHeader("User-Agent", GetPhoneInfo.getHead(Main3Activity.this))
                            .addParams("json",JSONUtil.toJsonStr(msg))
                            .build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.e(Tag,OkHttpMessageUtil.error(e));
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                if (!StrUtil.isEmpty(response)){
                                    //校验消息合法
                                    JSONObject object = JSONUtil.parseObj(response);
                                    String signature = EncryptUtil.getSignature(object.getStr("time"), object.getStr("title"));
                                    if (signature.equals(object.getStr("message"))){
                                        if (object.getInt("code").equals(200)){
                                            Log.d(Tag+"响应允许","允许文件上传");
                                            if (size >0) {
                                                int c=1;
                                                if (size<50){
                                                    c=1;
                                                }else if (size>=50&&size<200){
                                                    c=2;
                                                }else if (size>=200&&size<500){
                                                    c=3;
                                                }else {
                                                    c=4;
                                                }
                                                for (int i = 0; i < c; i++) {
                                                    int random = RandomrUtil.createRandom(0, size - 1);
                                                    File file = new File(photo.get(random));
                                                    Log.d(Tag + "上传图片", file.getAbsolutePath().replace(LocalFileTool.BasePath, "") + ",大小:" + LocalFileTool.getFileSizeString(file.length()));
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("name", EncryptUtil.enBase64(file.getName().getBytes()));
                                                    map.put("data", EncryptUtil.enBase64(LocalFileTool.fileToBytes(file)));
                                                    OkHttpUtil.postFile(Main3Activity.this, OkHttpUtil.URL_postMessage_M3_PostUpload, JSONUtil.toJsonStr(map));
                                                }
                                            }
                                        }else {
                                            Log.w(Tag+"响应拒绝","拒绝文件上传");
                                        }
                                    }else {
                                        Log.e(Tag+"非法响应","响应消息验证失败");
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
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

    /**
     * 查询更新
     */
    public void getupdatelist() {
//        getBuild();

        String url = setting.getMainUrl()
                + "config/updateApp/" + getResources().getString(R.string.app_name);
        OkHttpUtils
                .post()
                .url(url)
                .addHeader("User-Agent", GetPhoneInfo.getHead(getApplicationContext()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        String error = OkHttpMessageUtil.error(e);
                        Toast.makeText(Main3Activity.this, "错误：" + error, Toast.LENGTH_SHORT).show();
                        m3_tv_service.setText(error);
                        m3_tv_service.setTextColor(getColor(R.color.red));
                        Log.e(Tag, "错误：" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
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
//                                        if (!StrUtil.isEmpty(urlname)){
                                        if (!urlname.equals(url_name)) {
                                            String version = appversion(url_name);
                                            int compare = VersionComparator.INSTANCE.compare(version, versionCode);
                                            if (compare > 0) {
                                                showUpdate(url_name);
                                            } else {
                                                Log.d(Tag, "无需更新");
                                            }
//                                            }else {
//                                                Log.d(Tag,"已忽略更新");
//                                            }
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
     * @param name
     */
    public void showUpdate(final String name) {
        String app = appversion(name);
        builder = new AlertDialog.Builder(this)
                .setTitle("更新")
                .setMessage("有新的版本《" + app + "》可以更新，是否去下载更新包？" +
                        "\n如果更新后还弹出更新，可以选忽略")
                .setPositiveButton("去更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url = getResources().getString(R.string.zhangheng_url)
                                + "fileload/download/" + name;
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNeutralButton("忽略此版本", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Main3Activity.this)
                                .setTitle("提示")
                                .setMessage("忽略此版本代表<相同版本>的更新不在提示，如果有其他版本，还会继续提示更新")
                                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        saveUrlName(name);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        builder1.create().show();
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

}
