package com.zhangheng.myapplication;

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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhangheng.myapplication.Object.Resuilt;
import com.zhangheng.myapplication.activity.Test1Activity;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.TimeUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class Main3Activity extends Activity {

    private String[] permissions = {
            Manifest.permission.INTERNET,//网络
            Manifest.permission.ACCESS_WIFI_STATE,//写入状态
            Manifest.permission.ACCESS_COARSE_LOCATION,//粗略定位
            Manifest.permission.ACCESS_FINE_LOCATION,//精确定位
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,//后台定位
            Manifest.permission.ACCESS_NETWORK_STATE,//网络状态
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//写外部存储器
            Manifest.permission.READ_PHONE_STATE,//读取手机状态
    };
    private String getPhone,model,sdk,release,versionCode;

    private ListView listView;
    private TextView m3_tv_service, m3_tv_ipAddress;
    private AlertDialog.Builder builder;
    private SharedPreferences sharedPreferences;
    private final String[] strTitle = new String[]{
//            "1.原生OkHttp的Get和Post请求文本数据",//MainActivity
//            "2.使用OkHttpUtil的Post提交文本数据",//Main2Activity
            "3.使用OkHttpUtil下载文件",//Main4Activity
//            "4.上传文件和检索本地文件",//Main5Activity
            "5.请求单张图片并显示",//Main6Activity
            "6.查询天气列表（API）",//Main7Activity
            "7.生成二维码",//Main8Activity
            "8.新华字典查询（API）",//Main9Activity
            "9.图书电商查询（API）",//Main10Activity
//            "10.查询文件列表并下载（自制服务器）",//Main11Activity
            "11.自制地图（高德地图）",//M12_LoginActivity
//            "12.自制网络聊天室",//Main13Activity
//            "13.自制聊天室(无效)",//Main14Activity
//            "14.购物软件框架（自制服务器）",//Main15Activity
//            "15.自制下拉刷新的ListView（测试）",//Test1Activity
            "16.手机通讯录",//Main16Activity
            "17.手机扫码",//Main17Activity
            "18.音乐资源爬虫",//Main18Activity
    };
    private final static Map<Integer,Class<?>> contextMap=new HashMap<>();
    static {
        contextMap.put(1,MainActivity.class);
        contextMap.put(2,Main2Activity.class);
        contextMap.put(3,Main4Activity.class);
        contextMap.put(4,Main5Activity.class);
        contextMap.put(5,Main6Activity.class);
        contextMap.put(6,Main7Activity.class);
        contextMap.put(7,Main8Activity.class);
        contextMap.put(8,Main9Activity.class);
        contextMap.put(9,Main10Activity.class);
        contextMap.put(10,Main11Activity.class);
        contextMap.put(11,M12_LoginActivity.class);
        contextMap.put(12,Main13Activity.class);
        contextMap.put(13,Main14Activity.class);
        contextMap.put(14,Main15Activity.class);
        contextMap.put(15,Test1Activity.class);
        contextMap.put(16,Main16Activity.class);
        contextMap.put(17,Main17Activity.class);
        contextMap.put(18,Main18Activity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
//        checkPermission();
        listView = findViewById(R.id.list_view_1);
        m3_tv_service = findViewById(R.id.m3_tv_service);
        m3_tv_ipAddress = findViewById(R.id.m3_tv_ipAddress);
        setAdapter();
        model = android.os.Build.MODEL; // 手机型号
        sdk = android.os.Build.VERSION.SDK; // SDK号
        release = "Android" + android.os.Build.VERSION.RELEASE; // android系统版本号
        String s = model + "\t" + sdk + "\t" + release;
            //获取自己手机号码
            TelephonyManager phoneManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

            if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            if(phoneManager.getLine1Number()!=null) {
                if (phoneManager.getLine1Number().startsWith("+86")) {
                    getPhone = phoneManager.getLine1Number().replace("+86", "");
                } else {
                    getPhone = phoneManager.getLine1Number();//得到电话号码
                }
            }
//            String getPhone = phoneManager.getLine1Number();//得到电话号码
        versionCode = PhoneSystem.getVersionCode(this);

//        m3_tv_ipAddress.setText("电话："+getPhone+" 手机型号"+model+" sdk版本号"+sdk+" 版本号"+release);
        m3_tv_ipAddress.setText("应用版本号："+versionCode);

//        m3_tv_ipAddress.setText(s);
        getupdatelist();
    }
    private void setAdapter(){
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

                Integer integer=0;
                if (split.length>=2) {
                    integer = Integer.valueOf(split[0]);
                }else {
                    new RuntimeException("列表strTitle标题格式错误！格式为：数字.标题");
                }
                Class<?> aClass = Main3Activity.contextMap.get(integer);
                if (aClass!=null){
                    intent=new Intent(Main3Activity.this,aClass);
                }
                if (intent!=null) {
                    startActivity(intent);
                }
            }
        });
    }

    private void startIntent(Context context){
        Intent intent=new Intent(Main3Activity.this, Main17Activity.class);
        startActivity(intent);
    }
    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 查询更新
     */
    public void getupdatelist(){
        String url=getResources().getString(R.string.zhangheng_url)
                +"filelist/updatelist/"+getResources().getString(R.string.app_name);
        Map<String,String> map=new HashMap<>();
        if (getPhone!=null) {
            map.put("phonenum", getPhone);
            map.put("model", model);
            map.put("sdk", sdk);
            map.put("release", release);
            map.put("time", TimeUtil.getSystemTime());
        }
        OkHttpUtils
                .post()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        String error = OkHttpMessageUtil.error(e);
                        Toast.makeText(Main3Activity.this,"错误："+error,Toast.LENGTH_SHORT).show();
                        m3_tv_service.setText(error);
                        Log.e("错误：",e.getMessage());
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        Resuilt resuilt=null;
                        Gson gson=new Gson();
                        try {
                            resuilt = gson.fromJson(response, Resuilt.class);
                        }catch (Exception e){

                        }
                        if (response.indexOf("WEB服务器没有运行")<1) {
                            if (resuilt != null) {
                                Toast.makeText(Main3Activity.this, "服务器已连接", Toast.LENGTH_SHORT).show();
                                m3_tv_service.setText("服务器已连接");
                                m3_tv_service.setTextColor(getColor(R.color.black));
                                if (!resuilt.getTitle().equals("null")) {
                                    if (resuilt.getTitle().equals(getResources().getString(R.string.app_name))) {
                                        sharedPreferences = getSharedPreferences("update", MODE_PRIVATE);
                                        String urlname = sharedPreferences.getString("urlname", "");
                                        if (!urlname.equals(resuilt.getMessage())) {
                                            if (!versionCode.equals(appversion(resuilt.getMessage())))
                                                showUpdate(resuilt.getMessage());
                                        } else {
                                            Log.d("urlname", "urlname与更新地址一致");
                                        }
                                    } else {
                                        Log.d("title", "title与应用的名称不一致");
                                    }
                                } else {
                                    Log.d("title", "title为null");
                                }
                            } else {
                                Log.d("resuilt", "resuilt为空");
                            }
                        }else {
                            m3_tv_service.setText("WEB服务器没有运行");
                        }
                        Log.d("更新：",response);
                    }
                });
    }

    /**
     * 应用版本信息格式化
     * @param name
     * @return
     */
    public String appversion(String name){
        String[] strings=name.split("/");
        String appname=strings[strings.length-1].replace(".apk","");
        String[] s = appname.split("_");
        String app;
        if (s.length>1){
            app=s[s.length-1];
        }else {
            app=s[0];
        }
        return app;
    }

    /**
     * 展示更新提示弹窗
     * @param name
     */
    public void showUpdate(final String name){
        String app=appversion(name);
        builder=new AlertDialog.Builder(this)
                .setTitle("更新")
                .setMessage("有新的版本《"+app+"》可以更新，是否去下载更新包？" +
                        "\n如果更新后还弹出更新，可以选忽略")
                .setPositiveButton("去更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        String url = getResources().getString(R.string.zhangheng_url)
                                +"downloads/downupdate/"+name;
                        intent.setData(Uri.parse(url));
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
                AlertDialog.Builder builder1=new AlertDialog.Builder(Main3Activity.this)
                        .setTitle("提示")
                        .setMessage("忽略此版本代表<相同版本>的更新不在提示，如果有其他版本，还会继续提示更新")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sharedPreferences=getSharedPreferences("update",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("urlname",name);
                                editor.apply();
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
        try{
            super.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (isNeedCheck) {
                    checkPermissions(permissions);
                }
            }
            checkPermission();
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    //检查申请权限
    private void checkPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
//                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    isNeedCheck = true;
                }else {

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
        try{
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

        }catch(Throwable e){
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
        try{
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                for (String perm : permissions) {
                    if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
                            || shouldShowMyRequestPermissionRationale(perm)) {
                        if(!needCheckBackLocation
                                && BACK_LOCATION_PERMISSION.equals(perm)) {
                            continue;
                        }
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            return needRequestPermissonList;
        }catch(Throwable e){
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
        try{
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
        return true;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        try{
            if (Build.VERSION.SDK_INT >= 23) {
                if (requestCode == PERMISSON_REQUESTCODE) {
                    if (!verifyPermissions(paramArrayOfInt)) {
                        showMissingPermissionDialog();
                        isNeedCheck = false;
                    }
                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限");

            // 拒绝, 退出应用
            builder.setNegativeButton("退出应用",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try{
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
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        try{
            Intent intent = new Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
