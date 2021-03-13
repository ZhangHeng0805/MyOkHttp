package com.zhangheng.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhangheng.myapplication.adapter.WeatherList_Adapter;
import com.zhangheng.myapplication.bean.weather.JsonRootBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class Main7Activity extends Activity implements View.OnClickListener {
    private EditText m7_et_city;
    private Button m7_btn_query;
    private ListView m7_list_future;
    private LinearLayout m7_ll_realtime,m7_ll_future;
    private ImageView m7_img_realtime_icon;
    private TextView m7_text_realtime_city,m7_text_realtime
            ,m7_text_info,m7_text_temperature,m7_text_direct
            ,m7_text_power,m7_text_humidity,m7_text_aqi,m7_text_future_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        m7_et_city=findViewById(R.id.m7_et_city);
        m7_btn_query=findViewById(R.id.m7_btn_query);
        m7_btn_query.setOnClickListener(this);

        m7_img_realtime_icon=findViewById(R.id.m7_img_realtime_icon);
        m7_text_realtime_city=findViewById(R.id.m7_text_realtime_city);
        m7_text_realtime=findViewById(R.id.m7_text_realtime);
        m7_text_info=findViewById(R.id.m7_text_info);
        m7_text_temperature=findViewById(R.id.m7_text_temperature);
        m7_text_direct=findViewById(R.id.m7_text_direct);
        m7_text_power=findViewById(R.id.m7_text_power);
        m7_text_humidity=findViewById(R.id.m7_text_humidity);
        m7_text_aqi=findViewById(R.id.m7_text_aqi);
        m7_text_future_city=findViewById(R.id.m7_text_future_city);
        m7_list_future=findViewById(R.id.m7_list_future);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.m7_btn_query:
                String city = m7_et_city.getText().toString();
                getRealWeather(city);
                break;
        }
    }

    private void getRealWeather(String city) {
        String url="http://apis.juhe.cn/simpleWeather/query";
        String key=getResources().getString(R.string.key_weather);
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
                        m7_text_realtime_city.setText("错误："+e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson=new Gson();
                        JsonRootBean jsonRootBean = gson.fromJson(response, JsonRootBean.class);
                        if (jsonRootBean.getError_code()==0) {
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
                            String temperature = jsonRootBean.getResult().getRealtime().getTemperature()+"℃";
                            m7_text_temperature.setText(temperature);
                            //风向设置
                            String direct =jsonRootBean.getResult().getRealtime().getDirect();
                            m7_text_direct.setText(direct);
                            //风力设置
                            String power = jsonRootBean.getResult().getRealtime().getPower();
                            m7_text_power.setText(power);
                            //湿度
                            String humidity = "湿度："+jsonRootBean.getResult().getRealtime().getHumidity()+"%";
                            m7_text_humidity.setText(humidity);
                            //空气质量设置
                            String aqi = "空气质量："+jsonRootBean.getResult().getRealtime().getAqi();
                            m7_text_aqi.setText(aqi);

                            //近期天气状态栏
                            int size = jsonRootBean.getResult().getFuture().size();
                            String future_text="《"+city+"》近"+size+"天的天气";
                            //近期天气情况
                            m7_text_future_city.setText(future_text);
                            m7_list_future.setAdapter(new WeatherList_Adapter(Main7Activity.this,jsonRootBean));
                        }else {
                            switch (jsonRootBean.getError_code()){
                                case 207301:
                                    Toast.makeText(Main7Activity.this, "错误的查询城市名", Toast.LENGTH_SHORT).show();
                                    break;
                                case 207302:
                                    Toast.makeText(Main7Activity.this, "查询不到该城市的相关信息", Toast.LENGTH_SHORT).show();
                                    break;
                                case 207303:
                                    Toast.makeText(Main7Activity.this, "网络错误，请重试", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(Main7Activity.this, "错误码："+jsonRootBean.getError_code(), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            m7_text_realtime_city.setText("错误码："+jsonRootBean.getError_code());
                        }
                    }
                });
    }


    public int getDayIcon(String wid){
        int i=R.mipmap.undefined;
        switch (wid){
            case "00":
                i=R.mipmap.day_00;
                break;
            case "01":
                i=R.mipmap.day_01;
                break;
            case "02":
                i=R.mipmap.day_02;
                break;
            case "03":
                i=R.mipmap.day_03;
                break;
            case "04":
                i=R.mipmap.day_04;
                break;
            case "05":
                i=R.mipmap.day_05;
                break;
            case "06":
                i=R.mipmap.day_06;
                break;
            case "07":
                i=R.mipmap.day_07;
                break;
            case "08":
                i=R.mipmap.day_08;
                break;
            case "09":
                i=R.mipmap.day_09;
                break;
            case "10":
                i=R.mipmap.day_10;
                break;
            case "11":
                i=R.mipmap.day_11;
                break;
            case "12":
                i=R.mipmap.day_12;
                break;
            case "13":
                i=R.mipmap.day_13;
                break;
            case "14":
                i=R.mipmap.day_14;
                break;
            case "15":
                i=R.mipmap.day_15;
                break;
            case "16":
                i=R.mipmap.day_16;
                break;
            case "17":
                i=R.mipmap.day_17;
                break;
            case "18":
                i=R.mipmap.day_18;
                break;
            case "19":
                i=R.mipmap.day_19;
                break;
            case "20":
                i=R.mipmap.day_20;
                break;
            case "21":
                i=R.mipmap.day_21;
                break;
            case "22":
                i=R.mipmap.day_22;
                break;
            case "23":
                i=R.mipmap.day_23;
                break;
            case "24":
                i=R.mipmap.day_24;
                break;
            case "25":
                i=R.mipmap.day_25;
                break;
            case "26":
                i=R.mipmap.day_26;
                break;
            case "27":
                i=R.mipmap.day_27;
                break;
            case "28":
                i=R.mipmap.day_28;
                break;
            case "29":
                i=R.mipmap.day_29;
                break;
            case "30":
                i=R.mipmap.day_29;
                break;
            case "31":
                i=R.mipmap.day_29;
                break;
            case "53":
                i=R.mipmap.day_53;
                break;



        }
        return i;
    }
    public int getNightIcon(String wid){
        int i=R.mipmap.undefined;
        switch (wid){
            case "00":
                i=R.mipmap.night_00;
                break;
            case "01":
                i=R.mipmap.night_01;
                break;
            case "02":
                i=R.mipmap.night_02;
                break;
            case "03":
                i=R.mipmap.night_03;
                break;
            case "04":
                i=R.mipmap.night_04;
                break;
            case "05":
                i=R.mipmap.night_05;
                break;
            case "06":
                i=R.mipmap.night_06;
                break;
            case "07":
                i=R.mipmap.night_07;
                break;
            case "08":
                i=R.mipmap.night_08;
                break;
            case "09":
                i=R.mipmap.night_09;
                break;
            case "10":
                i=R.mipmap.night_10;
                break;
            case "11":
                i=R.mipmap.night_11;
                break;
            case "12":
                i=R.mipmap.night_12;
                break;
            case "13":
                i=R.mipmap.night_13;
                break;
            case "14":
                i=R.mipmap.night_14;
                break;
            case "15":
                i=R.mipmap.night_15;
                break;
            case "16":
                i=R.mipmap.night_16;
                break;
            case "17":
                i=R.mipmap.night_17;
                break;
            case "18":
                i=R.mipmap.night_18;
                break;
            case "19":
                i=R.mipmap.night_19;
                break;
            case "20":
                i=R.mipmap.night_20;
                break;
            case "21":
                i=R.mipmap.night_21;
                break;
            case "22":
                i=R.mipmap.night_22;
                break;
            case "23":
                i=R.mipmap.night_23;
                break;
            case "24":
                i=R.mipmap.night_24;
                break;
            case "25":
                i=R.mipmap.night_25;
                break;
            case "26":
                i=R.mipmap.night_26;
                break;
            case "27":
                i=R.mipmap.night_27;
                break;
            case "28":
                i=R.mipmap.night_28;
                break;
            case "29":
                i=R.mipmap.night_29;
                break;
            case "30":
                i=R.mipmap.night_29;
                break;
            case "31":
                i=R.mipmap.night_29;
                break;
            case "53":
                i=R.mipmap.night_53;
                break;



        }
        return i;
    }
}
