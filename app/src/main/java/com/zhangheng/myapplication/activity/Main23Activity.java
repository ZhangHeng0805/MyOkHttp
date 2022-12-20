package com.zhangheng.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class Main23Activity extends Activity {

    private final String TAg = getClass().getSimpleName();
    private final Context context = Main23Activity.this;

    private Spinner m23_sp_type;
    private TextView m23_tv_title, m23_tv_subtitle, m23_tv_update;
    private ListView m23_LV_content;

    private String[] types = {"itNews","douyinHot", "zhihuHot", "36Ke", "baiduRD", "bili", "baiduRY", "wbHot", "douban","itInfo",};
    private String[] titles = {"IT资讯最新","抖音热点", "知乎热榜", "36氪", "百度热点", "哔哩哔哩", "贴吧热议", "微博热搜", "豆瓣小组","IT资讯热榜",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main23);

        init();

    }

    private void init() {
        m23_sp_type = findViewById(R.id.m23_sp_type);
        m23_tv_title = findViewById(R.id.m23_tv_title);
        m23_tv_subtitle = findViewById(R.id.m23_tv_subtitle);
        m23_tv_update = findViewById(R.id.m23_tv_update);
        m23_LV_content = findViewById(R.id.m23_LV_content);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_list_text, titles);

        m23_sp_type.setAdapter(adapter);

        m23_sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getData(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getData(0);
    }

    private void getData(Integer index) {
        DialogUtil dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog("加载中。。。");
        OkHttpUtils.get()
                .url("https://api.vvhan.com/api/hotlist?type=" + types[index])
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAg, e.getMessage());
                        DialogUtil.dialog(context, "请求失败", OkHttpMessageUtil.error(e));
                        dialogUtil.closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject obj = JSONUtil.parseObj(response);
                            String title = obj.getStr("title",titles[index]);
                            m23_tv_title.setText(title);
                            String subtitle = obj.getStr("subtitle","");
                            m23_tv_subtitle.setText("——"+subtitle);
                            String update_time = obj.getStr("update_time", "");
                            m23_tv_update.setText("更新于:"+update_time);

                            JSONArray data = obj.getJSONArray("data");
                            List<Map<String,String>> list=new ArrayList<>();
                            for (Object datum : data) {
                                JSONObject o = JSONUtil.parseObj(datum);
                                String i = o.getStr("index", "");
                                String tit = o.getStr("title","");
                                String pic = o.getStr("pic","");
                                String desc = o.getStr("desc","");
                                String hot = o.getStr("hot","");
                                String url = o.getStr("mobilUrl","https://m.baidu.com/s?wd="+tit);
                                Map<String,String> map=new HashMap<>();
                                map.put("index",i);
                                map.put("title",tit);
                                map.put("pic",pic);
                                map.put("desc",desc);
                                map.put("hot",hot);
                                map.put("url",url);
                                list.add(map);
                            }

                            HospotAdatper adatper = new HospotAdatper(context, list);

                            m23_LV_content.setAdapter(adatper);

                            m23_LV_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Map<String, String> map = list.get(i);
                                    Uri url = Uri.parse(map.get("url"));
                                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                                    startActivity(intent);
                                }
                            });


                        } catch (Exception e) {
                            Log.e(TAg, e.getMessage());
                            DialogUtil.dialog(context, "请求错误", OkHttpMessageUtil.error(e));
                        } finally {
                            dialogUtil.closeProgressDialog();
                        }
                    }
                });
    }

    private class HospotAdatper extends BaseAdapter{

        private Context context;
        private List<Map<String,String>> list;

        public HospotAdatper(Context context, List<Map<String, String>> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
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
            Holder holder;
            if (view==null){
                holder=new Holder();
                view=View.inflate(context,R.layout.item_list_hotspot,null);
                holder.img= view.findViewById(R.id.item_hotspot_iV_img);
                holder.title= view.findViewById(R.id.item_hotspot_tv_title);
                holder.hot= view.findViewById(R.id.item_hotspot_tv_hot);
                holder.des= view.findViewById(R.id.item_hotspot_tv_desc);
                view.setTag(holder);
            }else {
                holder= (Holder) view.getTag();
            }

            Map<String, String> map = list.get(i);

            String index = StrUtil.isEmpty(map.get("index")) ? (i + 1) + "" : map.get("index");
            String title = map.get("title");
            if (StrUtil.isEmpty(title)){
                holder.title.setVisibility(View.GONE);
            }else {
                holder.title.setText(index+"、"+title);
            }
            String pic = map.get("pic");
            if (StrUtil.isEmpty(pic)){
                holder.img.setVisibility(View.GONE);
            }else {
                Glide.with(context).load(pic).into(holder.img);
            }
            String desc = map.get("desc");
            if (StrUtil.isEmpty(desc)){
                holder.des.setVisibility(View.GONE);
            }else {
                holder.des.setText(desc);
            }
            String hot = map.get("hot");
            if (StrUtil.isEmpty(hot)){
                holder.hot.setVisibility(View.GONE);
            }else {
                holder.hot.setText(hot);
            }
            return view;
        }
        class Holder{
            private ImageView img;
            private TextView title,hot,des;
        }
    }

}
