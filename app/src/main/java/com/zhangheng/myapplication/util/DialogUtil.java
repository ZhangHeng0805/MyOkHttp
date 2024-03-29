package com.zhangheng.myapplication.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.zhangheng.myapplication.R;

public class DialogUtil {
    private final ProgressDialog progressDialog;

    public DialogUtil(Context context) {
        this.progressDialog = new ProgressDialog(context);
    }

    public static void dialog(Context context, String title, String message,boolean isHtml){
        AlertDialog.Builder d=new AlertDialog.Builder(context);
        TextView showText = new TextView(context);
        showText.setTextSize(16);
        showText.setTextColor(context.getColor(R.color.black));
        showText.setLineSpacing(0,1.2f);
        showText.setPadding(50,25,50,25);
        showText.setTextIsSelectable(true);
        if (isHtml) {
            showText.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT));
        }else {
            showText.setText(message);
        }
        d.setView(showText);
        d.setTitle(title);
        d.create().show();
    }
    public static void dialog(Context context, String title, String message){
        dialog(context,title,message,false);
    }
    public void createProgressDialog(){
        createProgressDialog("加载中。。。");
    }
    public void createProgressDialog(String title){
//        ProgressDialog progressDialog= new ProgressDialog(context);
        if (!progressDialog.isShowing()) {
            progressDialog.setMessage(title);
            progressDialog.setIndeterminate(true);// 是否形成一个加载动画  true表示不明确加载进度形成转圈动画  false 表示明确加载进度
            progressDialog.setCancelable(false);//点击返回键或者dialog四周是否关闭dialog  true表示可以关闭 false表示不可关闭
            progressDialog.show();
        }
    }
    public void closeProgressDialog(){
        if (progressDialog.isShowing()){
            progressDialog.cancel();
        }
    }
}
