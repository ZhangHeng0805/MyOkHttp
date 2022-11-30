package com.zhangheng.myapplication.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.gson.Gson;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.adapter.WeatherList_Adapter;
import com.zhangheng.myapplication.bean.weather.JsonRootBean;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.setting.ServerSetting;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class Main7Activity extends Activity implements View.OnClickListener , GeocodeSearch.OnGeocodeSearchListener {
    private EditText m7_et_city;
    private Button m7_btn_query;
    private ListView m7_list_future;
    private LinearLayout m7_ll_realtime, m7_ll_future;
    private ImageView m7_img_realtime_icon;
    private TextView m7_text_realtime_city, m7_text_realtime, m7_text_info, m7_text_temperature, m7_text_direct, m7_text_power, m7_text_humidity, m7_text_aqi, m7_text_future_city;
    private LocationManager locationManager;
    private String locationProvider,city;       //位置提供器
    private GeocodeSearch geocodeSearch;
    private ProgressDialog progressDialog;

    private ServerSetting setting;
    private final String Tag=getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                } else {

                }
            }
        }
        setting=new ServerSetting(this);

        m7_et_city = findViewById(R.id.m7_et_city);
        m7_btn_query = findViewById(R.id.m7_btn_query);
        m7_btn_query.setOnClickListener(this);
        m7_img_realtime_icon = findViewById(R.id.m7_img_realtime_icon);
        m7_text_realtime_city = findViewById(R.id.m7_text_realtime_city);
        m7_text_realtime = findViewById(R.id.m7_text_realtime);
        m7_text_info = findViewById(R.id.m7_text_info);
        m7_text_temperature = findViewById(R.id.m7_text_temperature);
        m7_text_direct = findViewById(R.id.m7_text_direct);
        m7_text_power = findViewById(R.id.m7_text_power);
        m7_text_humidity = findViewById(R.id.m7_text_humidity);
        m7_text_aqi = findViewById(R.id.m7_text_aqi);
        m7_text_future_city = findViewById(R.id.m7_text_future_city);
        m7_list_future = findViewById(R.id.m7_list_future);
        getLocation(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.m7_btn_query:
                progressDialog= new ProgressDialog(this);
                progressDialog.setMessage("查询中，请稍后。。。");
                progressDialog.setIndeterminate(true);// 是否形成一个加载动画  true表示不明确加载进度形成转圈动画  false 表示明确加载进度
                progressDialog.setCancelable(false);//点击返回键或者dialog四周是否关闭dialog  true表示可以关闭 false表示不可关闭
                progressDialog.show();
                String city = m7_et_city.getText().toString();
                getRealWeather(city);
                break;
        }
    }

    private void getRealWeather(String city) {
        String url = "http://apis.juhe.cn/simpleWeather/query";
        String key = getResources().getString(R.string.key_weather);
        Map<String, String> params = new HashMap<>();
        params.put("city", city);
        params.put("key", key);
        OkHttpUtils
                .get()
                .params(params)
                .url(url)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        m7_text_realtime_city.setText("错误：" + OkHttpMessageUtil.error(e));
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        JsonRootBean jsonRootBean = gson.fromJson(response, JsonRootBean.class);
                        int error_code = jsonRootBean.getError_code();
                        Main7Activity context = Main7Activity.this;
                        if (error_code == 0) {
                            //当前状态栏设置
                            String city = jsonRootBean.getResult().getCity();
                            m7_text_realtime_city.setText("《" + city + "》当前天气情况");
                            //当前时间设置
                            SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
                            String time = df.format(new Date());// new Date()为获取当前系统时间
                            m7_text_realtime.setText("更新于" + time);
                            //天气图标设置
                            String wid = jsonRootBean.getResult().getRealtime().getWid();
                            int i = getDayIcon(wid);
                            m7_img_realtime_icon.setImageResource(i);
                            //天气设置
                            String info = jsonRootBean.getResult().getRealtime().getInfo();
                            m7_text_info.setText(info);
                            //温度设置
                            String temperature = jsonRootBean.getResult().getRealtime().getTemperature() + "℃";
                            m7_text_temperature.setText(temperature);
                            //风向设置
                            String direct = jsonRootBean.getResult().getRealtime().getDirect();
                            m7_text_direct.setText(direct);
                            //风力设置
                            String power = jsonRootBean.getResult().getRealtime().getPower();
                            m7_text_power.setText(power);
                            //湿度
                            String humidity = "湿度：" + jsonRootBean.getResult().getRealtime().getHumidity() + "%";
                            m7_text_humidity.setText(humidity);
                            //空气质量设置
                            String aqi = "空气质量：" + jsonRootBean.getResult().getRealtime().getAqi();
                            m7_text_aqi.setText(aqi);

                            //近期天气状态栏
                            int size = jsonRootBean.getResult().getFuture().size();
                            String future_text = "《" + city + "》近" + size + "天的天气";
                            //近期天气情况
                            m7_text_future_city.setText(future_text);
                            m7_list_future.setAdapter(new WeatherList_Adapter(context, jsonRootBean));
                        } else {
                            switch (error_code) {
                                case 207301:
                                    Toast.makeText(context, "错误的查询城市名", Toast.LENGTH_SHORT).show();
                                    break;
                                case 207302:
                                    Toast.makeText(context, "查询不到该城市的相关信息", Toast.LENGTH_SHORT).show();
                                    break;
                                case 207303:
                                    Toast.makeText(context, "网络错误，请重试", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    String msg = null;
                                    if (error_code ==10011||error_code==111){
                                        msg="当前IP请求超过限制";
                                    }else if (error_code ==10012||error_code==112){
                                        msg="请求超过次数限制,请明日再来";
                                    }else if (error_code ==10020||error_code==120){
                                        msg="功能维护中...";
                                    }else if (error_code ==10021||error_code==121){
                                        msg="对不起,该功能已停用";
                                    }
                                    else {
                                        msg="错误码："+ error_code;
                                    }
                                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            m7_text_realtime_city.setText("错误码：" + error_code);
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    public int getDayIcon(String wid) {
        int i = R.mipmap.undefined;
        switch (wid) {
            case "00":
                i = R.mipmap.day_00;
                break;
            case "01":
                i = R.mipmap.day_01;
                break;
            case "02":
                i = R.mipmap.day_02;
                break;
            case "03":
                i = R.mipmap.day_03;
                break;
            case "04":
                i = R.mipmap.day_04;
                break;
            case "05":
                i = R.mipmap.day_05;
                break;
            case "06":
                i = R.mipmap.day_06;
                break;
            case "07":
                i = R.mipmap.day_07;
                break;
            case "08":
                i = R.mipmap.day_08;
                break;
            case "09":
                i = R.mipmap.day_09;
                break;
            case "10":
                i = R.mipmap.day_10;
                break;
            case "11":
                i = R.mipmap.day_11;
                break;
            case "12":
                i = R.mipmap.day_12;
                break;
            case "13":
                i = R.mipmap.day_13;
                break;
            case "14":
                i = R.mipmap.day_14;
                break;
            case "15":
                i = R.mipmap.day_15;
                break;
            case "16":
                i = R.mipmap.day_16;
                break;
            case "17":
                i = R.mipmap.day_17;
                break;
            case "18":
                i = R.mipmap.day_18;
                break;
            case "19":
                i = R.mipmap.day_19;
                break;
            case "20":
                i = R.mipmap.day_20;
                break;
            case "21":
                i = R.mipmap.day_21;
                break;
            case "22":
                i = R.mipmap.day_22;
                break;
            case "23":
                i = R.mipmap.day_23;
                break;
            case "24":
                i = R.mipmap.day_24;
                break;
            case "25":
                i = R.mipmap.day_25;
                break;
            case "26":
                i = R.mipmap.day_26;
                break;
            case "27":
                i = R.mipmap.day_27;
                break;
            case "28":
                i = R.mipmap.day_28;
                break;
            case "29":
                i = R.mipmap.day_29;
                break;
            case "30":
                i = R.mipmap.day_29;
                break;
            case "31":
                i = R.mipmap.day_29;
                break;
            case "53":
                i = R.mipmap.day_53;
                break;


        }
        return i;
    }

    public int getNightIcon(String wid) {
        int i = R.mipmap.undefined;
        switch (wid) {
            case "00":
                i = R.mipmap.night_00;
                break;
            case "01":
                i = R.mipmap.night_01;
                break;
            case "02":
                i = R.mipmap.night_02;
                break;
            case "03":
                i = R.mipmap.night_03;
                break;
            case "04":
                i = R.mipmap.night_04;
                break;
            case "05":
                i = R.mipmap.night_05;
                break;
            case "06":
                i = R.mipmap.night_06;
                break;
            case "07":
                i = R.mipmap.night_07;
                break;
            case "08":
                i = R.mipmap.night_08;
                break;
            case "09":
                i = R.mipmap.night_09;
                break;
            case "10":
                i = R.mipmap.night_10;
                break;
            case "11":
                i = R.mipmap.night_11;
                break;
            case "12":
                i = R.mipmap.night_12;
                break;
            case "13":
                i = R.mipmap.night_13;
                break;
            case "14":
                i = R.mipmap.night_14;
                break;
            case "15":
                i = R.mipmap.night_15;
                break;
            case "16":
                i = R.mipmap.night_16;
                break;
            case "17":
                i = R.mipmap.night_17;
                break;
            case "18":
                i = R.mipmap.night_18;
                break;
            case "19":
                i = R.mipmap.night_19;
                break;
            case "20":
                i = R.mipmap.night_20;
                break;
            case "21":
                i = R.mipmap.night_21;
                break;
            case "22":
                i = R.mipmap.night_22;
                break;
            case "23":
                i = R.mipmap.night_23;
                break;
            case "24":
                i = R.mipmap.night_24;
                break;
            case "25":
                i = R.mipmap.night_25;
                break;
            case "26":
                i = R.mipmap.night_26;
                break;
            case "27":
                i = R.mipmap.night_27;
                break;
            case "28":
                i = R.mipmap.night_28;
                break;
            case "29":
                i = R.mipmap.night_29;
                break;
            case "30":
                i = R.mipmap.night_29;
                break;
            case "31":
                i = R.mipmap.night_29;
                break;
            case "53":
                i = R.mipmap.night_53;
                break;


        }
        return i;
    }

    @SuppressLint("MissingPermission")
    private void getLocation(Context context) {
        //1.获取位置管理器
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是网络定位
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS定位
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }

        //3.获取上次的位置，一般第一次运行，此值为null
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            showLocation(location);
        } else {
            // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(locationProvider, 1, 1, mListener);
        }
    }

    private void showLocation(Location location){
        String address = "纬度："+location.getLatitude()+"经度："+location.getLongitude();
        double latitude = location.getLatitude();//纬度
        double longitude = location.getLongitude();//经度
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        LatLonPoint latLng = new LatLonPoint(latitude,longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLng, 200,GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);


    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        // 如果位置发生变化，重新显示
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }
    };

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        city = regeocodeAddress.getCity();//城市
        String formatAddress = regeocodeAddress.getFormatAddress();
        if (city==null){
            Toast.makeText(Main7Activity.this,"无法获取当前城市",Toast.LENGTH_SHORT).show();
        }else {
            if (city.endsWith("市")){
                m7_et_city.setText(city.substring(0,city.length()-1));
            }else {
                m7_et_city.setText(city);
            }
            Map<String,Object> map=new HashMap<>();
            map.put("title","天气查询位置信息");
            map.put("message",formatAddress);
            map.put("time", new Date().getTime());
            String path=OkHttpUtil.URL_postPage_Function_Path;
            try {
                OkHttpUtil.postPage(Main7Activity.this, setting.getMainUrl() + path, JSONUtil.toJsonStr(map));
            } catch (IOException e) {
                Log.e(Tag + "[" + path + "]", e.toString());
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
