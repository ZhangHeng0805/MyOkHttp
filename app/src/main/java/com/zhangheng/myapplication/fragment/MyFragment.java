package com.zhangheng.myapplication.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.DialogPreference;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zhangheng.myapplication.M12_LoginActivity;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.base.BaseFragment;
import com.zhangheng.myapplication.bean.shop.Customer;
import com.zhangheng.myapplication.fragment.MyActivity.Location_Activity;
import com.zhangheng.myapplication.fragment.MyActivity.Login_Activity;
import com.zhangheng.myapplication.fragment.MyActivity.UserInfoActivity;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;

public class MyFragment extends BaseFragment {

    private TextView textView;
    private RelativeLayout m15_fragment_my_RL_user;
    private static final String TAG= MyFragment.class.getSimpleName();
    private TextView m15_fragment_my_txt_username,m15_fragment_my_txt_useraddress;
    private ImageView m15_fragment_my_iv_usericon;
    private SharedPreferences preferences;
    private String phone,name,password,address;
    private Button m15_fragment_my_btn_exit;
    private ListView m15_fragment_my_listview;

    @Override
    protected View initView() {
        Log.e(TAG,"我的框架Fragment页面被初始化了");
        View view = View.inflate(mContext, R.layout.m15_fragment_my, null);
        m15_fragment_my_txt_username=view.findViewById(R.id.m15_fragment_my_txt_username);
        m15_fragment_my_txt_useraddress=view.findViewById(R.id.m15_fragment_my_txt_useraddress);
        m15_fragment_my_iv_usericon=view.findViewById(R.id.m15_fragment_my_iv_usericon);
        m15_fragment_my_RL_user=view.findViewById(R.id.m15_fragment_my_RL_user);
        m15_fragment_my_btn_exit=view.findViewById(R.id.m15_fragment_my_btn_exit);
        m15_fragment_my_listview=view.findViewById(R.id.m15_fragment_my_listview);
        ListAdapter listAdapter = new ListAdapter(getContext());
        m15_fragment_my_listview.setAdapter(listAdapter);
        m15_fragment_my_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        getPreferences();
                        if (phone!=null&&name!=null&&password!=null){
                            Intent intent = new Intent(getContext(), Location_Activity.class);
                            startActivity(intent);
                        }else {
                            DialogUtil.dialog(getContext(),"请登录后在操作","用户没有登录，请登录后在操作");
                        }
                        break;
                    case 1:
                        String html=getResources().getString(R.string.zhangheng_url)+"registration";
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(html));
                        startActivity(intent);
                        break;
                }
            }
        });

        m15_fragment_my_RL_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPreferences();
                if (phone!=null&&name!=null&&password!=null){
                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), Login_Activity.class);
                    startActivity(intent);
                }
            }
        });
        m15_fragment_my_btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPreferences();
            }
        });
        return view;
    }
    @Override
    protected void inttData() {
        Log.e(TAG,"我的框架Fragment数据被初始化了");
        super.inttData();
        start();
    }

    @Override
    public void onResume() {
        super.onResume();
        start();
    }

    private void getIcon(){
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("刷新中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=getResources().getString(R.string.zhangheng_url)+"Customer/getCustomer";
        Map<String,String> map=new HashMap<>();
        map.put("username",phone);
        map.put("password",password);
        OkHttpUtils
                .post()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                        d.setTitle("加载失败");
                        d.setMessage(OkHttpMessageUtil.error(e));
                        d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("退出", new DialogInterface.OnClickListener() {
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
                        Gson gson = new Gson();
                        Customer customer = new Customer();
                        try {
                            customer = gson.fromJson(response, Customer.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(getContext(),"错误",e.getMessage());
                            }else {
                                DialogUtil.dialog(getContext(),"错误",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (customer!=null){
                            String url=getResources().getString(R.string.zhangheng_url)+"downloads/show/"+customer.getIcon();
                            Glide.with(getContext()).load(url).into(m15_fragment_my_iv_usericon);
                            m15_fragment_my_txt_useraddress.setText(customer.getAddress());
                            SharedPreferences sharedPreferences=getContext().getSharedPreferences("customeruser",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("address",customer.getAddress());
                            editor.commit();
                        }else {
                            AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                            d.setTitle("网络加载失败");
                            d.setMessage("");
                            d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FragmentActivity activity = getActivity();
                                    activity.finish();
                                }
                            });
                            d.show();
                        }

                    }
                });

    }

    private void start(){
        getPreferences();
        if (phone!=null&&name!=null&&password!=null){
            m15_fragment_my_btn_exit.setVisibility(View.VISIBLE);
            getIcon();
            m15_fragment_my_txt_username.setText(name);
            m15_fragment_my_txt_useraddress.setText(address);
        }else {
            m15_fragment_my_btn_exit.setVisibility(View.GONE);
        }
    }
    private void getPreferences(){
        preferences = mContext.getSharedPreferences("customeruser", MODE_PRIVATE);
        phone = preferences.getString("phone", null);
        name=preferences.getString("name",null);
        password = preferences.getString("password", null);
        address = preferences.getString("address", null);
        Log.d(TAG, "getPreferences: "+phone+name);
    }
    private void clearPreferences(){
        if (name!=null) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setTitle("退出登录");
            builder1.setMessage("是否退出该账户的登录信息？");
            builder1.setCancelable(false);
            builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    preferences = mContext.getSharedPreferences("customeruser", MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = preferences.edit();
                    editor1.clear();
                    editor1.commit();
                    exitState();
                }
            });
            builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder1.create().show();

        }else {
            DialogUtil.dialog(getContext(),"退出失败","账户已经不存在了");
            exitState();
        }
    }
    private void exitState(){
        m15_fragment_my_btn_exit.setVisibility(View.GONE);
        m15_fragment_my_iv_usericon.setImageResource(R.drawable.icon);
        m15_fragment_my_txt_username.setText("暂无用户，点击登录");
        m15_fragment_my_txt_useraddress.setText("暂无地址");
    }

    //设置列表的适配器
    class ListAdapter extends BaseAdapter{
        private Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        private String[] info={
                "位置设置",
                "注册商家"
        };
        private Integer[] icon={
                R.drawable.location,
                R.drawable.zhuce,
        };

        @Override
        public int getCount() {
            return info.length;
        }

        @Override
        public Object getItem(int i) {
            return info.length;
        }

        @Override
        public long getItemId(int i) {
            return info.length;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Hodler hodler;
            if (view==null){
                hodler=new Hodler();
                view=View.inflate(context,R.layout.item_m15_myfragment_list,null);
                hodler.iv_icon=view.findViewById(R.id.item_m15_myfragment_iv_icon);
                hodler.txt_info=view.findViewById(R.id.item_m15_myfragment_txt_info);
                view.setTag(hodler);
            }else {
                hodler= (Hodler) view.getTag();
            }
            hodler.iv_icon.setImageResource(icon[i]);
            hodler.txt_info.setText(info[i]);
            return view;
        }
        class Hodler{
            private ImageView iv_icon;
            private TextView txt_info;
        }
    }
}
