package com.zhangheng.myapplication.activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.SystemUtil;
import com.zhangheng.util.FormatUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class Main24Activity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private final Context context = Main24Activity.this;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private List<Map<String,String>> data=new ArrayList<>();

    private ListView m24_LV_list;
    private EditText m24_et_content;
    private Button m24_btn_send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main24);
        init();
    }

    private void init() {
        m24_LV_list = findViewById(R.id.m24_LV_list);
        m24_et_content = findViewById(R.id.m24_et_content);
        m24_btn_send = findViewById(R.id.m24_btn_send);

        m24_btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = m24_et_content.getText().toString();
                if (!StrUtil.isBlank(s)){
                    m24_et_content.setText("");
                    SystemUtil.closeInput(Main24Activity.this);
                    getData(s);
                }else {
                    DialogUtil.dialog(context,"输入错误","输入内容不能为空！");
                }
            }
        });

    }

    private void getData(String s) {
        DialogUtil dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog();
        OkHttpUtils.get()
                .url("https://xiaoapi.cn/API/lt_xiaoai.php")
                .addParams("msg",s)
                .addParams("type","json")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, e.toString());
                        dialogUtil.closeProgressDialog();
                        DialogUtil.dialog(context, "请求失败", OkHttpMessageUtil.error(e));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject data = JSONUtil.parseObj(response).getJSONObject("data");
                            String url = data.getStr("tts");
                            String txt = data.getStr("txt");
                            if (Validator.isUrl(url)){
                                Map<String,String> map1=new HashMap<>();
                                map1.put("flag","right");
                                map1.put("r_content",s);
                                Map<String,String> map2=new HashMap<>();
                                map2.put("flag","left");
                                map2.put("url",url);
                                map2.put("l_content",txt);
                                Main24Activity.this.data.add(map1);
                                Main24Activity.this.data.add(map2);
                                ChatAdapter adapter = new ChatAdapter(context, Main24Activity.this.data);
                                m24_LV_list.setAdapter(adapter);
                                getPlay(url);
                                m24_LV_list.setSelection(m24_LV_list.getBottom());
                            }else {
                                DialogUtil.dialog(context,"小爱不懂","请换一种说法，或者小爱暂不支持此功能！");
                            }
                        }catch (Exception e){
                            Log.e(TAG, e.toString());
                            DialogUtil.dialog(context, "请求错误", OkHttpMessageUtil.error(e));
                        }finally {
                            dialogUtil.closeProgressDialog();
                        }
                    }
                });
    }


    private void getPlay(String url) {
        DialogUtil dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog();
        try {
            Log.d(TAG + "音频地址：", url);
            if (FormatUtil.isWebUrl(url)) {
                mediaPlayer = new MediaPlayer();

                mediaPlayer.reset();
                Uri uri = Uri.parse(url);
                mediaPlayer.setDataSource(context, uri);// 设置文件源
                mediaPlayer.prepare();// 解析文件
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mp != null) {
                            mp.stop();
                            mp.release();
                        }
                    }
                });
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.dialog(context, "播放失败", OkHttpMessageUtil.error(e));
        } finally {
            dialogUtil.closeProgressDialog();
        }

    }

    private class ChatAdapter extends BaseAdapter {

        private Context context;
        private List<Map<String, String>> list;

        public ChatAdapter(Context context, List<Map<String, String>> list) {
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
            if (view == null) {
                holder = new Holder();
                view = View.inflate(context, R.layout.item_list_chat, null);
                holder.bofang = view.findViewById(R.id.item_chat_left_bofng);
                holder.r_content = view.findViewById(R.id.item_chat_right_content);
                holder.l_content = view.findViewById(R.id.item_chat_left_content);
                holder.LL_right = view.findViewById(R.id.item_chat_LL_right);
                holder.LL_left = view.findViewById(R.id.item_chat_LL_left);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            Map<String, String> map = list.get(i);
            String flag = map.get("flag");
            if (flag.equals("left")) {
                holder.LL_right.setVisibility(View.GONE);
                holder.l_content.setText(map.get("l_content"));
                holder.bofang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = map.get("url");
//                        url = URLEncodeUtil.encode(url);
                        if (FormatUtil.isWebUrl(url)){
                            try {
                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                }
                            } catch (Exception e) {
                            }
                            getPlay(url);
                        }
                    }
                });
            } else {
                holder.LL_left.setVisibility(View.GONE);
                holder.r_content.setText(map.get("r_content"));
            }


            return view;
        }

        class Holder {
            private ImageView bofang;
            private TextView r_content,l_content;
            private LinearLayout LL_right, LL_left;
        }
    }
}
