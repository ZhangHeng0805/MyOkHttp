package com.zhangheng.myapplication.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.adapter.CommonFragmentListAdapter;
import com.zhangheng.myapplication.base.BaseFragment;

public class CommonFragment extends BaseFragment {

    private TextView textView;
    private ListView mListView;
    private static final String TAG=CommonFragment.class.getSimpleName();

    @Override
    protected View initView() {
        Log.e(TAG,"常用框架Fragment页面被初始化了");
        View view=View.inflate(mContext, R.layout.m15_fragment_common_listview,null);
        mListView=view.findViewById(R.id.m15_fragment_common_listview);
        textView=new TextView(mContext);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return view;
    }

    @Override
    protected void inttData() {
        Log.e(TAG,"常用框架Fragment数据被初始化了");
        super.inttData();
        final String[] data={"OKHttp", "xUtils3","Retrofit2","Fresco","Glide",
                "greenDao","RxJava","volley","Gson","FastJson","picasso",
                "evenBus","jcvideoplayer","pulltorefresh","Expandablelistview",
                "UniversalVideoView","....."};

//        textView.setText("常用页面框架");
        BaseAdapter adapter=new CommonFragmentListAdapter(data, mContext);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s=data[i];
                Toast.makeText(mContext,"data=="+s,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
