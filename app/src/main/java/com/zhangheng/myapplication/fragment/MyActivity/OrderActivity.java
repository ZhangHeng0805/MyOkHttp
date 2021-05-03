package com.zhangheng.myapplication.fragment.MyActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangheng.myapplication.Object.Resuilt;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.adapter.GoodsMeunList_Adapter;
import com.zhangheng.myapplication.adapter.GoodsOrderList_Adapter;
import com.zhangheng.myapplication.bean.shop.Goods;
import com.zhangheng.myapplication.bean.shop.submitgoods.SubmitGoods;
import com.zhangheng.myapplication.bean.shop.submitgoods.goods;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.Utility;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class OrderActivity extends Activity {

    private ImageView order_iv_back,order_iv_help;
    private ListView order_lV_title;
    private SharedPreferences preferences;
    private String name,phone,address,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m15_myfragment_activity_order);

        order_iv_back=findViewById(R.id.order_iv_back);
        order_lV_title=findViewById(R.id.order_lV_title);
        order_iv_help=findViewById(R.id.order_iv_help);


        Listener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferences();
    }

    private void Listener(){
        order_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        order_iv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String help="订单确认：表示商家已经确认接单了，只需等待收货。\n\n" +
                        "订单拒绝：表示商家拒绝接单，此订单已经无效。\n\n" +
                        "未处理：表示顾客已经提交订单，但商家还未处理该订单。\n\n" +
                        "已收货：表示商家已经确认订单，并且顾客已经确认收货。\n\n" +
                        "退货：表示商家已经确认订单，但顾客拒绝收货，并且退货。\n";
                DialogUtil.dialog(OrderActivity.this,"订单状态说明",help);
            }
        });
    }
    private void getdata(){
        final ProgressDialog progressDialog=new ProgressDialog(OrderActivity.this);
        progressDialog.setMessage("加载中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=getResources().getString(R.string.zhangheng_url)+"Insert_Order";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("phone",phone)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        DialogUtil.dialog(OrderActivity.this,"错误",error);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        List<SubmitGoods> submitGoodsList=new ArrayList<>();
                        try {
                             submitGoodsList=gson.fromJson(response, new TypeToken<List<SubmitGoods>>(){}.getType());
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(OrderActivity.this,"错误",e.getMessage());
                            }else {
                                DialogUtil.dialog(OrderActivity.this,"错误",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (submitGoodsList.size()>0){
                            ListAdapter listAdapter = new ListAdapter(OrderActivity.this,submitGoodsList);
                            order_lV_title.setAdapter(listAdapter);
                            listAdapter.setMyOnClick1(new ListAdapter.MyOnClick1() {
                                @Override
                                public void myClick1(int position, int num, String submit_id, int goods_id) {
                                    System.out.println(position+"\t"+num+"\t"+submit_id+"\t"+goods_id);
                                    switch (position){
                                        case 1://确认收货
                                            if (phone!=null) {
                                                getOKOrder(num,submit_id,goods_id,phone);
                                            }else {
                                                DialogUtil.dialog(OrderActivity.this,"账号为空","账户的电话号码为空");
                                            }
                                            break;
                                        case 2://退货
                                            if (phone!=null) {
                                                getNoOrder(submit_id,goods_id,phone);
                                            }else {
                                                DialogUtil.dialog(OrderActivity.this,"账号为空","账户的电话号码为空");
                                            }
                                            break;
                                    }
                                }
                            });
                        }else {
                            android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(OrderActivity.this);
                            d.setTitle("订单列表为空");
                            d.setMessage("对不起，你的订单列表为空");
                            d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                            d.show();
                        }
                    }
                });
    }
    private void getOKOrder(int num, String submit_id, int goods_id ,String phone){
        final ProgressDialog progressDialog=new ProgressDialog(OrderActivity.this);
        progressDialog.setMessage("加载中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=getResources().getString(R.string.zhangheng_url)+"OK_Order";
        Map<String,String> map=new HashMap<>();
        map.put("num", String.valueOf(num));
        map.put("submit_id", submit_id);
        map.put("phone", phone);
        map.put("goods_id", String.valueOf(goods_id));
        OkHttpUtils
                .post()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        String error = OkHttpMessageUtil.error(e);
                        DialogUtil.dialog(OrderActivity.this,"错误",error);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        Resuilt resuilt = new Resuilt();
                        try {
                            resuilt=gson.fromJson(response, Resuilt.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(OrderActivity.this,"错误",e.getMessage());
                            }else {
                                DialogUtil.dialog(OrderActivity.this,"错误",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (resuilt!=null){
                            if (resuilt.getTitle().equals("收货成功")){
                                DialogUtil.dialog(OrderActivity.this,resuilt.getTitle(),"");
                                getPreferences();
                            }else {
                                DialogUtil.dialog(OrderActivity.this,resuilt.getTitle(),resuilt.getMessage());
                            }
                        }else {
                            android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(OrderActivity.this);
                            d.setTitle("网络加载失败");
                            d.setMessage("");
                            d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                            d.show();
                        }
                    }
                });
    }

    private void getNoOrder(String submit_id, int goods_id ,String phone){
        final ProgressDialog progressDialog=new ProgressDialog(OrderActivity.this);
        progressDialog.setMessage("加载中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=getResources().getString(R.string.zhangheng_url)+"NO_Order";
        Map<String,String> map=new HashMap<>();
        map.put("submit_id", submit_id);
        map.put("phone", phone);
        map.put("goods_id", String.valueOf(goods_id));
        OkHttpUtils
                .post()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        String error = OkHttpMessageUtil.error(e);
                        DialogUtil.dialog(OrderActivity.this,"错误",error);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        Resuilt resuilt = new Resuilt();
                        try {
                            resuilt=gson.fromJson(response, Resuilt.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(OrderActivity.this,"错误",e.getMessage());
                            }else {
                                DialogUtil.dialog(OrderActivity.this,"错误",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (resuilt!=null){
                            if (resuilt.getTitle().equals("退货成功")){
                                DialogUtil.dialog(OrderActivity.this,resuilt.getTitle(),"");
                                getPreferences();
                            }else {
                                DialogUtil.dialog(OrderActivity.this,resuilt.getTitle(),resuilt.getMessage());
                            }
                        }else {
                            android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(OrderActivity.this);
                            d.setTitle("网络加载失败");
                            d.setMessage("");
                            d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                            d.show();
                        }
                    }
                });
    }
    private void getPreferences(){
        preferences = getSharedPreferences("customeruser", MODE_PRIVATE);
        phone = preferences.getString("phone", null);
        name=preferences.getString("name",null);
        password = preferences.getString("password", null);
        address = preferences.getString("address", null);

        if (phone!=null) {
            getdata();
        }else {
            android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(OrderActivity.this);
            d.setTitle("账号为空");
            d.setMessage("请登录后再来");
            d.setCancelable(false);
            d.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            d.show();
        }
    }
    static class ListAdapter extends BaseAdapter {
        private Context context;
        private List<SubmitGoods> info;
        private MyOnClick1 myOnClick1;

        public ListAdapter(Context context, List<SubmitGoods> info) {
            this.context = context;
            this.info = info;
        }

        @Override
        public int getCount() {
            return info.size();
        }

        @Override
        public Object getItem(int i) {
            return info.size();
        }

        @Override
        public long getItemId(int i) {
            return info.size();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final Hodler hodler;
            SubmitGoods submitGoods = info.get(i);
            List<goods> goods_list = submitGoods.getGoods_list();
//            System.out.println("size大小："+goods_list.size());
            if (view==null){
                hodler=new Hodler();
                view=View.inflate(context,R.layout.item_order_list,null);
                hodler.order_txt_id=view.findViewById(R.id.order_txt_id);
                hodler.order_txt_price=view.findViewById(R.id.order_txt_price);
                hodler.order_txt_time=view.findViewById(R.id.order_txt_time);
                hodler.order_LV_info=view.findViewById(R.id.order_LV_info);
                hodler.order_RL_info=view.findViewById(R.id.order_RL_info);
                view.setTag(hodler);
            }else {
                hodler= (Hodler) view.getTag();
            }
            hodler.order_txt_id.setText("订单号："+submitGoods.getSubmit_id());
            hodler.order_txt_time.setText("时间："+submitGoods.getTime());
            hodler.order_txt_price.setText("订单金额："+submitGoods.getCount_price()+"元");

            GoodsOrderList_Adapter goodsOrderList_adapter = new GoodsOrderList_Adapter(context, goods_list);
            hodler.order_LV_info.setAdapter(goodsOrderList_adapter);
            Utility.setListViewHeightBasedOnChildren(hodler.order_LV_info);
            goodsOrderList_adapter.setMyOnClick(new GoodsOrderList_Adapter.MyOnClick() {
                @Override
                public void myClick(int position, int num, String submit_id, int goods_id) {
                    myOnClick1.myClick1(position,num,submit_id,goods_id);
                }
            });
            hodler.order_LV_info.setVisibility(View.GONE);
            final boolean[] b = {false};
            hodler.order_RL_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (b[0]){
                        hodler.order_LV_info.setVisibility(View.GONE);
                        b[0] =!b[0];
                    }else {
                        hodler.order_LV_info.setVisibility(View.VISIBLE);
                        b[0] =!b[0];
                    }
                }
            });
            return view;
        }
        class Hodler{
            private TextView order_txt_id,order_txt_price,order_txt_time;
            private RelativeLayout order_RL_info;
            private ListView order_LV_info;
        }
        public interface MyOnClick1{
            void myClick1(int position,int num,String submit_id,int goods_id);
        }
        public void setMyOnClick1(MyOnClick1 onClick){
            this.myOnClick1 = onClick;
        }
    }
}
