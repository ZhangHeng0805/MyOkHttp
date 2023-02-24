package com.zhangheng.myapplication.activity.setting_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.util.AndroidImageUtil;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.LocalFileTool;

import cn.hutool.core.util.StrUtil;

public class Setting_WXOfficialAccount extends SettingActivity {
    public Setting_WXOfficialAccount(Context context) {
        super(context);
    }

    @Override
    public void OnItemClick() {
        WXOfficialAccount();
    }

    /**
     * 微信公众号
     */
    private void WXOfficialAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("扫码关注微信公众号");
        builder.setMessage("打开微信搜索“星曦向荣”微信公众号,或长按图片保存后使用微信扫一扫即可");
        ImageView img = new ImageView(context);
        img.setImageResource(R.drawable.wx_qrcode);
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Bitmap bitmap = AndroidImageUtil.drawableToBitmap(img.getDrawable());
                String name = "星曦向荣-微信公众号二维码.png";
                String filepath = LocalFileTool.BasePath + "/Pictures/" + context.getString(R.string.app_name) + "/";
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

        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        img_params.setMargins(10, 30, 10, 30);
        layout.addView(img, img_params);
        builder.setView(layout);
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
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

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
