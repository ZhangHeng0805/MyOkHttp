package com.zhangheng.myapplication.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.service.receiver.MyReceiver;
import com.zhangheng.myapplication.setting.ServerSetting;
import com.zhangheng.myapplication.util.RandomrUtil;

import java.util.Date;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class MyService extends Service {
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    public JSONObject LoactionJson = null;
    private ServerSetting setting;
    private final Context context = MyService.this;
    private final static String Tag="位置监听服务";


    @Override
    public void onCreate() {
        super.onCreate();
        setting=new ServerSetting(context);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //这里开辟一条线程,用来执行具体的逻辑操作:
//        Log.e("MyService", new Date().toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                getLocation();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int random = RandomrUtil.createRandom(1, 12);
        int anHour = random*60*60*1000;
        long triggerAtTime = System.currentTimeMillis() + anHour;
        Intent i = new Intent(this, MyReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }
    public void getLocation() {

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
                            OkHttpUtil.postLocation(context,OkHttpUtil.URL_postMessage_location,json);
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
}
