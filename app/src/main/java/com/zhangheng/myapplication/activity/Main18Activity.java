package com.zhangheng.myapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.permissions.ReadAndWrite;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.SystemUtil;
import com.zhangheng.myapplication.util.TimeUtil;
import com.zhangheng.myapplication.view.RefreshListView;
import com.zhangheng.util.MathUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.net.URLEncoder;
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
 * 音乐爬虫
 */
public class Main18Activity extends Activity {

    private EditText m18_et_search_name;
    private Button m18_btn_search, m18_btn_continue;
    private RadioGroup m18_RG_type;
    private TextView m18_tv_result, m18_tv_music_title, m18_tv_real_time, m18_tv_total_time;
    private ImageView m18_iv_music_btn, m18_iv_music_pic, m18_iv_music_last, m18_iv_music_next;
    private RefreshListView m18_lv_music_list;
    private LinearLayout m18_LL_console;
    private SeekBar m18_pro_progress;


    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private progressThread progressThread;

    private List<Map<String, Object>> music_list = new ArrayList<>();//歌曲列表
    private Integer page = 1;//结果页码
    private Integer index = 0;//播放歌曲索引
    private Integer checked_id;//单选框id
    private String music_name = "", music_type = "", music_url = null;
    private Map<String, Object> music = new HashMap<>();
    private Map<Integer, String> map_type = new HashMap<Integer, String>();//歌曲类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main18);
        init();
        listener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    private void init() {

        initTypeMap();

        m18_et_search_name = findViewById(R.id.m18_et_search_name);
        m18_btn_search = findViewById(R.id.m18_btn_search);
        m18_RG_type = findViewById(R.id.m18_RG_type);
        m18_tv_result = findViewById(R.id.m18_tv_result);
        m18_lv_music_list = findViewById(R.id.m18_lv_music_list);
        m18_btn_continue = findViewById(R.id.m18_btn_continue);
        m18_iv_music_btn = findViewById(R.id.m18_iv_music_btn);
        m18_tv_music_title = findViewById(R.id.m18_tv_music_title);
        m18_iv_music_pic = findViewById(R.id.m18_iv_music_pic);
        m18_iv_music_last = findViewById(R.id.m18_iv_music_last);
        m18_iv_music_next = findViewById(R.id.m18_iv_music_next);
        m18_LL_console = findViewById(R.id.m18_LL_console);
        m18_LL_console.setVisibility(View.GONE);
        m18_tv_real_time = findViewById(R.id.m18_tv_real_time);
        m18_tv_total_time = findViewById(R.id.m18_tv_total_time);
        m18_pro_progress = findViewById(R.id.m18_pro_progress);
        m18_pro_progress.setProgress(0);

    }

    private void initTypeMap() {
        map_type.put(R.id.m18_rb_wangyi, "netease");
        map_type.put(R.id.m18_rb_qq, "qq");
        map_type.put(R.id.m18_rb_qianqian, "baidu");
        map_type.put(R.id.m18_rb_kuwo, "kuwo");
    }

    private void listener() {
        //搜索按钮点击事件
        m18_btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil dialogUtil = new DialogUtil(Main18Activity.this);
                dialogUtil.createProgressDialog();
                String search_name = m18_et_search_name.getText().toString();
                if (!StrUtil.isEmptyIfStr(search_name)) {
                    checked_id = m18_RG_type.getCheckedRadioButtonId();
                    music_type = map_type.get(checked_id);
                    boolean b = ReadAndWrite.RequestPermissions(Main18Activity.this, Manifest.permission.INTERNET);
                    if (b) {
                        music_list.clear();
                        page = 1;
                        try {
                            getMusics(search_name, music_type, page);
                            page++;
                        } catch (Exception e) {
                            DialogUtil.dialog(Main18Activity.this, "搜索错误", e.toString());
                        }
                    }
                } else {
                    DialogUtil.dialog(Main18Activity.this, "输入错误", "请先输入音乐名称");
                }
                dialogUtil.closeProgressDialog();
            }
        });
        //继续加载按钮点击事件
        m18_btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoadMoreMusic();
            }
        });
        //list刷新事件
        m18_lv_music_list.setRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onLoadMoreMusic();
                m18_lv_music_list.onRefreshComplete();
            }

            @Override
            public void onLoadMore() {
                onLoadMoreMusic();
                m18_lv_music_list.onRefreshComplete();
            }
        });
        //播放暂停点击事件
        m18_iv_music_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    m18_iv_music_btn.setImageResource(R.drawable.zanting);
                } else {
                    mediaPlayer.start();
                    m18_iv_music_btn.setImageResource(R.drawable.bofang);
                }
            }
        });
        //上一曲点击事件
        m18_iv_music_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastSong();
            }
        });
        //下一曲点击事件
        m18_iv_music_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextSong();
            }
        });
        //播放器完成播放监听
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                progressThread.stopThread();
                m18_pro_progress.setProgress(0);
                m18_tv_real_time.setText("00:00");
                m18_tv_total_time.setText("00:00");
                nextSong();
            }
        });
        //进度条滑动监听
        m18_pro_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                m18_tv_real_time.setText(TimeUtil.format(progress));
            }
        });

    }

    private void onLoadMoreMusic() {
        String search_name = m18_et_search_name.getText().toString();
        if (!StrUtil.isEmptyIfStr(search_name)) {
            Integer checkedId = m18_RG_type.getCheckedRadioButtonId();
            if (search_name.equals(music_name) && checkedId.equals(checked_id)) {
                music_type = map_type.get(checkedId);
                try {
                    getMusics(search_name, music_type, page);
                    page++;
                    if (!m18_btn_continue.isCursorVisible()) {
                        m18_btn_continue.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    DialogUtil.dialog(Main18Activity.this, "搜索错误", e.toString());
                }
            } else {
                m18_btn_continue.setVisibility(View.GONE);
                DialogUtil.dialog(Main18Activity.this, "加载异常", "请重新搜索");
            }
        } else {
            DialogUtil.dialog(Main18Activity.this, "输入错误", "请先输入音乐名称");
        }

    }

    private void setAdapter(List<Map<String, Object>> maps) {
        MusicAdapter adapter = new MusicAdapter(maps, Main18Activity.this);
        m18_lv_music_list.setAdapter(adapter);
        Toast.makeText(this, "已加载 " + maps.size() + " 条数据", Toast.LENGTH_SHORT).show();
        //list每行点击事件
        m18_lv_music_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                index = i - 1;
                String url = music_list.get(index).get("url").toString();
                playMusci(index);
            }
        });
        //list每行长按事件
        m18_lv_music_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, Object> map = music_list.get(i - 1);
                String title = map.get("title").toString();
                new AlertDialog.Builder(Main18Activity.this)
                        .setMessage("是否打开浏览器下载歌曲《" + title + "》？")
                        .setTitle("下载音乐")
                        .setPositiveButton("去下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String url = map.get("url").toString();
                                String author = map.get("author").toString();
                                boolean b = SystemUtil.copyStr(Main18Activity.this, title + " - " + author);
                                if (b) {
                                    Toast.makeText(Main18Activity.this, "音乐信息已复制到剪切板中", Toast.LENGTH_SHORT).show();
                                }
                                Uri parse = Uri.parse(url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, parse);
                                startActivity(intent);
                            }
                        }).show();


                return true;
            }
        });
    }

    class MusicAdapter extends BaseAdapter {
        private List<Map<String, Object>> musics;
        private Context context;


        public MusicAdapter(List<Map<String, Object>> musics, Context context) {
            this.musics = musics;
            this.context = context;
        }

        @Override
        public int getCount() {
            return musics.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        View n = null;

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Holder holder;
            if (view == null) {
                holder = new Holder();
                view = View.inflate(context, R.layout.item_list_musics, null);
                holder.item_music_layout = view.findViewById(R.id.item_music_layout);
                holder.item_music_pic = view.findViewById(R.id.item_music_pic);
                holder.item_music_name = view.findViewById(R.id.item_music_name);
                holder.item_music_author = view.findViewById(R.id.item_music_author);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            Map<String, Object> music = this.musics.get(i);
            String pic = music.get("pic").toString();
            Glide.with(context).load(pic).into(holder.item_music_pic);
            String name = music.get("title").toString();
            holder.item_music_name.setText(name);
            String author = music.get("author").toString();
            holder.item_music_author.setText(author);
            String url = music.get("url").toString();
//            holder.item_music_layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int i1 = viewGroup.indexOfChild(view);
//                    index = i1 - 1;
//                    Log.d("index", String.valueOf(i1));
//                    Map<String, String> head = new HashMap<>();
//                    head.put("name", name);
//                    head.put("author", author);
//                    head.put("pic", pic);
//                    head.put("url", url);
//                    setPlayPath(url);
////                    sendSimpleNotify(name, author);
//                    n = view;
//                }
//            });
//            holder.item_music_layout.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//
//                    return true;
//                }
//            });

            return view;
        }

        class Holder {
            ImageView item_music_pic;
            TextView item_music_name, item_music_author;
            LinearLayout item_music_layout;
        }
    }

    /**
     * 设置音乐播放
     *
     * @param index
     */
    private void playMusci(Integer index) {
        Map<String, Object> map = music_list.get(index);
        music = map;
        String name = map.get("title").toString();
        String author = map.get("author").toString();
        String text = "《" + name + "》- " + author;
        DialogUtil dialogUtil = new DialogUtil(this);
        try {
            dialogUtil.createProgressDialog();
            // 设置类型
//            mediaPlayer.setAudioStreamType(AudioManager.);
            // 这里要reset一下啊 (当已经设置过音乐后，再调用此方法时，没有reset就会异常)
            mediaPlayer.reset();

            String url = music_list.get(index).get("url").toString();
            Uri uri = Uri.parse(url);

            mediaPlayer.setDataSource(this, uri);// 设置文件源
            mediaPlayer.prepare();// 解析文件
//            mediaPlayer.setVolume(100,100);
            //设置进度
            int duration = mediaPlayer.getDuration();
            m18_pro_progress.setMax(duration);
            m18_tv_total_time.setText(TimeUtil.format(duration));
            mediaPlayer.start();

            progressThread = new progressThread();
            progressThread.start();

            String pic = map.get("pic").toString();
            Glide.with(Main18Activity.this).load(pic).into(m18_iv_music_pic);
            m18_tv_music_title.setText(text);

            String lrc1 = music.get("lrc").toString();
            StringBuilder sb = new StringBuilder();
            if (!StrUtil.isEmptyIfStr(lrc1)) {
                String[] lrcs = lrc1.split("\\n");
                for (String lrc : lrcs) {
                    sb.append(lrc.substring(lrc.indexOf("]") + 1) + "\n");
                }
            }else {
                sb.append("暂无歌词，请欣赏！");
            }
            m18_tv_result.setText(sb.toString());

            music_url = url;
            m18_LL_console.setVisibility(View.VISIBLE);

            if (mediaPlayer.isPlaying()){
                m18_iv_music_btn.setImageResource(R.drawable.bofang);
            }else {
                m18_iv_music_btn.setImageResource(R.drawable.zanting);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(Main18Activity.this, "歌曲 " + text + " 播放失败,自动切换下一首", Toast.LENGTH_LONG).show();
            nextSong();
        } finally {
            dialogUtil.closeProgressDialog();
        }
    }

    class progressThread extends Thread {
        boolean flag = true;
        int total;
        int n=0;

        @Override
        public void run() {
            super.run();
            total = mediaPlayer.getDuration();
            while (flag) {
                if (mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    m18_pro_progress.setProgress(currentPosition); //实时获取播放音乐的位置并且设置进度条的位置
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Main18Activity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String format = TimeUtil.format(currentPosition);
                            m18_tv_real_time.setText(format);
                            double f= MathUtil.twoDecimalPlaces(currentPosition/total);
                            String[] lrcs = music.get("lrc").toString().split("\\n");
                            for (int i=0;i<lrcs.length;i++) {
                                if (lrcs[i].indexOf("["+format) >-1){
                                    n=i;
                                    break;
                                }
                            }
                            int scrollBarSize = m18_tv_result.getLineHeight()*n;
                            m18_tv_result.scrollTo(0, scrollBarSize);
                        }
                    });
                }
            }
        }
        private int getTextViewHeight(TextView view) {
            Layout layout = view.getLayout();
            int desired = layout.getLineTop(view.getLineCount());
            int padding = view.getCompoundPaddingTop() + view.getCompoundPaddingBottom();
            return desired + padding;
        }
        //下面的函数是外部调用种植线程的，因为现在是不提倡直接带哦用stop方法的
        public void stopThread() {
            this.flag = false;
        }
    }

    public void nextSong() {
        if (index < music_list.size()-1) {
            index++;
        } else {
            index = 0;
        }
        m18_iv_music_next.setClickable(false);
        playMusci(index);
        m18_iv_music_next.setClickable(true);
    }

    public void lastSong() {
        if (index > 0) {
            index--;
        } else {
            index = music_list.size() - 1;
        }
        m18_iv_music_last.setClickable(false);
        playMusci(index);
        m18_iv_music_last.setClickable(true);
    }

    public void getMusics(String name, String type, Integer page) throws Exception {
//        List<Map<String, Object>> list = music_list;
        DialogUtil dialogUtil = new DialogUtil(this);
        dialogUtil.createProgressDialog();
        String Url = "https://music.liuzhijin.cn/";
        Map<String, String> body = new HashMap<>();
        body.put("input", name);
        if (page == null || page < 1) {
            page = 1;
        }
        body.put("page", page.toString());
        body.put("filter", "name");
        //qq：QQ音乐；netease：网易云
        body.put("type", type);

        OkHttpUtils.post()
                .url(Url)
                .params(body)
                .addHeader("x-requested-with", "XMLHttpRequest")
                .addHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("origin", "https://music.liuzhijin.cn")
                .addHeader("referer", Url + "?name=" + URLEncoder.encode(name, "UTF-8") + "&type=" + type)
                .build().connTimeOut(30_000L).readTimeOut(30_000L).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dialogUtil.closeProgressDialog();
                Log.e("音乐爬虫错误", e.toString());
                DialogUtil.dialog(Main18Activity.this, "搜索错误", e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                String json = UnicodeUtil.toString(response);
//                System.out.println(JSONUtil.formatJsonStr(json));
                JSONObject jsonObject = JSONUtil.parseObj(json);
                Integer code = jsonObject.getInt("code");
                if (code.equals(200)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (Object d : data) {
                        String s = JSONUtil.toJsonStr(d);
                        JSONObject obj = JSONUtil.parseObj(s);
                        Map<String, Object> music = new HashMap<>();
                        //歌名
                        String title = obj.getStr("title");
                        music.put("title", StrUtil.isEmptyIfStr(title) ? "" : title);
                        //歌手
                        String author = obj.getStr("author");
                        music.put("author", StrUtil.isEmptyIfStr(author) ? "" : author);
                        //平台
                        String type = obj.getStr("type");
                        music.put("type", type != null ? type : "");
                        //封面
                        String pic = obj.getStr("pic");
                        music.put("pic", pic != null ? pic.replace("\\", "") : "");
                        //文件地址
                        String url = obj.getStr("url");
                        music.put("url", url != null ? url.replace("\\", "") : "");
                        //来源地址
                        String link = obj.getStr("link");
                        music.put("link", link != null ? link.replace("\\", "") : "");
                        //歌词
                        String lrc = obj.getStr("lrc");
                        music.put("lrc", lrc != null ? lrc.replace("\\", "") : "");
                        music_list.add(music);
                    }
                    setAdapter(music_list);
                    music_name = name;
                    m18_btn_continue.setVisibility(View.VISIBLE);
                    //自动播放
                    if (index == 0 && new Main18Activity().page <= 1) {
                        playMusci(0);
                    }
                } else {
                    Log.e("音乐爬虫错误", "第三方音乐[" + Url + "]API错误：" + jsonObject.getStr("error"));
                    DialogUtil.dialog(Main18Activity.this, "搜索失败", jsonObject.getStr("error"));
                }
                dialogUtil.closeProgressDialog();

            }
        });

    }


    private void sendSimpleNotify(String title, String message) {
        int count = 0;
        //从系统服务中获取通知管理器
        NotificationManager notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //调用后只有8.0以上执行
        createNotifyChannel(notifyMgr, this, "channel_id");
        //创建一个跳转到活动页面的意图
        Intent clickIntent = new Intent(this, Main18Activity.class);
        clickIntent.putExtra("flag", count);
        //创建一个用于页面跳转的延迟意图
        PendingIntent contentIntent = PendingIntent.getActivity(this, count, clickIntent
                , PendingIntent.FLAG_UPDATE_CURRENT);
        //创建一个通知消息的构造器
        Notification.Builder builder = new Notification.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //Android8.0开始必须给每个通知分配对应的渠道
            builder = new Notification.Builder(this, "channel_id");
        }
        builder.setContentIntent(contentIntent)//设置内容的点击意图
                .setAutoCancel(false)//设置是否允许自动清除
                .setSmallIcon(R.mipmap.ic_launcher)//设置状态栏里的小图标
                .setTicker("正在播放中")//设置状态栏里面的提示文本
                .setWhen(System.currentTimeMillis())//设置推送时间，格式为"小时：分钟"
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))//设置通知栏里面的大图标
                .setContentTitle(title)//设置通知栏里面的标题文本
                .setContentText(message);//设置通知栏里面的内容文本
        //根据消息构造器创建一个通知对象
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Notification notify = builder.build();
            //使用通知管理器推送通知，然后在手机的通知栏就会看到消息
            notifyMgr.notify(count, notify);
        }

    }

    /**
     * 创建通知渠道，Android8.0开始必须给每个通知分配对应的渠道
     *
     * @param notifyMgr
     * @param ctx
     * @param channelId
     */
    private void createNotifyChannel(NotificationManager notifyMgr, Context ctx, String channelId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //创建一个默认重要性的通知渠道
            NotificationChannel channel = new NotificationChannel(channelId, "Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            channel.setShowBadge(true);
            channel.canBypassDnd();//可否绕过请勿打扰模式
            channel.enableLights(true);//闪光
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);//锁屏显示通知
            channel.setLightColor(Color.RED);//指定闪光时的灯光颜色
            channel.canShowBadge();//桌面ICON是否可以显示角标
            channel.setShowBadge(true);
            channel.enableVibration(true);//是否可以震动
            channel.getGroup();//获取通知渠道组
            channel.setVibrationPattern(new long[]{100, 100, 200});//震动的模式
            channel.shouldShowLights();//是否会闪光
            notifyMgr.createNotificationChannel(channel);
        }
    }
}
