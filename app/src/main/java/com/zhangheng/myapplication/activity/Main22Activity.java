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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class Main22Activity extends Activity {

    private final String TAG=getClass().getSimpleName();
    private final Context context=Main22Activity.this;

    private TextView m22_tv_title,m22_tv_update;
    private ListView m22_LV_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main22);

        m22_tv_title=findViewById(R.id.m22_tv_title);
        m22_tv_update=findViewById(R.id.m22_tv_update);
        m22_LV_content=findViewById(R.id.m22_LV_content);

        getData();
    }

    private void getData() {
        DialogUtil dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog("加载中。。。");
        OkHttpUtils.get()
                .url("https://api.vvhan.com/api/hotlist?type=history")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG,e.getMessage());
                        DialogUtil.dialog(context,"请求失败", OkHttpMessageUtil.error(e));
                        dialogUtil.closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject obj = JSONUtil.parseObj(response);
                            String title = obj.getStr("title");
                            m22_tv_title.setText(title);
                            String update_time = obj.getStr("update_time");
                            m22_tv_update.setText("更新于:"+update_time);

                            JSONArray data = obj.getJSONArray("data");

                            List<Map<String,String>> list=new ArrayList<>();
                            for (Object datum : data) {
                                JSONObject o = JSONUtil.parseObj(datum);
                                String index = o.getStr("index");
                                String tit = o.getStr("title");
                                String desc = o.getStr("desc");
                                String url = o.getStr("mobilUrl");
                                Map<String,String> map=new HashMap<>();
                                map.put("index",index);
                                map.put("title",tit);
                                map.put("desc",desc);
                                map.put("url",url);
                                list.add(map);
                            }

                            NewsAdatper adatper = new NewsAdatper(context, list);
                            m22_LV_content.setAdapter(adatper);
                            m22_LV_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Map<String, String> map = list.get(i);
                                    String url = map.get("url");
                                    Uri parse = Uri.parse(url);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, parse);
                                    startActivity(intent);
                                }
                            });

                        }catch (Exception e){
                            Log.e(TAG,e.getMessage());
                            DialogUtil.dialog(context,"请求错误", OkHttpMessageUtil.error(e));
                        }finally {
                            dialogUtil.closeProgressDialog();
                        }
                    }
                });
    }

    private class NewsAdatper extends BaseAdapter{

        private Context context;
        private List<Map<String,String>> data;

        public NewsAdatper(Context context, List<Map<String, String>> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
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
            Holder holde;
            if (view==null){
                holde=new Holder();
                view=View.inflate(context,R.layout.item_text_news,null);
                holde.title=view.findViewById(R.id.item_news_txt_title);
                holde.content=view.findViewById(R.id.item_news_txt_content);
                view.setTag(holde);
            }else {
                holde= (Holder) view.getTag();
            }
            Map<String, String> map = data.get(i);
            String title=map.get("index")+"、"+map.get("title");
            holde.title.setText(title);
            holde.content.setText(map.get("desc"));

            return view;
        }
        class Holder{
            private TextView title,content;
        }
    }


}
