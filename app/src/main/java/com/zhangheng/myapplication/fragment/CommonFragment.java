package com.zhangheng.myapplication.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.adapter.GoodsList_Adapter;
import com.zhangheng.myapplication.adapter.GoodsMeunList_Adapter;
import com.zhangheng.myapplication.base.BaseFragment;
import com.zhangheng.myapplication.bean.shop.Customer;
import com.zhangheng.myapplication.bean.shop.Goods;
import com.zhangheng.myapplication.bean.shop.submitgoods.SubmitGoods;
import com.zhangheng.myapplication.bean.shop.submitgoods.goods;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.TimeUtil;
import com.zhangheng.myapplication.view.RefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;

public class CommonFragment extends BaseFragment {

    private TextView m15_fragment_common_txt_notic;
    private Spinner m15_fragment_common_spinner;
    private RefreshListView mListView;
    private ListView m15_fragment_common_listview_meun;
    private List<Goods> goodsList;//?????????????????????
    private List<Goods> g=new ArrayList<>();//????????????????????????
    private TextView m15_fragment_common_txt_num,m15_fragment_common_txt_pice;
    private List<String> spinner_list = new ArrayList<String>();//????????????
    private int spinner_position ;//???????????????????????????
    private CheckBox m115_fragment_common_cb_switch;
    private LinearLayout m115_fragment_common_LL_meun;
    private Button m115_common_btn_clearmeun,m115_common_btn_submit;
    private int num=0;//?????????????????????
    private double pice=0;//????????????????????????
    private static final String TAG=CommonFragment.class.getSimpleName();
    private SharedPreferences preferences;
    private String phone,name,password,address,submit_id;
    SimpleDateFormat time_sdf=new SimpleDateFormat("yyyyMMddHHmmss");

    private ProgressDialog progressDialog,progressDialog1;
    @Override
    protected View initView() {
        Log.e(TAG,"????????????Fragment?????????????????????");
        View view=View.inflate(mContext, R.layout.m15_fragment_common,null);
        mListView=view.findViewById(R.id.m15_fragment_common_listview);
        mListView.setRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (g.size()>0){
                    AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                    d.setTitle("???????????????");
                    d.setMessage("????????????????????????????????????");
                    d.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getOkHttp(spinner_list.get(spinner_position));
                            close_meun();
                            clear_meun();
                            mListView.onRefreshComplete();
                        }
                    });
                    d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mListView.onRefreshComplete();
                        }
                    });
                    d.show();
                }else {
                    getOkHttp(spinner_list.get(spinner_position));
                    mListView.onRefreshComplete();
                    close_meun();
                    clear_meun();
                }
            }
            @Override
            public void onLoadMore() {
            }
        });
        m15_fragment_common_txt_num=view.findViewById(R.id.m15_fragment_common_txt_num);
        m15_fragment_common_txt_pice=view.findViewById(R.id.m15_fragment_common_txt_pice);
        m115_fragment_common_cb_switch=view.findViewById(R.id.m115_fragment_common_cb_switch);
        m115_fragment_common_LL_meun=view.findViewById(R.id.m115_fragment_common_LL_meun);
        m115_common_btn_clearmeun=view.findViewById(R.id.m115_common_btn_clearmeun);
        m115_common_btn_submit=view.findViewById(R.id.m115_common_btn_submit);
        m15_fragment_common_txt_notic=view.findViewById(R.id.m15_fragment_common_txt_notic);
        m15_fragment_common_txt_notic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinner_list.size()>0){
                    getOkHttp(spinner_list.get(spinner_position));
                }else {
                    getOkHttp("??????");
                }
            }
        });
        m15_fragment_common_listview_meun=view.findViewById(R.id.m15_fragment_common_listview_meun);
        m15_fragment_common_listview_meun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Goods goods = g.get(i);
                int i1 = goodsList.indexOf(goods);
                if (i1>=0){
                    if (i1==goodsList.size()-1) {
                        mListView.setSelection(i1+1);
                    }else {
                        mListView.setSelection(i1+1);
                    }
                }
            }
        });
        m115_common_btn_clearmeun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ???????????????");
                clear_meun();
            }
        });
        m115_common_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPreferences();
                Log.d(TAG, "onClick: ????????????");
                final SubmitGoods submitGoods = new SubmitGoods();
                List<goods> goodslist=new ArrayList<>();
                String[] strings = new String[g.size()+5];
                for (Goods goods:g){
                    com.zhangheng.myapplication.bean.shop.submitgoods.goods goods1 = new goods();
                    goods1.setGoods_id(goods.getGoods_id());
                    goods1.setGoods_name(goods.getGoods_name());
                    goods1.setGoods_price(goods.getGoods_price());
                    goods1.setNum(goods.getNum());
                    goods1.setStore_id(goods.getStore_id());
                    goodslist.add(goods1);
                }
                for (int i=0;i<g.size();i++){
                    String s=g.get(i).getGoods_name()+" "+g.get(i).getGoods_price()+"??? ?? "+g.get(i).getNum();
                    strings[i]=s;
                }
                strings[g.size()+4]="???????????????"+num+"???:????????????"+pice+"???";
                if (phone!=null) {
                    strings[g.size() + 3] = "???????????????" + phone;
                }else {
                    strings[g.size() + 3] = "??????????????????";
                }
                if (address!=null&&!address.equals("????????????")) {
                    strings[g.size()+2] = "?????????" + address;
                }else {
                    strings[g.size()+2] = "????????????";
                }
                if (name!=null) {
                    strings[g.size()+1] = "????????????" + name;
                }else {
                    strings[g.size()+1] = "???????????????";
                }
                strings[g.size()]="----------------------------";
                submitGoods.setGoods_list(goodslist);
                submitGoods.setCount_price(pice);
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("????????????");
                builder.setCancelable(false);
                builder.setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getPreferences();
                        if (phone!=null&&name!=null&&password!=null) {
                            submitGoods.setName(name);
                            submitGoods.setPhone(phone);
                            if (address!=null&&!address.equals("????????????")) {
                                submitGoods.setAddress(address);
                                submit_id=time_sdf.format(new Date())+"_"+UUID.randomUUID().toString().substring(0,8);
                                submitGoods.setTime(TimeUtil.getSystemTime());
                                submitGoods.setSubmit_id(submit_id);
                                OkHttp2(submitGoods);
                            }else {
                                DialogUtil.dialog(getContext(),"????????????","?????????\"??????\"-\"????????????\"???????????????,????????????");
                            }
                        }else {
                            DialogUtil.dialog(getContext(),"??????????????????","?????????\"??????\"???????????????,????????????");
                        }
                    }
                });
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });

        m115_fragment_common_cb_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (num!=0) {
                        m115_fragment_common_LL_meun.setVisibility(View.VISIBLE);
                    }else {
                        Toast.makeText(getContext(),"?????????????????????????????????",Toast.LENGTH_SHORT).show();
                        close_meun();
                    }
                }else {
                    m115_fragment_common_LL_meun.setVisibility(View.GONE);
                }
            }
        });
        m15_fragment_common_spinner=view.findViewById(R.id.m15_fragment_common_spinner);
        return view;
    }

    @Override
    protected void inttData() {
        Log.e(TAG,"????????????Fragment?????????????????????");
        super.inttData();
        start();
        getOkHttp("??????");

    }

    private void getPreferences(){
        preferences = mContext.getSharedPreferences("customeruser", MODE_PRIVATE);
        phone = preferences.getString("phone", null);
        name=preferences.getString("name",null);
        password = preferences.getString("password", null);
        address = preferences.getString("address", null);
        Log.d(TAG, "getPreferences: "+phone+name);
    }
    private void getOkHttp(String type){
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("??????????????????");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=getResources().getString(R.string.zhangheng_url)+"Goods/allgoodslist";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("GoodsType",type)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        progressDialog.dismiss();
                        final AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                        d.setTitle("????????????");
                        d.setMessage(OkHttpMessageUtil.error(e));
                        d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FragmentActivity activity = getActivity();
                                activity.finish();
                            }
                        });
                        d.show();
                        mListView.setVisibility(View.GONE);
                        m15_fragment_common_txt_notic.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progressDialog.dismiss();
                        Gson gson=new Gson();
                        try {
                            goodsList = gson.fromJson(response, new TypeToken<List<Goods>>() {
                            }.getType());
                        }catch (Exception e){
                            Log.e(TAG, "onResponse: "+e.getMessage() );
                            if (OkHttpMessageUtil.response(response)==null) {
                                DialogUtil.dialog(getContext(), "??????", e.getMessage());
                            }else {
                                DialogUtil.dialog(getContext(), "??????", OkHttpMessageUtil.response(response));
                            }
                        }

                        if (goodsList!=null) {
                            m15_fragment_common_txt_notic.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                            for (Goods g : goodsList) {
                                g.setGoods_image(getResources().getString(R.string.zhangheng_url)
                                        + "downloads/show/" + g.getGoods_image());
                                g.setNum(0);
                                if (spinner_list.contains(g.getGoods_type())) {
                                } else {
                                    spinner_list.add(g.getGoods_type());
                                }
                            }

                            final BaseAdapter adapter = new GoodsList_Adapter(mContext, goodsList);
                            mListView.setAdapter(adapter);

                            ((GoodsList_Adapter) adapter).setMyOnClickNum(new GoodsList_Adapter.MyOnClickNum() {
                                @Override
                                public void myNumClick(int position, int operation) {
                                    switch (operation) {
                                        case 1:
                                            num += 1;
                                            double p1 = goodsList.get(position).getGoods_price();
                                            pice += p1;
                                            int indexAdd = g.indexOf(goodsList.get(position));
                                            if (indexAdd >= 0) {
                                                g.get(indexAdd).setNum(g.get(indexAdd).getNum());
                                                GoodsMeunList_Adapter goodsMeunList_adapter = new GoodsMeunList_Adapter(getContext(), g);
                                                m15_fragment_common_listview_meun.setAdapter(goodsMeunList_adapter);
                                            } else {
                                                g.add(goodsList.get(position));
                                                GoodsMeunList_Adapter goodsMeunList_adapter = new GoodsMeunList_Adapter(getContext(), g);
                                                m15_fragment_common_listview_meun.setAdapter(goodsMeunList_adapter);
                                            }
                                            open_meun();
                                            break;
                                        case 2:
                                            if (num > 0) {
                                                num -= 1;
                                                double p2 = goodsList.get(position).getGoods_price();
                                                pice -= p2;
                                                int indexSub = g.indexOf(goodsList.get(position));
                                                if (indexSub >= 0) {
                                                    if (g.get(indexSub).getNum() > 0) {
                                                        g.get(indexSub).setNum(g.get(indexSub).getNum());
                                                        GoodsMeunList_Adapter goodsMeunList_adapter = new GoodsMeunList_Adapter(getContext(), g);
                                                        m15_fragment_common_listview_meun.setAdapter(goodsMeunList_adapter);
                                                    } else {
                                                        g.remove(indexSub);
                                                        GoodsMeunList_Adapter goodsMeunList_adapter = new GoodsMeunList_Adapter(getContext(), g);
                                                        m15_fragment_common_listview_meun.setAdapter(goodsMeunList_adapter);
                                                    }
                                                }
                                            }
                                            break;
                                    }
                                    if (num == 0) {
                                        close_meun();
                                    }
                                    m15_fragment_common_txt_num.setText(String.valueOf(num) + "???");
                                    BigDecimal bg = new BigDecimal(pice);
                                    pice = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    m15_fragment_common_txt_pice.setText(String.valueOf(pice) + "???");

                                }
                            });
                        }else {
                            mListView.setVisibility(View.GONE);
                            m15_fragment_common_txt_notic.setVisibility(View.VISIBLE);
                        }
                        if (spinner_list.size()>0){
                            m15_fragment_common_spinner.setVisibility(View.VISIBLE);
                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getContext(), R.layout.item_list_text, spinner_list);
                            m15_fragment_common_spinner.setAdapter(spinner_adapter);
                            m15_fragment_common_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                    String s = spinner_list.get(i);
//                                    List<Goods> glist=new ArrayList<>();
                                    if (spinner_position!=i) {//??????????????????
                                        refresh_GoodList(i);
                                        Log.d(TAG, "onItemSelected: "+s);
                                        spinner_position=i;
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }

                            });
                            m15_fragment_common_spinner.setSelection(spinner_position);

                        }

                    }
                });
    }

    private void start(){
        m15_fragment_common_txt_notic.setVisibility(View.GONE);
        m115_fragment_common_LL_meun.setVisibility(View.GONE);
        m15_fragment_common_spinner.setVisibility(View.GONE);
        spinner_position=0;
        spinner_list.add("??????");
    }
    private void close_meun(){
        m115_fragment_common_LL_meun.setVisibility(View.GONE);
        m115_fragment_common_cb_switch.setChecked(false);
    }
    private void open_meun(){
        m115_fragment_common_LL_meun.setVisibility(View.VISIBLE);
        m115_fragment_common_cb_switch.setChecked(true);
    }
    private void clear_meun(){
        GoodsList_Adapter adapter=new GoodsList_Adapter(mContext,goodsList);
        adapter.clear_num();
        num=0;
        pice=0;
        m15_fragment_common_txt_num.setText(String.valueOf(num)+"???");
        m15_fragment_common_txt_pice.setText(String.valueOf(pice)+"???");
        m115_fragment_common_LL_meun.setVisibility(View.GONE);
        m115_fragment_common_cb_switch.setChecked(false);
        g.clear();
    }
    private void refresh_GoodList(int i){
        if (g.size()>0){
            AlertDialog.Builder d=new AlertDialog.Builder(getContext());
            d.setTitle("???????????????????????????");
            d.setMessage("??????????????????????????????????????????????????????");
            d.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getOkHttp(spinner_list.get(spinner_position));
                    close_meun();
                    clear_meun();

                }
            });
            d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            d.show();
        }else {
            getOkHttp(spinner_list.get(i));
            close_meun();
            clear_meun();
        }


    }

    private void OkHttp2(SubmitGoods submitGoods){
        progressDialog1=new ProgressDialog(getContext());
        progressDialog1.setMessage("????????????????????????");
        progressDialog1.setIndeterminate(true);
        progressDialog1.setCancelable(false);
        progressDialog1.show();
        String url=getResources().getString(R.string.zhangheng_url)+"Goods/submitgoodslist";
        Gson gson = new Gson();
        String json = gson.toJson(submitGoods);
        Map<String,String> map=new HashMap<>();
        map.put("submitGoodsList",json);
        OkHttpUtils
                .post()
                .params(map)
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        progressDialog1.dismiss();
                        final AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                        d.setTitle("??????????????????");
                        d.setMessage(OkHttpMessageUtil.error(e));
                        d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FragmentActivity activity = getActivity();
                                activity.finish();
                            }
                        });
                        d.show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: "+response);
                        progressDialog1.dismiss();
                        if (response.equals("??????")){
                            AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                            d.setTitle("??????????????????");
                            d.setMessage("???????????????"+submit_id+"\t\n???????????????"+pice+"???");
                            d.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    clear_meun();
                                    getOkHttp(spinner_list.get(spinner_position));
                                }
                            });
                            d.show();
                        }else {
                            AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                            d.setTitle("??????????????????");
                            d.setMessage("??????????????????????????????");
                            d.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            d.show();
                        }
                    }
                });
    }
}
