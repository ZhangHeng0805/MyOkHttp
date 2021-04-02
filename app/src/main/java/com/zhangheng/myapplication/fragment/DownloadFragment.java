package com.zhangheng.myapplication.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.zhangheng.myapplication.base.BaseFragment;

public class DownloadFragment extends BaseFragment {

    private TextView textView;
    private static final String TAG= DownloadFragment.class.getSimpleName();

    @Override
    protected View initView() {
        Log.e(TAG,"下载框架Fragment页面被初始化了");
        textView=new TextView(mContext);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return textView;
    }

    @Override
    protected void inttData() {
        Log.e(TAG,"下载框架Fragment数据被初始化了");
        super.inttData();
        textView.setText("下载页面框架");
    }
}
