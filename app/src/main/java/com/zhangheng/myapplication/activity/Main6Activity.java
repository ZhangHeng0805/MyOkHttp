package com.zhangheng.myapplication.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.AndroidImageUtil;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.LocalFileTool;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.RandomrUtil;
import com.zhangheng.util.TimeUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class Main6Activity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private EditText editText;
    private ProgressBar progressBar;
    private TextView tv_pro, textView;
    private ImageView imageView, m6_iv_refresh,m6_iv_refresh_setting;
    private final Context context = Main6Activity.this;
    private final String Tag = getClass().getSimpleName();
    String[] urls = {
            "https://api.uomg.com/api/rand.img3?sort=胖次猫&format=json",//0
            "https://api.uomg.com/api/rand.avatar?sort=男&format=json",//1
            "https://api.uomg.com/api/rand.avatar?sort=女&format=json",//2
            "https://api.uomg.com/api/rand.avatar?sort=动漫男&format=json",//3
            "https://api.uomg.com/api/rand.avatar?sort=动漫女&format=json",//4
            "https://api.gmit.vip/Api/DmImg?format=json",//5
            "https://api.gmit.vip/Api/DmImgS?format=json",//6
            "https://api.gmit.vip/Api/MvImg?format=json",//7
            "https://api.gmit.vip/Api/FjImg?format=json",//8
            "https://api.gmit.vip/Api/McImg?format=json",//9
            "https://api.gmit.vip/Api/BingImg?format=json",//10
            "https://v.api.aa1.cn/api/api-gqsh/index.php?wpon=json",//11
            "https://v.api.aa1.cn/api/bz-5k-01/?type=json",//12
            "https://api.vvhan.com/api/tao?type=json",//13
            "https://api.vvhan.com/api/view?type=json",//14
            "https://api.vvhan.com/api/bing?type=json&rand=sj",//15
            "https://api.vvhan.com/api/mobil.girl?type=json",//16
    };
    String[] url_names={
            "淘宝买家秀图片-胖次猫",//0
            "随机头像-男",//1
            "随机头像-女",//2
            "随机头像-动漫男",//3
            "随机头像-动漫女",//4
            "随机二次元壁纸",//5
            "随机动漫壁纸",//6
            "随机美女壁纸",//7
            "随机风景壁纸",//8
            "随机MC酱壁纸",//9
            "必应每日一图",//10
            "高清手绘壁纸",//11
            "随机5K壁纸",//12
            "淘宝买家秀",//13
            "随机风景图",//14
            "随机Bing每日图",//15
            "随机cosplay美图",//16
    };
    private boolean[] checkedItems=new boolean[url_names.length];
    private Set<Integer> checkedIndexs=new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        button = findViewById(R.id.btn_img);
        editText = findViewById(R.id.et_img_url);
        progressBar = findViewById(R.id.progress_img);
        tv_pro = findViewById(R.id.text_progress_img);
        textView = findViewById(R.id.m6_txt_img);
        imageView = findViewById(R.id.iv_img);
        m6_iv_refresh = findViewById(R.id.m6_iv_refresh);
        m6_iv_refresh_setting = findViewById(R.id.m6_iv_refresh_setting);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Drawable drawable = imageView.getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = AndroidImageUtil.drawableToBitmap(drawable);
                    String appname = getString(R.string.app_name);
                    String path = LocalFileTool.BasePath + "/" + appname + "/download/image/";
                    String name = appname + "_download_" + TimeUtil.toTime(new Date(), "yyyyMMddHHmmss") + ".png";
                    String s = AndroidImageUtil.saveImage(context, bitmap, path, name, Bitmap.CompressFormat.PNG);
                    if (s != null) {
                        DialogUtil.dialog(context, "保存成功！", "保存路径：" + s.replace(LocalFileTool.BasePath, "内部存储"));
                    } else {
                        Toast.makeText(Main6Activity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
        m6_iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh_url();
            }
        });
        m6_iv_refresh_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshSetting();
            }
        });
        for (int i = 0; i < url_names.length; i++) {
            checkedItems[i]=true;
            checkedIndexs.add(i);
        }
        button.setOnClickListener(this);
        refresh_url();
    }

    private void refreshSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("随机图片种类设置");
//        String[] items = url_names;
        builder.setMultiChoiceItems(url_names, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                checkedItems[i]=b;
                if (b){
                    checkedIndexs.add(i);
                }else {
                    checkedIndexs.remove(i);
                }
                Log.d(Tag+url_names[i],b+"");
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void refresh_url() {
        DialogUtil dialogUtil = new DialogUtil(context);
        dialogUtil.createProgressDialog();

//        int r = RandomrUtil.createRandom(0, urls.length - 1);
        Integer[] arr=new Integer[checkedIndexs.size()];
        arr = checkedIndexs.toArray(arr);
        int r=arr[RandomrUtil.createRandom(0,arr.length-1)];
        OkHttpUtils.get()
                .url(urls[r])
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialogUtil.closeProgressDialog();
                        Log.e(Tag + "图片地址刷新", e.toString());
                        DialogUtil.dialog(context, "加载失败", OkHttpMessageUtil.error(e));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(Tag+"请求图片地址"+r,urls[r]);
                        try {
                            String url = null;
                            JSONObject obj = JSONUtil.parseObj(response);
                            if (r == 0 || r == 1||r==2||r==3||r==4) {
                                url = obj.getStr("imgurl");
                            }else if(r==5||r==6||r==7||r==8||r==9||r==10){
                                url= obj.getJSONObject("data").getStr("url");
                            }else if (r==11||r==12){
                                url= obj.getStr("img");
                                url=URLUtil.normalize(url);
                            }else if (r==13){
                                url= obj.getStr("pic");
                            }else if (r==14){
                                url= obj.getStr("imgurl");
                            }else if (r==15){
                                url=obj.getJSONObject("data").getStr("url");
                            }
                            if (StrUtil.isBlank(url)) {
                                url = getString(R.string.image_url);
                            }
                            editText.setText(url);
                        } catch (Exception e) {
                            Log.e(Tag + "图片地址刷新"+r, e.toString());
                            DialogUtil.dialog(context, "加载失败", OkHttpMessageUtil.error(e));
                        } finally {
                            dialogUtil.closeProgressDialog();
                        }
                    }
                });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_img:
                progressBar.setProgress(0);
                tv_pro.setText("0%");
                imageView.setImageBitmap(null);
                String url = editText.getText().toString();
                textView.setVisibility(View.GONE);
                if (!Validator.isUrl(url)) {
                    Toast.makeText(context, "请输入正确网址", Toast.LENGTH_SHORT).show();
                } else {
                    getImage(url);
                }
                break;
        }
    }

    public void getImage(String url) {
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("User-Agent","Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36 Edg/108.0.0.0")
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("错误：" + OkHttpMessageUtil.error(e));
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        imageView.setImageBitmap(response);
                        progressBar.setProgress((100));
                        textView.setVisibility(View.VISIBLE);
                        tv_pro.setText("100%");
                        textView.setText("长按图片即可保存");
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        progressBar.setProgress((int) (progress * 100));
                        tv_pro.setText((int) (progress * 100) + "%");
                        if (progress == 1) {
                            Toast.makeText(Main6Activity.this, "加载完毕！", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

}
