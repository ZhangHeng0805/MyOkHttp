package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.MyLocationStyle;

public class Main12Activity extends AppCompatActivity implements View.OnClickListener {

    private MapView mapView;
    private AMap aMap = null;
    private UiSettings mUiSettings;//定义一个UiSettings对象
    private RadioGroup m12_rg_mapstyle,m12_rg_locationtype;
    private CheckBox m12_cb_lukuang;
    private LinearLayout m12_LL_mapstyle,m12_LL_locationtype;
    private Button m12_btn_mapstyle,m12_btn_downloadmap,m12_btn_locationtype;


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
                }
            }
        }

        mapView=findViewById(R.id.m12_map);
        mapView.onCreate(savedInstanceState);


        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mapView.getMap();
        }

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

        Location(4);
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
        m12_btn_locationtype.setText("定位方式");

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

        }
    }
}
