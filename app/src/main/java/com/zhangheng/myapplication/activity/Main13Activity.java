package com.zhangheng.myapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;
import com.zhangheng.myapplication.permissions.ReadAndWrite;
import com.zhangheng.myapplication.util.AndroidImageUtil;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.LocalFileTool;
import com.zhangheng.myapplication.view.MyPaintView;
import com.zhangheng.util.TimeUtil;

import java.util.Date;

public class Main13Activity extends Activity {

    private Button clearBtn, btn_paintcolor, btn_paintsize;
    private Button red, pink, orange, yellow, green, cyan, skyblue, blue, mediumpurple, purple, black, white;
    private MyPaintView paintView;
    private LinearLayout setting, layout_seebar;
    private HorizontalScrollView colors;
    private SeekBar seekBar;
    private TextView tit_progress;
    private ImageView m13_iv_help, m13_iv_close, m13_iv_save;
    private int i;
    private boolean flag_close = false;
    private boolean flag_start = false;
    private final Context context = Main13Activity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main13);
        paintView = findViewById(R.id.view_paint);
        clearBtn = findViewById(R.id.btn_clear);
        btn_paintsize = findViewById(R.id.btn_paintsize);
        btn_paintcolor = findViewById(R.id.btn_paintcolor);
        setting = findViewById(R.id.setting);
        colors = findViewById(R.id.color);
        red = findViewById(R.id.btn_red);
        pink = findViewById(R.id.btn_pink);
        orange = findViewById(R.id.btn_orange);
        yellow = findViewById(R.id.btn_yellow);
        green = findViewById(R.id.btn_green);
        cyan = findViewById(R.id.btn_cyan);
        skyblue = findViewById(R.id.btn_skyblue);
        blue = findViewById(R.id.btn_blue);
        mediumpurple = findViewById(R.id.btn_mediumpurple);
        purple = findViewById(R.id.btn_purple);
        black = findViewById(R.id.btn_black);
        white = findViewById(R.id.btn_white);
        seekBar = findViewById(R.id.seekbar);
        layout_seebar = findViewById(R.id.layout_seekbar);
        tit_progress = findViewById(R.id.tit_progress);
        m13_iv_help = findViewById(R.id.m13_iv_help);
        m13_iv_close = findViewById(R.id.m13_iv_close);
        m13_iv_save = findViewById(R.id.m13_iv_save);

        setOClickListener();
        DeFault();

        m13_iv_close.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                fullscreen();
                return true;
            }
        });
        paintView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //fullscreen();
                        break;
                    case MotionEvent.ACTION_UP:
                        //DeFault();
                        flag_start=true;
                }
                return false;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tit_progress.setText("画笔大小：" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                paintView.setMyPaintSize(progress);
                //Toast.makeText(PaintActivity.this,"画笔大小："+progress,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setOClickListener() {
        OnClick onClick = new OnClick();
        clearBtn.setOnClickListener(onClick);
        btn_paintcolor.setOnClickListener(onClick);
        btn_paintsize.setOnClickListener(onClick);
        red.setOnClickListener(onClick);
        pink.setOnClickListener(onClick);
        orange.setOnClickListener(onClick);
        yellow.setOnClickListener(onClick);
        green.setOnClickListener(onClick);
        cyan.setOnClickListener(onClick);
        skyblue.setOnClickListener(onClick);
        blue.setOnClickListener(onClick);
        mediumpurple.setOnClickListener(onClick);
        purple.setOnClickListener(onClick);
        black.setOnClickListener(onClick);
        white.setOnClickListener(onClick);
        tit_progress.setOnClickListener(onClick);
        m13_iv_help.setOnClickListener(onClick);
        m13_iv_close.setOnClickListener(onClick);
        m13_iv_save.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.m13_iv_close:
                    flag_close = !flag_close;
                    if (flag_close) {
                        setting.setVisibility(View.GONE);
                    } else {
                        setting.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.m13_iv_help:
                    StringBuilder msg = new StringBuilder();
                    msg.append("<p>* 左上角的<b>(×)</b>图标:<b>单击</b>可以关闭底部工具栏,<b>长按</b>可以全屏</p>");
                    msg.append("<p>* <b>全屏切换</b>：点击返回即可在默认和全屏之间切换");
                    msg.append("<p>* <b>退出</b>：快速连续点击两次返回即可退出</p>");
                    msg.append("<p>* <b>保存</b>：单击顶部中间图标<b>(√)</b></p>");
                    DialogUtil.dialog(context, "使用说明", msg.toString(), true);
                    break;
                case R.id.m13_iv_save:
                    saveImg();
                    break;
                case R.id.btn_clear:
                    paintView.clear();
                    flag_start=false;
                    break;
                case R.id.btn_paintcolor:
                    btn_paintcolor.setVisibility(View.GONE);
                    btn_paintsize.setVisibility(View.GONE);
                    layout_seebar.setVisibility(View.GONE);
                    clearBtn.setVisibility(View.GONE);
                    colors.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_red:
                    paintView.setMyPaintColor("red");
                    btn_paintcolor.setTextColor(getColor(R.color.red));
                    Toast.makeText(context, "红色", Toast.LENGTH_SHORT).show();
                    DeFault();
                    break;
                case R.id.btn_pink:
                    paintView.setMyPaintColor("pink");
                    btn_paintcolor.setTextColor(getColor(R.color.pink));
                    Toast.makeText(context, "粉色", Toast.LENGTH_SHORT).show();
                    DeFault();
                    break;
                case R.id.btn_orange:
                    paintView.setMyPaintColor("orange");
                    btn_paintcolor.setTextColor(getColor(R.color.orange));
                    Toast.makeText(context, "橙色", Toast.LENGTH_SHORT).show();
                    DeFault();
                    break;
                case R.id.btn_yellow:
                    paintView.setMyPaintColor("yellow");
                    btn_paintcolor.setTextColor(getColor(R.color.yellow));
                    Toast.makeText(context, "黄色", Toast.LENGTH_SHORT).show();
                    DeFault();
                    break;
                case R.id.btn_green:
                    paintView.setMyPaintColor("green");
                    btn_paintcolor.setTextColor(getColor(R.color.green));
                    Toast.makeText(context, "绿色", Toast.LENGTH_SHORT).show();
                    DeFault();
                    break;
                case R.id.btn_cyan:
                    paintView.setMyPaintColor("cyan");
                    btn_paintcolor.setTextColor(getColor(R.color.cyan));
                    Toast.makeText(context, "青色", Toast.LENGTH_SHORT).show();
                    DeFault();
                    break;
                case R.id.btn_skyblue:
                    paintView.setMyPaintColor("skyblue");
                    btn_paintcolor.setTextColor(getColor(R.color.skyblue));
                    Toast.makeText(context, "天蓝色", Toast.LENGTH_SHORT).show();
                    DeFault();
                    break;
                case R.id.btn_blue:
                    paintView.setMyPaintColor("blue");
                    btn_paintcolor.setTextColor(getColor(R.color.blue));
                    Toast.makeText(context, "蓝色", Toast.LENGTH_SHORT).show();
                    DeFault();
                    break;
                case R.id.btn_mediumpurple:
                    paintView.setMyPaintColor("mediumpurple");
                    btn_paintcolor.setTextColor(getColor(R.color.mediumpurple));
                    Toast.makeText(context, "浅紫色", Toast.LENGTH_SHORT).show();
                    DeFault();
                    break;
                case R.id.btn_purple:
                    paintView.setMyPaintColor("purple");
                    btn_paintcolor.setTextColor(getColor(R.color.purple));
                    Toast.makeText(context, "紫色", Toast.LENGTH_SHORT).show();
                    DeFault();
                    break;
                case R.id.btn_black:
                    paintView.setMyPaintColor("black");
                    btn_paintcolor.setTextColor(getColor(R.color.black));
                    Toast.makeText(context, "黑色", Toast.LENGTH_SHORT).show();
                    DeFault();
                    break;
                case R.id.btn_white:
                    paintView.setMyPaintColor("white");
                    btn_paintcolor.setTextColor(getColor(R.color.white));
                    Toast.makeText(context, "白色", Toast.LENGTH_SHORT).show();
                    DeFault();
                    break;
                case R.id.btn_paintsize:
                    btn_paintsize.setVisibility(View.GONE);
                    layout_seebar.setVisibility(View.VISIBLE);
                    break;
                case R.id.tit_progress:
                    Toast.makeText(context, tit_progress.getText().toString(), Toast.LENGTH_SHORT).show();
                    DeFault();
                    break;
            }
        }
    }

    private void saveImg() {
        if (flag_start) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("保存图片");
            builder.setMessage("是否将此刻画布作品保存为图片?");
            builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    boolean b = ReadAndWrite.RequestPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);//写入权限
                    if (b) {
                        String timeTips = TimeUtil.toTime(new Date(), "yyyyMMddHHmmssSSS");
                        String appName = getString(R.string.app_name);
                        String name = appName + "_" + PhoneSystem.getVersionCode(context) + "画板-" + timeTips + ".png";
                        String path = LocalFileTool.BasePath + "/Pictures/" + appName + "/" + appName + "画布" + "/";
                        Bitmap bitmap = AndroidImageUtil.createViewBitmap(paintView);
                        String s = AndroidImageUtil.saveImage(context, bitmap, path, name, Bitmap.CompressFormat.PNG);
                        if (s!=null) {
                            DialogUtil.dialog(context, "保存成功！", "保存路径：" + s.replace(LocalFileTool.BasePath, "内部存储"));
                        }else {
                            Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, "没有打开文件写入权限", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.create().show();
        }else {
            Toast.makeText(context,"画布为空",Toast.LENGTH_SHORT).show();
        }
    }

    private void DeFault() {//默认布局
        btn_paintcolor.setVisibility(View.VISIBLE);
        m13_iv_close.setVisibility(View.VISIBLE);
        m13_iv_help.setVisibility(View.VISIBLE);
        m13_iv_save.setVisibility(View.VISIBLE);
        btn_paintsize.setVisibility(View.VISIBLE);
        clearBtn.setVisibility(View.VISIBLE);
        setting.setVisibility(View.VISIBLE);
        colors.setVisibility(View.GONE);
        layout_seebar.setVisibility(View.GONE);
        i = 1;
    }

    private void fullscreen() {//全屏
        m13_iv_close.setVisibility(View.GONE);
        m13_iv_help.setVisibility(View.GONE);
        m13_iv_save.setVisibility(View.GONE);
        setting.setVisibility(View.GONE);
        i = 0;
    }
    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 1000) {
//                Toast.makeText(context, "再按一次退出！", Toast.LENGTH_LONG).show();
                mExitTime = System.currentTimeMillis();
                if (i == 0) {
                    DeFault();
                } else if (i == 1) {
                    fullscreen();
                }
            } else {
                if (flag_start){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("确定退出");
                    builder.setMessage("您的画板作品还未保存，确定退出");
                    builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.create().show();
                }else {
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
    }
}
