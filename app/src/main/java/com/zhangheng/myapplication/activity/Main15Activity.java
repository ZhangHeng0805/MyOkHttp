package com.zhangheng.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.base.BaseFragment;
import com.zhangheng.myapplication.fragment.CommonFragment;
import com.zhangheng.myapplication.fragment.DownloadFragment;
import com.zhangheng.myapplication.fragment.MyFragment;
import com.zhangheng.myapplication.fragment.ToolFragment;

import java.util.ArrayList;
import java.util.List;

public class Main15Activity extends FragmentActivity {

    private RadioGroup m15_rg_main;
    private List<BaseFragment> mBaseFragment;
    private int position;   //选中的fragment对应的位置
    private Fragment mContext;     //上次切换的Fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化View
        initFragment();//初始化Fragment
        setListener();
        m15_rg_main.check(R.id.m15_rb_common_fragme);
    }

    private void setListener() {
        m15_rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.m15_rb_common_fragme:
                    position=0;
                    break;
                    case R.id.m15_rb_tool_fragme:
                        position=1;
                        break;
                    case R.id.m15_rb_downloda_fragme:
                        position=2;
                        break;
                    case R.id.m15_rb_my_fragme:
                        position=3;
                        break;
                        default:
                            position=0;
                            break;
                }

                BaseFragment to=getFragment();
                switchFragment(mContext,to);
            }
        });
    }

    /*
    * from:刚刚显示的fragment，马上要被隐藏
    * to:马上要被替换的fragment，一会儿要显示
    * */
    private void switchFragment(Fragment from,Fragment to) {
        if (from!=to){
            mContext=to;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //才切换
            //判断有没有添加
            if(!to.isAdded()){
                //to没有被添加
                //from隐藏
                if (from!=null){
                    ft.hide(from);
                }
                //to添加
                if (to!=null){
                    ft.add(R.id.m15_fl_content,to).commit();
                }
            }else {
                //to已经被添加了
                //from隐藏
                if (from!=null){
                    ft.hide(from);
                }
                //to展示
                if (to!=null){
                    ft.show(to).commit();
                }
            }
        }
    }

//    private void switchFragment(BaseFragment fragment) {
//        //1.得到FragmentManager
//        FragmentManager fm=getSupportFragmentManager();
//        //2.开启事务
//        FragmentTransaction transaction=fm.beginTransaction();
//        //3.替换
//        transaction.replace(R.id.m15_fl_content,fragment);
//        //4.提交事务
//        transaction.commit();
//
//    }

    private BaseFragment getFragment() {
        BaseFragment fragment = mBaseFragment.get(position);
        return fragment;
    }

    private void initFragment() {
        mBaseFragment=new ArrayList<>();
        mBaseFragment.add(new CommonFragment());
        mBaseFragment.add(new ToolFragment());
        mBaseFragment.add(new DownloadFragment());
        mBaseFragment.add(new MyFragment());
    }

    private void initView() {
        setContentView(R.layout.activity_main15);
        m15_rg_main=findViewById(R.id.m15_rg_main);
    }
}
