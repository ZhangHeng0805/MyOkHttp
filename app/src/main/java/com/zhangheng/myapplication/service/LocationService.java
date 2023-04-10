package com.zhangheng.myapplication.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.permissions.ReadAndWrite;
import com.zhangheng.myapplication.service.receiver.LocationReceiver;
import com.zhangheng.myapplication.util.RandomrUtil;

import java.util.Date;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class LocationService extends MyService {
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    public JSONObject LoactionJson = null;
    protected Context context=LocationService.this;

    private final static String Tag = "位置监听服务";

    @Override
    public void onCreate() {
        super.onCreate();

        initLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ReadAndWrite.RequestPermissions(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                &&ReadAndWrite.RequestPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION))
        new Thread(new Runnable() {
            @Override
            public void run() {
//                startForeground(1,buildNotification());
                mLocationClient.setLocationOption(mLocationOption);
                mLocationClient.startLocation();
            }
        }).start();
        int random = RandomrUtil.createRandom(1, 6);
        long hour = random*60*60*1000;
//        long hour = 10 * 1000;
        timingService(hour, LocationReceiver.class);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        mLocationOption = getDefaultOption();
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
       getLocation();
    }

    /**
     * 默认的定位参数
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
//        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
//        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
//        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
//        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(true);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(false); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    private void getLocation() {

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
                            Log.d(Tag, json);
                            OkHttpUtil.postLocation(context, json);
                        } else {
                            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                            Log.e(Tag + "AmapError", "location Error, ErrCode:"
                                    + amapLocation.getErrorCode() + ", errInfo:"
                                    + amapLocation.getErrorInfo());
                            if (LoactionJson != null)
                                LoactionJson.clear();

                        }
                    } else {
                        if (LoactionJson != null)
                            LoactionJson.clear();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (LoactionJson != null)
                        LoactionJson.clear();

                } finally {
//                    dialogUtil.closeProgressDialog();
                }
            }
        });

//        //初始化AMapLocationClientOption对象
//        mLocationOption = new AMapLocationClientOption();
//        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        //获取一次定位结果：
//        //该方法默认为false。
//        mLocationOption.setOnceLocation(true);
//        //获取最近3s内精度最高的一次定位结果：
//        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
//        mLocationOption.setOnceLocationLatest(true);
//        //设置是否返回地址信息（默认返回地址信息）
//        mLocationOption.setNeedAddress(true);
//        //关闭缓存机制
//        mLocationOption.setLocationCacheEnable(false);
//        //给定位客户端对象设置定位参数
//        mLocationClient.setLocationOption(mLocationOption);
//        //启动定位
//        mLocationClient.startLocation();

    }



    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;

    @SuppressLint("NewApi")
    private Notification buildNotification() {

        Notification.Builder builder = null;
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = getPackageName();
            if (!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        context.getClass().getSimpleName(), NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(getApplicationContext(), channelId);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        builder.setSmallIcon(R.drawable.logo)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("定位Srevice正在后台运行")
                .setContentIntent(PendingIntent.getActivity(this, 0, this.getPackageManager().getLaunchIntentForPackage(this.getPackageName()), PendingIntent.FLAG_UPDATE_CURRENT))
                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }

}
