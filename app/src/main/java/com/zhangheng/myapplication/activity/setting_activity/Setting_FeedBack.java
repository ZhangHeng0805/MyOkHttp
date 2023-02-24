package com.zhangheng.myapplication.activity.setting_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.zhangheng.myapplication.R;

public class Setting_FeedBack extends SettingActivity {
    public Setting_FeedBack(Context context) {
        super(context);
    }

    @Override
    public void OnItemClick() {
        setFeedback();
    }

    /**
     * 意见反馈
     */
    private void setFeedback() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("意见反馈");
        dialog.setMessage("您可以通过邮箱向[zhangheng_0805@163.com]发送反馈邮件");
        dialog.setPositiveButton("打开邮箱发送", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //<a target="_blank" href="http://mail.qq.com/cgi-bin/qm_share?t=qm_mailme&email=wriqo6ylqqespezy_vL3grOz7KGtrw" style="text-decoration:none;"><img src="http://rescdn.qqmail.com/zh_CN/htmledition/images/function/qm_open/ico_mailme_12.png"/></a>
                Uri uri = Uri.parse("mailto:zhangheng_0805@163.com");
                String[] email = {"zhangheng.0805@qq.com"};
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                intent.putExtra(Intent.EXTRA_SUBJECT, "《" + context.getString(R.string.app_name) + "》android app BUG反馈"); // 主题
                intent.putExtra(Intent.EXTRA_TEXT, "bug描述（可配图）："); // 正文
                context.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        if (dialog != null) {
            dialog.show();
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
