package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import com.zhangheng.myapplication.getphoneMessage.Address;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;
import com.zhangheng.myapplication.getphoneMessage.RegisterMessage;
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
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };
    private String getPhone,model,sdk,release,versionCode;

    private ListView listView;
    private TextView m3_tv_service, m3_tv_ipAddress;
    private AlertDialog.Builder builder;
    private SharedPreferences sharedPreferences;
    private String[] strArr = new String[]{
            "1.原生OkHttp的Get和Post请求文本数据",//1
            "2.使用OkHttpUtil的Post提交文本数据",//2
            "4.使用OkHttpUtil下载大文件",//4
            "5.上传文件和检索本地文件",//5
            "6.请求单张图片并显示",//6
            "7.查询天气列表（API）",//7
            "8.生成二维码（API）",//8
            "9.新华字典查询（API）",//9
            "10.图书电商查询（API）",//10
            "11.查询文件列表并下载（自制服务器）",//11
            "12.自制地图（高德地图）",//12
            "13.自制网络聊天室",//13
            "14.自制聊天室(无效)",//14
            "15.购物软件框架（自制服务器）",
            "16.自制下拉刷新的ListView（测试）",
    };

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
            if (phoneManager.getLine1Number().startsWith("+86")){
                getPhone=phoneManager.getLine1Number().replace("+86","");
            }else {
                getPhone = phoneManager.getLine1Number();//得到电话号码
            }
//            String getPhone = phoneManager.getLine1Number();//得到电话号码
        versionCode = PhoneSystem.getVersionCode(this);

//        m3_tv_ipAddress.setText("电话："+getPhone+" 手机型号"+model+" sdk版本号"+sdk+" 版本号"+release);
        m3_tv_ipAddress.setText("当前版本型号："+versionCode);

//        m3_tv_ipAddress.setText(s);
        getupdatelist();
    }
    private void setAdapter(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                Main3Activity.this, R.layout.item_list_text, strArr);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(Main3Activity.this,"点击："+i,Toast.LENGTH_SHORT).show();
                Intent intent;
                switch (i){
                    case 0:
                        intent=new Intent(Main3Activity.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent=new Intent(Main3Activity.this,Main2Activity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent=new Intent(Main3Activity.this,Main4Activity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent=new Intent(Main3Activity.this,Main5Activity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent=new Intent(Main3Activity.this,Main6Activity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent=new Intent(Main3Activity.this,Main7Activity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent=new Intent(Main3Activity.this,Main8Activity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        intent=new Intent(Main3Activity.this,Main9Activity.class);
                        startActivity(intent);
                        break;
                    case 8:
                        intent=new Intent(Main3Activity.this,Main10Activity.class);
                        startActivity(intent);
                        break;
                    case 9:
                        intent=new Intent(Main3Activity.this,Main11Activity.class);
                        startActivity(intent);
                        break;
                    case 10:
                        intent=new Intent(Main3Activity.this,M12_LoginActivity.class);
                        startActivity(intent);
                        break;
                    case 11:
                        intent=new Intent(Main3Activity.this,Main13Activity.class);
                        startActivity(intent);
                        break;
                    case 12:
                        intent=new Intent(Main3Activity.this,Main14Activity.class);
                        startActivity(intent);
                        break;
                    case 13:
                        intent=new Intent(Main3Activity.this,Main15Activity.class);
                        startActivity(intent);
                        break;
                    case 14:
                        intent=new Intent(Main3Activity.this, Test1Activity.class);
                        startActivity(intent);
                        break;

                }
            }
        });
    }

    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }
    public void getupdatelist(){
        String url=getResources().getString(R.string.upload_html_url)
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
                .addParams("phonenum", getPhone)
                .addParams("model", model)
                .addParams("sdk", sdk)
                .addParams("release", release)
                .addParams("time", TimeUtil.getSystemTime())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (e.getMessage().indexOf("404")>1||e.getMessage().indexOf("not found")>1){
                            Toast.makeText(Main3Activity.this,"服务器未连接",Toast.LENGTH_SHORT).show();
                            m3_tv_service.setText("服务器未连接");
                        }else if (e.getMessage().startsWith("Unable to resolve host")){
                            Toast.makeText(Main3Activity.this,"网络异常",Toast.LENGTH_SHORT).show();
                            m3_tv_service.setText("网络异常");
                        }else if (e.getMessage().indexOf("timeout")>=0){
                            Toast.makeText(Main3Activity.this,"服务器连接超时",Toast.LENGTH_SHORT).show();
                            m3_tv_service.setText("服务器连接超时");
                        }
                        else{
                            Toast.makeText(Main3Activity.this,"错误："+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
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
                        String url = getResources().getString(R.string.upload_html_url)
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
