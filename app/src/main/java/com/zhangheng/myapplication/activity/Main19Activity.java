package com.zhangheng.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.SystemUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import okhttp3.Call;

/**
 * 影视爬虫
 */
public class Main19Activity extends AppCompatActivity {

    private EditText m19_et_search_name;
    private Button m19_btn_search;
    private TextView m19_tv_result;
    private ListView m19_lv_video;

    private List<Map<String, String>> video_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main19);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        SystemUtil.closeInput(Main19Activity.this);
    }

    private void initView() {
        m19_et_search_name = findViewById(R.id.m19_et_search_name);
        m19_btn_search = findViewById(R.id.m19_btn_search);
        m19_tv_result = findViewById(R.id.m19_tv_result);
        m19_tv_result.setVisibility(View.GONE);
        m19_lv_video = findViewById(R.id.m19_lv_video);

        viewLinstener();
    }

    private void viewLinstener() {
        //搜索按钮点击
        m19_btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.closeInput(Main19Activity.this);
                String name = m19_et_search_name.getText().toString();
                if (!StrUtil.isEmptyIfStr(name)) {
                    video_list.clear();
                    getVideo(name);
                }else {
                    DialogUtil.dialog(Main19Activity.this,"输入错误","搜索内容不能为空");
                }
            }
        });
    }

    private void setAdapter(List<Map<String, String>> list) {

        VideoAdatper adatper = new VideoAdatper(this, list);
        m19_lv_video.setAdapter(adatper);
        Toast.makeText(this, "已加载 " + list.size() + " 条数据", Toast.LENGTH_SHORT).show();

        m19_lv_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> map = list.get(i);
                Uri url = Uri.parse(map.get("url"));
                Intent intent = new Intent(Intent.ACTION_VIEW, url);
                startActivity(intent);
            }
        });
        m19_lv_video.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> map = list.get(i);
                boolean b = SystemUtil.copyStr(Main19Activity.this, map.get("url"));
                if (b){
                    Toast.makeText(Main19Activity.this, "播放地址已复制到剪切板中", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }

    class VideoAdatper extends BaseAdapter {

        private Context context;
        private List<Map<String, String>> list;

        public VideoAdatper(Context context, List<Map<String, String>> list) {
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
            Map<String, String> map = list.get(i);
            Holder holder;
            if (view == null) {
                holder = new Holder();
                view = View.inflate(context, R.layout.item_list_video, null);
                holder.item_video_name = view.findViewById(R.id.item_video_name);
                holder.item_video_website = view.findViewById(R.id.item_video_website);
                holder.item_video_tags = view.findViewById(R.id.item_video_tags);
                holder.item_video_icon = view.findViewById(R.id.item_video_icon);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            holder.item_video_name.setText("《"+map.get("name")+"》");
            holder.item_video_website.setText(map.get("web_name"));
            String tag_name = map.get("tag_name");
            if (!StrUtil.isEmptyIfStr(tag_name)) {
                holder.item_video_tags.setText(tag_name);
            }else {
                holder.item_video_tags.setVisibility(View.GONE);
            }
            Glide.with(Main19Activity.this).load(map.get("web_icon")).into(holder.item_video_icon);
            return view;
        }

        class Holder {
            TextView item_video_name, item_video_website, item_video_tags;
            ImageView item_video_icon;
        }
    }

    private void getVideo(String name) {
        DialogUtil dialogUtil = new DialogUtil(this);
        dialogUtil.createProgressDialog("搜索中。。。");
        String url = "https://cupfox.app/search?key=" + name;
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialogUtil.closeProgressDialog();
                        Log.e("影视爬虫请求错误", e.toString());
                        DialogUtil.dialog(Main19Activity.this, "搜索错误", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document doc = Jsoup.parse(response);
//                        m19_tv_result.setText(doc.title());
                        Elements result_list = doc.getElementsByClass("search-result-list").select("div").select("a");
                        for (Element e : result_list) {
                            //播放地址
                            String href = e.selectFirst("a").attr("href");
                            //播放站名
                            String web_name = e.selectFirst("span.website-name").text();
                            //播放站点图标
                            String web_icon = e.selectFirst("img.icon-website").attr("src");
                            //标签名
                            String tag_name = e.select("span.tag-name").text();
                            //影视名
                            String name = e.selectFirst("div.title").text();
                            Map<String, String> video = new HashMap<>();
                            video.put("name", name);
                            video.put("url", href);
                            video.put("web_name", web_name);
                            video.put("web_icon", web_icon);
                            video.put("tag_name", tag_name);
                            video_list.add(video);
                        }
                        setAdapter(video_list);
                        if (video_list.size()>0){
                            m19_tv_result.setVisibility(View.VISIBLE);
                        }else {
                            m19_tv_result.setVisibility(View.GONE);
                        }
                        dialogUtil.closeProgressDialog();
                    }
                });
    }
}
