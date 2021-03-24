package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.StreetNumber;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangheng.myapplication.util.TimeUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class Main12Activity extends AppCompatActivity implements View.OnClickListener, GeocodeSearch.OnGeocodeSearchListener {

    private GeocodeSearch geocodeSearch;
    private MapView mapView;
    private AMap aMap = null;
    private UiSettings mUiSettings;//定义一个UiSettings对象
    private RadioGroup m12_rg_mapstyle,m12_rg_locationtype;
    private CheckBox m12_cb_lukuang;
    private LinearLayout m12_LL_mapstyle,m12_LL_locationtype,m12_LL_message;
    private Button m12_btn_mapstyle,m12_btn_downloadmap,m12_btn_locationtype,m12_btn_refresh;
    private TextView m12_tv_location;
    private LatLonPoint latLng;
    private boolean f=true;
    private int screenWidth,screenHeight;
    private static final int PERMISSON_REQUESTCODE = 0;
     // 判断是否需要检测，防止不停的弹框
    private boolean isNeedCheck = true;
    private String username=null;
    private  double latitude=0,longitude=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main12);

        m12_rg_mapstyle=findViewById(R.id.m12_rg_mapstyle);
        m12_cb_lukuang=findViewById(R.id.m12_cb_lukuang);
        m12_LL_mapstyle=findViewById(R.id.m12_LL_mapstyle);
        m12_btn_mapstyle=findViewById(R.id.m12_btn_mapstyle);
        m12_btn_downloadmap=findViewById(R.id.m12_btn_downloadmap);
        m12_btn_downloadmap.setOnClickListener(this);
        m12_btn_mapstyle.setOnClickListener(this);
        m12_LL_locationtype=findViewById(R.id.m12_LL_locationtype);
        m12_btn_locationtype=findViewById(R.id.m12_btn_locationtype);
        m12_btn_locationtype.setOnClickListener(this);
        m12_rg_locationtype=findViewById(R.id.m12_rg_locationtype);
        m12_tv_location=findViewById(R.id.m12_tv_location);
        m12_LL_message=findViewById(R.id.m12_LL_message);
        m12_btn_refresh=findViewById(R.id.m12_btn_refresh);
        m12_btn_refresh.setOnClickListener(this);

        Intent intent=getIntent();
        username = intent.getStringExtra("name");

        checkPermission();

        screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽
        screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高
        //取控件当前的布局参数
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) m12_tv_location.getLayoutParams();
        //设置宽度值
        params.width= (int) (screenWidth*0.7);
        m12_tv_location.setLayoutParams(params);

        mapView=findViewById(R.id.m12_map);
        mapView.onCreate(savedInstanceState);

        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                latitude=location.getLatitude();//经度  
                longitude=location.getLongitude();//纬度  
                double altitude=location.getAltitude();//海拔 
                latLng = new LatLonPoint(latitude,longitude);
                m12_tv_location.setText(
                        "经度:"+latitude
                                + "\t纬度:"+longitude
                        +"\t海拔:"+altitude
                );
                RegeocodeQuery query = new RegeocodeQuery(latLng, 200,GeocodeSearch.AMAP);
                geocodeSearch.getFromLocationAsyn(query);

            }
        });


        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setCompassEnabled(true);//指南针
        mUiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);//设置logo位置


        aMap.setMapType(AMap.MAP_TYPE_NORMAL);//设置地图样式为普通
        m12_rg_mapstyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.m12_rb_01:
                        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                        m12_LL_mapstyle.setBackgroundColor(getResources().getColor(R.color.black_50));
                        break;
                    case R.id.m12_rb_02:
                        aMap.setMapType(AMap.MAP_TYPE_NIGHT);
                        m12_LL_mapstyle.setBackgroundColor(getResources().getColor(R.color.white_50));
                        break;
                    case R.id.m12_rb_03:
                        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                        m12_LL_mapstyle.setBackgroundColor(getResources().getColor(R.color.white_50));
                        break;
                    case R.id.m12_rb_04:
                        aMap.setMapType(AMap.MAP_TYPE_NAVI);
                        m12_LL_mapstyle.setBackgroundColor(getResources().getColor(R.color.black_50));
                        break;
                }
            }
        });

        aMap.setTrafficEnabled(true);//显示实时路况图层，aMap是地图控制器对象。
        m12_cb_lukuang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    aMap.setTrafficEnabled(true);//显示实时路况图层，aMap是地图控制器对象。
                }else {
                    aMap.setTrafficEnabled(false);//显示实时路况图层，aMap是地图控制器对象。
                }
            }
        });

        Location(4);//设置定位模式
        m12_rg_locationtype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.m12_rb_locationtype_01:
                        Location(3);
                        closeView();
                        break;
                    case R.id.m12_rb_locationtype_02:
                        Location(5);
                        closeView();
                        break;
                    case R.id.m12_rb_locationtype_03:
                        Location(1);
                        closeView();
                        break;
                    case R.id.m12_rb_locationtype_04:
                        Location(4);
                        closeView();
                        break;

                }
            }
        });
        closeView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        refreshMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void Location(int i){
        MyLocationStyle myLocationStyle;
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）
        // 如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle = new MyLocationStyle();
        switch (i){
            case 1:
                //连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
                break;
            case 0:
                //定位一次，且将视角移动到地图中心点。
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
                break;
            case 2:
                //定位一次，且将视角移动到地图中心点。
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
                break;
            case 3:
                //连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);
                break;
            case 4:
                //连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
                break;
            case 5:
                //连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);
                break;
            case 6:
                //连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
                break;
        }
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    private void closeView(){
        m12_LL_mapstyle.setVisibility(View.GONE);
        m12_btn_mapstyle.setTextColor(getColor(R.color.black));
        m12_btn_mapstyle.setText("地图样式");

        m12_LL_locationtype.setVisibility(View.GONE);
        m12_btn_locationtype.setTextColor(getColor(R.color.black));
        m12_btn_locationtype.setText("定位视角");

        if(username!=null){
            m12_btn_refresh.setVisibility(View.VISIBLE);
        }else {
            m12_btn_refresh.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.m12_btn_downloadmap:
                //在Activity页面调用startActvity启动离线地图组件
                startActivity(new Intent(this.getApplicationContext(),
                        com.amap.api.maps.offlinemap.OfflineMapActivity.class));
                break;
            case R.id.m12_btn_mapstyle:
                if (m12_LL_mapstyle.getVisibility()==View.GONE){
                    m12_btn_mapstyle.setText("关闭样式");
                    m12_btn_mapstyle.setTextColor(getColor(R.color.colorAccent));
                    m12_LL_mapstyle.setVisibility(View.VISIBLE);
                }else {

                    closeView();
                }
                break;
            case R.id.m12_btn_locationtype:
                if (m12_LL_locationtype.getVisibility()==View.GONE){
                    m12_btn_locationtype.setText("关闭方式");
                    m12_btn_locationtype.setTextColor(getColor(R.color.colorAccent));
                    m12_LL_locationtype.setVisibility(View.VISIBLE);
                    m12_LL_locationtype.setBackgroundColor(getColor(R.color.white));
                }else {
                    closeView();
                }
                break;
            case R.id.m12_btn_refresh:
                refreshMap();
                break;

        }
    }
    private void refreshMap(){
        com.zhangheng.myapplication.Object.Location location1=new com.zhangheng.myapplication.Object.Location();
        if (username!=null) {
            if (latitude != 0&&longitude!=0) {
                if (aMap == null) {
                    aMap = mapView.getMap();
                } else {
                    aMap.clear();
                }
                String time=TimeUtil.getSystemTime();
                    location1.setUsername(username);
                    location1.setLatitude(String.valueOf(latitude));
                    location1.setLongitude(String.valueOf(longitude));
                    location1.setTime(time);
                    location1.setState("t");//t公开；f隐藏
                    locationList(location1);
            }
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        String country = regeocodeAddress.getCountry();//国家
        String province = regeocodeAddress.getProvince();//省或直辖市
        String city = regeocodeAddress.getCity();//城市
        String township = regeocodeAddress.getTownship();//乡镇
        String neighborhood = regeocodeAddress.getNeighborhood();//社区名称。
        StreetNumber streetNumber = regeocodeAddress.getStreetNumber();//门牌信息。
        String formatAddress = regeocodeAddress.getFormatAddress();//格式化地址。
        m12_tv_location.setText(formatAddress/*+"\n"+m12_tv_location.getText().toString()*/);
    }
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    //检查申请权限
    private void checkPermission(){
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
                }else {

                }
            }
        }
    }
    //提示权限弹窗
    private void showMissingPermissionDialog() {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("当前应用缺少必要权限。\\n\\n请点击\\\"设置\\\"-\\\"权限\\\"-打开所需权限");

            // 拒绝, 退出应用
            builder.setNegativeButton("取消",
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

            builder.setPositiveButton("设置",
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
    //启动应用的设置
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
    private void locationList(com.zhangheng.myapplication.Object.Location location){
        List<com.zhangheng.myapplication.Object.Location> list=new ArrayList<>();
        String url=getResources().getString(R.string.location_url)+"location";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("username",location.getUsername())
                .addParams("latitude",location.getLatitude())
                .addParams("longitude",location.getLongitude())
                .addParams("time",location.getTime())
                .addParams("state",location.getState())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        f=false;
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response!=null){
                            Gson gson=new Gson();
                            List<com.zhangheng.myapplication.Object.Location> l = gson.fromJson(response, new TypeToken<List<com.zhangheng.myapplication.Object.Location>>() {
                            }.getType());
                            if (l.size()>0){
                                f=true;
                                for (com.zhangheng.myapplication.Object.Location loc:l) {
                                    LatLng latLng = new LatLng(Double.valueOf(loc.getLatitude()), Double.valueOf(loc.getLongitude()));
                                    MarkerOptions markerOption = new MarkerOptions();
                                    markerOption.position(latLng);
                                    markerOption.title(loc.getUsername());
                                    markerOption.snippet("更新于：\n"+loc.getTime());
                                    if (loc.getState()!=null) {
                                        switch (loc.getState()) {
                                            case "t":
                                                markerOption.visible(true);
                                                break;
                                            case "f":
                                                markerOption.visible(false);
                                                break;
                                            default:
                                                markerOption.visible(true);
                                                break;
                                        }
                                    }
//                                    Log.d("位置状态：",loc.getState());
                                    Marker marker = aMap.addMarker(markerOption);
                                    if (marker.isVisible()) {
                                    if (loc.getUsername().equals(username)) {
                                            marker.showInfoWindow();
                                        }
                                    }

                                }
                            }else {
                                f=false;
                            }
                        }else {
                            f=false;
                        }
                    }
                });


    }

}
