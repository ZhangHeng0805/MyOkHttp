package com.zhangheng.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.view.RefreshListView;

import java.util.ArrayList;
import java.util.List;

public class Test1Activity extends AppCompatActivity {
    private RefreshListView listview;
    private MyAdapter myAdapter;
    private List<String> listDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        listview = (RefreshListView) findViewById(R.id.listview);
        listview.setRefreshListener(new RefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Thread(){
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        listDatas.add(0,"我是下拉刷新出来的数据!");

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                myAdapter.notifyDataSetChanged();
                                listview.onRefreshComplete();
                            }
                        });
                    };

                }.start();
            }


            @Override
            public void onLoadMore() {
                new Thread(){
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        listDatas.add("我是加载更多出来的数据!1");
                        listDatas.add("我是加载更多出来的数据!2");
                        listDatas.add("我是加载更多出来的数据!3");

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                myAdapter.notifyDataSetChanged();
                                listview.onRefreshComplete();
                            }
                        });
                    };

                }.start();
            }

        });
        listDatas = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            listDatas.add("这是一条数据：" + i);
        }
        myAdapter = new MyAdapter();
        listview.setAdapter(myAdapter);
    }


    class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
// TODO Auto-generated method stub
            return listDatas.size();
        }


        @Override
        public Object getItem(int position) {
// TODO Auto-generated method stub
            return listDatas.get(position);
        }


        @Override
        public long getItemId(int position) {
// TODO Auto-generated method stub
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
// TODO Auto-generated method stub


            TextView textView = new TextView(getApplicationContext());
            textView.setTextSize(18f);
            textView.setTextColor(Color.BLACK);
            textView.setText(listDatas.get(position));
            return textView;
        }


    }
}
