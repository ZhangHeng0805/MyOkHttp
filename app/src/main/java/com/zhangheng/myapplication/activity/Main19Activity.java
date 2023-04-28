package com.zhangheng.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.SystemUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

/**
 * 影视爬虫
 */
public class Main19Activity extends AppCompatActivity {

    private EditText m19_et_search_name;
    private Button m19_btn_search;
    private TextView m19_tv_result, m19_tv_title_name, m19_tv_title_explain, m19_tv_title_introduce,m19_tv_title_rating;
    private ListView m19_lv_video;
    private ImageView m19_iv_img;
    private LinearLayout m19_LL_title;

    private List<Map<String, String>> video_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main19);

        initView();

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        if (!StrUtil.isEmpty(name)) {
            m19_et_search_name.setText(name);
            getVideo(name);
        } else {
        }
        m19_LL_title.setVisibility(View.GONE);
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
        m19_iv_img = findViewById(R.id.m19_iv_title_img);
        m19_tv_title_name = findViewById(R.id.m19_tv_title_name);
        m19_tv_title_explain = findViewById(R.id.m19_tv_title_explain);
        m19_tv_title_introduce = findViewById(R.id.m19_tv_title_introduce);
        m19_LL_title = findViewById(R.id.m19_LL_title);
        m19_tv_title_rating = findViewById(R.id.m19_tv_title_rating);

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
//                    Toast.makeText(Main19Activity.this,"点击可以打开浏览器播放，长按可以复制播放地址",Toast.LENGTH_SHORT).show();

                } else {
                    DialogUtil.dialog(Main19Activity.this, "输入错误", "搜索内容不能为空");
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
                if (b) {
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

        @RequiresApi(api = Build.VERSION_CODES.N)
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
            String name = map.get("name");
            if (name.indexOf("<em>") > -1) {
                name = name.replace("<em>", "<font color='#dd5325'>")
                        .replace("</em>", "</font>");
            }
            holder.item_video_name.setText(Html.fromHtml(name, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL));
            holder.item_video_website.setText(map.get("web_name"));
            String tag_name = map.get("tag_name");
            if (!StrUtil.isEmptyIfStr(tag_name)) {
                holder.item_video_tags.setText(tag_name);
            } else {
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
        String url = getString(R.string.cupfox_url) + "s/" + name;
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialogUtil.closeProgressDialog();
                        Log.e("影视爬虫请求错误", e.toString());
                        DialogUtil.dialog(Main19Activity.this, "搜索错误", OkHttpMessageUtil.error(e));
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.d("影视请求", response);
                        try {
                            Document doc = Jsoup.parse(response);
                            m19_LL_title.setVisibility(View.VISIBLE);
                            String img_src = doc.select("div.poster").select("img").attr("src");
                            String title_name = doc.select(".movie-title-text").text();
                            String title_info = doc.select("div.title-block div.movie-info").text();
                            String title_summary = doc.select(".summary").text();
                            String title_rating = doc.select("div.rating div.movie-rating").text();
                            Glide.with(Main19Activity.this).load(img_src).into(m19_iv_img);
                            m19_tv_title_name.setText(title_name);
                            m19_tv_title_explain.setText(title_info);
                            m19_tv_title_introduce.setText(title_summary);
                            m19_tv_title_rating.setText(title_rating);

                            Elements script = doc.select("script");
                            String text = script.get(script.size() - 1).html();
                            String jsonStr = UnicodeUtil.toString(text);
                            JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
                            JSONArray jsonArray = jsonObject.getJSONObject("props")
                                    .getJSONObject("pageProps")
                                    .getJSONObject("resourceSearchResult")
                                    .getJSONArray("resources");
                            for (Object o : jsonArray) {
                                Map<String, String> map = new HashMap<>();
                                JSONObject obj = JSONUtil.parseObj(o);
                                String title = obj.getStr("text");
                                map.put("name", title);
                                String href = obj.getStr("url");
                                map.put("url", href);
                                String website = obj.getStr("website");
                                map.put("web_name", website);
                                String icon = obj.getStr("icon");
                                map.put("web_icon", icon);
                                JSONArray tags = obj.getJSONArray("tags");
                                StringBuilder tags_sb = new StringBuilder();
                                for (int i = 0; i < tags.size(); i++) {
                                    String str = tags.get(i).toString();
                                    if (i == tags.size() - 1) {
                                        tags_sb.append(str);
                                    } else {
                                        tags_sb.append(str + ",");
                                    }
                                }
                                map.put("tag_name", tags_sb.toString());
                                video_list.add(map);
                            }
                            if (video_list.size() > 0) {
                                setAdapter(video_list);
                                m19_tv_result.setVisibility(View.VISIBLE);
                            } else {
                                m19_tv_result.setVisibility(View.GONE);
                                DialogUtil.dialog(Main19Activity.this, "搜索失败", "暂无搜索结果");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(Main19Activity.this, "解析错误：" + e.toString(), Toast.LENGTH_SHORT).show();
                        } finally {
                            dialogUtil.closeProgressDialog();
                        }
                    }
                });
    }
}
