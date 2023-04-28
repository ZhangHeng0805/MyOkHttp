package com.zhangheng.myapplication.activity.setting_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

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
        dialog.setTitle("关于我们");
        String mainUrl = setting.getMainUrl();
        String github = context.getString(R.string.github);
        String gitee = context.getString(R.string.gitee);
        dialog.setMessage("-邮箱：zhangheng_0805@163.com\n\n" +
                "-个人网站："+ mainUrl +"\n\n"+
                "-GitHub主页："+ github +"\n\n"+
                "-Gitee主页："+ gitee +"\n"
        );
        dialog.setPositiveButton("邮箱联系", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //<a target="_blank" href="http://mail.qq.com/cgi-bin/qm_share?t=qm_mailme&email=wriqo6ylqqespezy_vL3grOz7KGtrw" style="text-decoration:none;"><img src="http://rescdn.qqmail.com/zh_CN/htmledition/images/function/qm_open/ico_mailme_12.png"/></a>
                Uri uri = Uri.parse("mailto:zhangheng_0805@163.com");
                String[] email = {"zhangheng.0805@qq.com"};
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                intent.putExtra(Intent.EXTRA_SUBJECT, "《" + context.getString(R.string.app_name) + "》android app 反馈"); // 主题
                intent.putExtra(Intent.EXTRA_TEXT, "反馈描述（可配图）："); // 正文
                context.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
            }
        });
        dialog.setNeutralButton("个人网站", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse(mainUrl);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(uri);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
//                    String componentName = intent.resolveActivity(context.getPackageManager()).getClassName();
                    context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
                } else {
                    Toast.makeText(context, "请下载浏览器", Toast.LENGTH_SHORT).show();
                }
//                context.startActivity(Intent.createChooser(intent,"请选择打开的浏览器"));
            }
        });
        dialog.setNegativeButton("GitHub主页", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse(github);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
        dialog.setNegativeButton("Gitee主页", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse(gitee);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
//        dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//            }
//        });
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
