package com.zhangheng.myapplication.activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.reptile.TextToSpeech;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.RandomrUtil;
import com.zhangheng.myapplication.util.SystemUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;

import java.io.IOException;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class Main14Activity extends AppCompatActivity {
    private final String Tag = this.getClass().getSimpleName();
    private final Context context = Main14Activity.this;

    private Spinner m14_sp_type;
    private TextView m14_tv_content;
    private Button m14_btn_copy, m14_btn_next;
    private ImageView m14_iv_bofang;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String[] title = {
            "毒鸡汤",//0
            "舔狗日记",//1
            "社会语录",//2
            "朋友圈一言",//3
            "每日一言",//4
            "情感一言",//5
            "神回复段子",//6
            "搞笑语录",//7
            "名人名言",//8
            "网易云热评",//9
            "《我在人间凑数的日子》",//10
            "骚话文案",//11
            "随机谜语",//12
            "英汉文案",//13
            "爱情文案",//14
            "早安语录",//15
            "口吐芬芳",//16
            "随机姓名",//17
            "顺口溜",//18
            "安慰文案",//19
    };
    private String[] urls = {
            "https://du.liuzhijin.cn/",//0
            "https://du.liuzhijin.cn/dog.php",//1
            "https://du.liuzhijin.cn/yulu.php",//2
            "https://v.api.aa1.cn/api/pyq/index.php?aa1=json",//3
            "https://v.api.aa1.cn/api/yiyan/index.php",//4
            "https://v.api.aa1.cn/api/api-wenan-qg/index.php?aa1=json",//5
            "https://v.api.aa1.cn/api/api-wenan-shenhuifu/index.php?aa1=json",//6
            "https://v.api.aa1.cn/api/api-wenan-gaoxiao/index.php?aa1=json",//7
            "https://v.api.aa1.cn/api/api-wenan-mingrenmingyan/index.php?aa1=json",//8
            "https://v.api.aa1.cn/api/api-wenan-wangyiyunreping/index.php?aa1=json",//9
            "https://v.api.aa1.cn/api/api-renjian/index.php?type=json",//10
            "https://v.api.aa1.cn/api/api-saohua/index.php?type=json",//11
            "https://v.api.aa1.cn/api/api-miyu/index.php",//12
            "https://v.api.aa1.cn/api/api-wenan-yingwen/index.php?type=json",//13
            "https://v.api.aa1.cn/api/api-wenan-aiqing/index.php?type=json",//14
            "https://v.api.aa1.cn/api/zaoanyulu/index.php",//15
            "https://v.api.aa1.cn/api/api-wenan-ktff/index.php?type=",//16
            "https://v.api.aa1.cn/api/api-xingming/index.php",//17
            "https://v.api.aa1.cn/api/api-wenan-shunkouliu/index.php?type=json",//18
            "https://v.api.aa1.cn/api/api-wenan-anwei/index.php?type=json",//19
    };

    /**
     * 解析返回结果
     *
     * @param response
     * @param index
     * @return
     */
    private String response(String response, Integer index) {
        String res = "";
        if (index.equals(0) || index.equals(1) || index.equals(2)) {
            res = Jsoup.parse(response).body().getElementById("text").text();
        } else if (index.equals(3)) {
            res = JSONUtil.parseObj(response).getStr("pyq", "");
        } else if (index.equals(4)) {
            res = Jsoup.parse(response).body().text();
        } else if (index.equals(5)) {
            res = JSONUtil.parseObj(JSONUtil.parseArray(response).get(0)).getStr("qinggan", "");
        } else if (index.equals(6)) {
            res = JSONUtil.parseObj(JSONUtil.parseArray(response).get(0)).getStr("shenhuifu", "").replace("<br>", "\n\n");
        } else if (index.equals(7)) {
            res = JSONUtil.parseObj(JSONUtil.parseArray(response).get(0)).getStr("gaoxiao", "");
        } else if (index.equals(8)) {
            res = JSONUtil.parseObj(JSONUtil.parseArray(response).get(0)).getStr("mingrenmingyan", "");
        } else if (index.equals(9)) {
            res = JSONUtil.parseObj(JSONUtil.parseArray(response).get(0)).getStr("wangyiyunreping", "");
        } else if (index.equals(10)) {
            res = JSONUtil.parseObj(response).getStr("renjian", "");
        } else if (index.equals(11)) {
            res = JSONUtil.parseObj(response).getStr("saohua", "");
        } else if (index.equals(12)) {
            JSONObject object = JSONUtil.parseObj(response);
            res = "谜题:" + object.getStr("mt") + "\n\n"
                    + "提示:" + object.getStr("ts") + "(" + object.getStr("lx") + ")\n\n"
                    + "谜底:" + object.getStr("md");
        } else if (index.equals(13) || index.equals(14) || index.equals(15) || index.equals(16)) {
            response = response.replace("\n", "");
            res = JSONUtil.parseObj(response).getStr("text", "");
        } else if (index.equals(17)) {
            res = JSONUtil.parseObj(response).getStr("xingming", "");
        } else if (index.equals(18)) {
            res = JSONUtil.parseObj(response).getStr("skl", "");
        } else if (index.equals(19)) {
            res = JSONUtil.parseObj(response).getStr("anwei", "");
        }
        return res;
    }

    private Integer index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main14);

        m14_btn_copy = findViewById(R.id.m14_btn_copy);
        m14_btn_next = findViewById(R.id.m14_btn_next);
        m14_tv_content = findViewById(R.id.m14_tv_content);
        m14_sp_type = findViewById(R.id.m14_sp_type);
        m14_iv_bofang = findViewById(R.id.m14_iv_bofang);

        init();
        getData(index);
    }

    private void init() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, R.layout.item_list_text, title);
        m14_sp_type.setAdapter(adapter);

        m14_sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                index = i;
                getData(index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        m14_btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                } catch (Exception e) {
                }

                getData(index);
            }
        });
        m14_btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence text = m14_tv_content.getText();
                if (!StrUtil.isEmpty(text)) {
                    boolean b = SystemUtil.copyStr(context, text.toString());
                    if (b) {
                        Toast.makeText(context, "文案已成功复制到剪切板中！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "文案为空，无法复制！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        m14_iv_bofang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = m14_tv_content.getText().toString();
                if (!StrUtil.isBlank(text)) {
                    boolean chinese = Validator.hasChinese(text);
                    String id;
                    if (chinese) {
                         id= RandomrUtil.createRandom(1, 19) + "";
                    }else {
                        id="20";
                    }
//                    getPlay(text,id);
                    new TextToSpeech(context).getPlay(text,id);
//                    String url="https://api.vvhan.com/api/song?txt="+text;
                } else {
                    m14_iv_bofang.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getPlay(String text,String id) {
        DialogUtil dialogUtil = new DialogUtil(context);
        String[] types={"baidu","youdao","xunfei"};
        dialogUtil.createProgressDialog();
        GetBuilder builder = OkHttpUtils.get()
                .url("https://xiaoapi.cn/API/zs_tts.php")
                .addParams("type", types[Convert.toInt(id)%types.length])//可为baidu、youdao、xunfei（即为百度、有道、讯飞）
//                .addParams("id", id)//语音类型1-20(20为英文专用语音)
                .addParams("msg", text);
        builder.build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(Tag, e.toString());
                        DialogUtil.dialog(context, "播放错误", OkHttpMessageUtil.error(e));
                        dialogUtil.closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            Log.d(Tag + "音频", "response:" + response);
                            if (JSONUtil.isTypeJSON(response)) {
                                JSONObject obj = JSONUtil.parseObj(response);
                                String url;
                                if (obj.getInt("code").equals(200)) {
                                    url = obj.getStr("tts");
                                }else {
                                    DialogUtil.dialog(context, "播放失败", obj.getStr("msg"));
                                    return;
                                }
                                Log.d(Tag + "音频地址["+obj.getStr("name")+"]：", url);
                                plagAudio(url);
                            }else {
                                DialogUtil.dialog(context, "播放失败", response);
                            }
                        } catch (Exception e) {
                            DialogUtil.dialog(context,"播放失败","对不起，此语音暂时无法播放");
                            e.printStackTrace();
                            m14_iv_bofang.setVisibility(View.GONE);
                        } finally {
                            dialogUtil.closeProgressDialog();
                        }
                    }
                });

    }

    private void plagAudio(String url) throws IOException {
        if (Validator.isUrl(url)) {
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
        }else {
            throw new IOException("音频地址错误！");
        }
    }

    private void getData(Integer index) {
        DialogUtil dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog();
        String url = urls[index];
        if (index.equals(16)) {
            url += RandomrUtil.createRandom(1, 5);
        }
        m14_tv_content.setText("");
        m14_btn_copy.setVisibility(View.GONE);
        m14_iv_bofang.setVisibility(View.GONE);
        Log.d(Tag + "文案地址", url);
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(Tag, e.toString());
                        dialogUtil.closeProgressDialog();
                        DialogUtil.dialog(context, "文案获取失败", OkHttpMessageUtil.error(e));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            String text = response(response, index);
                            if (!StrUtil.isEmpty(text)) {
                                m14_tv_content.setText(text);
                                m14_btn_copy.setVisibility(View.VISIBLE);
                                if (true) {
                                    m14_iv_bofang.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            Log.e(Tag, e.toString());
                            DialogUtil.dialog(context, "文案获取错误", OkHttpMessageUtil.error(e));
                        } finally {
                            dialogUtil.closeProgressDialog();
                        }
                    }
                });
    }


}
