package com.zhangheng.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.adapter.MovieGrid_Adapter;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.view.FixedGridView;
import com.zhangheng.myapplication.view.RoundImageView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class Main19_1Activity extends Activity {


    private String TAG = this.getClass().getSimpleName();
    private Context context = Main19_1Activity.this;
    private String[] types={"tv","movie","anime","show","documentary","high","filter"};
    private String[] titles={"热播新剧","热门电影","热播动漫","热播综艺","热播记录片","豆瓣Top250","好片推荐"};

    private Button m19_1_btn_search;
    private Banner m19_1_banner;
    private ListView m19_1_LV_movies;

    private List<String> list_banner_path = new ArrayList<>(),//放图片地址的集合
            list_banner_title = new ArrayList<>();//放标题的集合

    private List<Map<String,String>> list_map_tv=new ArrayList<>(),//热播新剧
            list_map_movie=new ArrayList<>(),//热门电影
            list_map_anime=new ArrayList<>(),//热播动漫
            list_map_show=new ArrayList<>(),//热播综艺
            list_map_documentary=new ArrayList<>(),//热播纪录片
            list_map_high=new ArrayList<>(),//豆瓣Top250
            list_map_filter=new ArrayList<>();//更多好片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main19_1);

        init();
    }

    private void init() {

        m19_1_btn_search = findViewById(R.id.m19_1_btn_search);
        m19_1_btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentSearch("");
            }
        });
        m19_1_banner = findViewById(R.id.m19_1_banner);
        m19_1_LV_movies=findViewById(R.id.m19_1_LV_movies);

        getData();

    }

    private static void getMovieInfo(Document doc,String type,List<Map<String,String>> data) throws IOException {
        Elements tv = doc.getElementById(type).select("div.movie-list-item");
        for (Element e : tv) {
            try {
                Map<String, String> map = new HashMap<>();
                String img_src = e.selectFirst("img.movie-post").attr("src");
                String title = e.selectFirst("div.movie-title").text();
                String rating = e.selectFirst("div.movie-rating").text();
                map.put("src", img_src);
                map.put("rating", rating);
                map.put("name", title);
                data.add(map);
            }catch (Exception e1){
                Log.e("解析错误",e1.getMessage());            }
        }
    }

    private void intentSearch(String name){
        Intent intent = new Intent(context, Main19Activity.class);
        intent.putExtra("name",name);
        startActivity(intent);
    }


    private void getData() {
        DialogUtil dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog("加载中。。。");
        OkHttpUtils.get()
                .url("https://www.cupfox.app/")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, e.getMessage());
                        DialogUtil.dialog(context, "请求错误", OkHttpMessageUtil.error(e));
                        dialogUtil.closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Document doc = Jsoup.parse(response);
                        getBanner(doc);


                        List<Map<String,List<Map<String,String>>>> list=new ArrayList<>();

                        for (int i = 0; i < types.length; i++) {
                            List<Map<String,String>> mapList=new ArrayList<>();
                            try {
                                getMovieInfo(doc,types[i],mapList);
                                Map<String,List<Map<String,String>>> map=new HashMap<>();
                                map.put(titles[i],mapList);
                                list.add(map);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        MoviesAdatper adatper = new MoviesAdatper(list, context);
                        m19_1_LV_movies.setAdapter(adatper);

                        dialogUtil.closeProgressDialog();
                    }
                });
    }

    /**
     * 获取banner图
     *
     * @param document
     */
    private void getBanner(Document document) {
        Elements select = document.selectFirst("div.swiper-wrapper").select("div.swiper-slide");
        for (Element e : select) {
            try {
                String src = e.selectFirst("img").attr("src");
                list_banner_path.add(src);
                String title = e.selectFirst("div.cupfox-title").text();
                list_banner_title.add(title);
            }catch (Exception e1){
                Log.e("解析错误",e1.getMessage());
            }
        }
        //设置内置样式，共有六种可以点入方法内逐一体验使用。
        m19_1_banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置图片加载器，图片加载器在下方
        m19_1_banner.setImageLoader(new MyLoader());
        //设置图片网址或地址的集合
        m19_1_banner.setImages(list_banner_path);
        //设置轮播的动画效果，内含多种特效，可点入方法内查找后内逐一体验
        m19_1_banner.setBannerAnimation(Transformer.Default);
        //设置轮播图的标题集合
        m19_1_banner.setBannerTitles(list_banner_title);
        //设置轮播间隔时间
        m19_1_banner.setDelayTime(3000);
        //设置是否为自动轮播，默认是“是”。
        m19_1_banner.isAutoPlay(true);
        //设置指示器的位置，小点点，左中右。
        m19_1_banner.setIndicatorGravity(BannerConfig.CENTER);
        //必须最后调用的方法，启动轮播图。
        m19_1_banner.start();
        //以上内容都可写成链式布局，这是轮播图的监听。
        m19_1_banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Log.i(TAG, "你点了第"+position+"张轮播图"+list_banner_title.get(position));
                intentSearch(list_banner_title.get(position));
            }
        });
    }

    //自定义的图片加载器
    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load((String) path).into(imageView);//这个是Glide框架加载网络图片，和下面的二选一即可
//            imageView.setImageResource((Integer) path);//加载本地文件
        }

        /**
         * 自定义圆角类
         *
         * @param context
         * @return
         */
        @Override
        public ImageView createImageView(Context context) {
            RoundImageView img = new RoundImageView(context);
            return img;

        }
    }

    private class MoviesAdatper extends BaseAdapter{

        private List<Map<String,List<Map<String,String>>>> data;

        private Context context;

        public MoviesAdatper(List<Map<String, List<Map<String, String>>>> data, Context context) {
            this.data = data;
            this.context = context;
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
            Holder holder;
            if (view==null){
                holder=new Holder();
                view=View.inflate(context,R.layout.item_list_movies1,null);
                holder.title= view.findViewById(R.id.item_tv_movies_title);
                holder.gridView= view.findViewById(R.id.item_GL_movies);
                view.setTag(holder);
            }else {
                holder= (Holder) view.getTag();
            }
            Map<String, List<Map<String, String>>> d = data.get(i);
            for (Map.Entry<String, List<Map<String, String>>> entry : d.entrySet()) {
                String title = entry.getKey();
                holder.title.setText(title);
                List<Map<String, String>> value = entry.getValue();
                MovieGrid_Adapter adatper = new MovieGrid_Adapter(value, context);
                holder.gridView.setAdapter(adatper);
                holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        intentSearch(value.get(i).get("name"));
                    }
                });
            }

            return view;
        }

        class Holder{
            private TextView title;
            private FixedGridView gridView;
        }
    }
}
