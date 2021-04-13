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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.adapter.CommonFragmentListAdapter;
import com.zhangheng.myapplication.adapter.GoodsList_Adapter;
import com.zhangheng.myapplication.base.BaseFragment;
import com.zhangheng.myapplication.bean.shop.Goods;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

public class CommonFragment extends BaseFragment {

    private TextView textView;
    private ListView mListView;
    private List<Goods> goodsList;
    private static final String TAG=CommonFragment.class.getSimpleName();

    @Override
    protected View initView() {
        Log.e(TAG,"常用框架Fragment页面被初始化了");
        View view=View.inflate(mContext, R.layout.m15_fragment_common_listview,null);
        mListView=view.findViewById(R.id.m15_fragment_common_listview);
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
        getOkHttp();

//        textView.setText("常用页面框架");
//        BaseAdapter adapter=new CommonFragmentListAdapter(data, mContext);
//        mListView.setAdapter(adapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String s=data[i];
//                Toast.makeText(mContext,"data=="+s,Toast.LENGTH_SHORT).show();
//            }
//        });
    }
    private void getOkHttp(){
        String url=getResources().getString(R.string.zhangheng_url)+"Goods/allgoodslist";
        OkHttpUtils
                .post()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson=new Gson();
                        goodsList = gson.fromJson(response, new TypeToken<List<Goods>>() {
                        }.getType());
                        for (Goods g:goodsList){
                            g.setGoods_image(getResources().getString(R.string.zhangheng_url)
                                    +"downloads/show/"+g.getGoods_image());
                        }
                        BaseAdapter adapter=new GoodsList_Adapter(mContext,goodsList);
                        mListView.setAdapter(adapter);
                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Goods goods = goodsList.get(i);
                                Toast.makeText(getActivity(),goods.getGoods_name(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
    }
}
