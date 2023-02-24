package com.zhangheng.myapplication.activity.setting_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.AndroidImageUtil;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.LocalFileTool;

import cn.hutool.core.util.StrUtil;

public class Setting_Donation extends SettingActivity {
    public Setting_Donation(Context context) {
        super(context);
    }

    @Override
    public void OnItemClick() {
        setDonation();
    }


    /**
     * 捐赠支持
     */
    private void setDonation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        AlertDialog dialog = builder.create();
        ImageView img = new ImageView(context);
        //原图
        Bitmap bitmap = AndroidImageUtil.drawableToBitmap(context.getDrawable(R.drawable.collection_code));

//        String f1 = LocalFileTool.getFileSizeString((long) AndroidImageUtil.bitmapToByte(bitmap).length);
        Bitmap zip = AndroidImageUtil.zoomBitmap(bitmap, 0.7f, 0.7f);
//        String f2 = LocalFileTool.getFileSizeString((long) AndroidImageUtil.bitmapToByte(zip).length);
        //旋转
        Bitmap rotate = AndroidImageUtil.rotate(zip, -90);
        //图片缩放
//        Bitmap zoom = AndroidImageUtil.zoomBitmap(bitmap, 0.7f, 0.7f);
        //水印文字
        Bitmap tip = AndroidImageUtil.creatStringBitmap(context, context.getString(R.string.app_name) + "捐赠二维码", 5, Color.BLUE, Color.WHITE);
        //添加水印
        Bitmap watermark = AndroidImageUtil.createWatermark(rotate, tip, AndroidImageUtil.RIGHT_BOTTOM, 10);
        img.setImageBitmap(watermark);

        LinearLayout layout = new LinearLayout(context);
        layout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        img_params.setMargins(10, 10, 10, 10);
        layout.addView(img, img_params);
        builder.setView(layout);

        builder.setTitle("感谢捐赠");
        builder.setMessage("您的捐赠是我前进的动力!\nThanks♪(･ω･)ﾉ\n可以先长按图片保存,然后打开APP扫码");
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Bitmap bitmap = AndroidImageUtil.drawableToBitmap(img.getDrawable());
                String appname = context.getString(R.string.app_name);
                String name = appname + "捐赠收款码.png";
                String filepath = LocalFileTool.BasePath + "/Pictures/" + appname + "/";
                try {
                    String p = AndroidImageUtil.saveImage(context, bitmap, filepath, name, Bitmap.CompressFormat.PNG);
                    if (!StrUtil.isEmpty(p)) {
                        DialogUtil.dialog(context, "保存成功！", "保存路径为：" + p.replace(LocalFileTool.BasePath, "内部存储"));
                    } else {
                        Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        builder.setPositiveButton("打开微信", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Intent lan = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(lan.getComponent());
                    context.startActivity(intent);
                } catch (Exception e) {
                    //若无法正常跳转，在此进行错误处理
                    Toast.makeText(context, "无法跳转到微信，请检查您是否安装了微信！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("打开支付宝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Intent lan = context.getPackageManager().getLaunchIntentForPackage("com.eg.android.AlipayGphone");
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(lan.getComponent());
                    context.startActivity(intent);
                } catch (Exception e) {
                    //若无法正常跳转，在此进行错误处理
                    Toast.makeText(context, "无法跳转到支付宝，请检查您是否安装了支付宝！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        if (builder != null) {
            builder.show();
        }
    }

    @Override
    protected String makePwd() throws Exception {
        return null;
    }

    @Override
    protected void checkingPwdCallBack() throws Exception {

    }
}
