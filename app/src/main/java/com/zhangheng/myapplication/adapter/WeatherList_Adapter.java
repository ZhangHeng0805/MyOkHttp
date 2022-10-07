package com.zhangheng.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhangheng.myapplication.activity.Main7Activity;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.bean.weather.Future;
import com.zhangheng.myapplication.bean.weather.JsonRootBean;

import java.text.DateFormat;

public class WeatherList_Adapter extends BaseAdapter {

    private final Context context;
    private final JsonRootBean jsonRootBean;
    private LayoutInflater mInflater;//布局装载器对象

    public WeatherList_Adapter(Context context, JsonRootBean jsonRootBean) {
        this.context=context;
        this.jsonRootBean=jsonRootBean;
    }

    @Override
    public int getCount() {
        return jsonRootBean.getResult().getFuture().size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view==null) {
            view = View.inflate(context,R.layout.item_weatherlist,null);
            viewHolder=new ViewHolder();
            viewHolder.future_date=view.findViewById(R.id.itme1_text_future_date);
            viewHolder.text_info=view.findViewById(R.id.itme1_text_info);
            viewHolder.text_temperature=view.findViewById(R.id.itme1_text_temperature);
            viewHolder.text_direct=view.findViewById(R.id.itme1_text_direct);
//            viewHolder.text_power=view.findViewById(R.id.itme1_text_power);
//            viewHolder.text_humidity=view.findViewById(R.id.itme1_text_humidity);
//            viewHolder.text_aqi=view.findViewById(R.id.itme1_text_aqi);
            viewHolder.img_day_icon=view.findViewById(R.id.itme1_img_day_icon);
            viewHolder.night_icon=view.findViewById(R.id.itme1_img_night_icon);
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        //根据位置绑定数据
        Future future=jsonRootBean.getResult().getFuture().get(i);
        //设置时间
        DateFormat df= DateFormat.getDateInstance();
        String date=df.format(future.getDate())+"天气";
        viewHolder.future_date.setText(date);
        //设置天气图标
        Main7Activity m=new Main7Activity();
        String day=future.getWid().getDay();
        int dayIcon = m.getDayIcon(day);
        viewHolder.img_day_icon.setImageResource(dayIcon);
        String night = future.getWid().getNight();
        int nightIcon = m.getNightIcon(night);
        viewHolder.night_icon.setImageResource(nightIcon);
        //设置天气
        String weather = future.getWeather();
        viewHolder.text_info.setText(weather);
        //设置温度
        String temperature = future.getTemperature();
        viewHolder.text_temperature.setText(temperature);
        //设置风向
        String direct = "风向："+future.getDirect();
        viewHolder.text_direct.setText(direct);
        //
        return view;
    }
    static class ViewHolder{
        TextView future_date,text_info,text_temperature
                ,text_direct,text_power,text_humidity,text_aqi;
        ImageView img_day_icon,night_icon;
    }
}

