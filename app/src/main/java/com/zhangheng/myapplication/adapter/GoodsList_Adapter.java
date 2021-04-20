package com.zhangheng.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zhangheng.myapplication.Main10Activity_2;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.bean.books.bookslist.Data;
import com.zhangheng.myapplication.bean.shop.Goods;

import java.util.List;

public class GoodsList_Adapter extends BaseAdapter {
    private final List<Goods> data;
    private final Context context;
    private MyOnClickNum myOnClickNum;
    private int count;

//    private OnItemClickListen onItemClickListen;

    public GoodsList_Adapter(Context context, List<Goods> data/*,OnItemClickListen onItemClickListen*/) {
        this.context=context;
        this.data=data;
//        this.onItemClickListen=onItemClickListen;

    }

    @Override
    public int getCount() {
        return data.size();
    }
    public void clear_num(){
        for (Goods g:data){
            g.setNum(0);
        }
        notifyDataSetInvalidated();
    }

    @Override
    public Object getItem(int i) {
        return data.size();
    }

    @Override
    public long getItemId(int i) {
        return data.size();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Holder holder;
        final Goods d = this.data.get(i);
        if (view==null){
            holder=new Holder();
            view = View.inflate(context, R.layout.item_goods_list,null);
            holder.item_booklist_image=view.findViewById(R.id.item_goodslist_image);
            holder.item_booklist_title=view.findViewById(R.id.item_goodslist_title);
            holder.item_booklist_catalog=view.findViewById(R.id.item_goodslist_catalog);
            holder.item_booklist_bytime=view.findViewById(R.id.item_goodslist_bytime);
            holder.item_booklist_tags=view.findViewById(R.id.item_goodslist_tags);
            holder.btn_add=view.findViewById(R.id.item_goodslist_btn_add);
            holder.btn_sub=view.findViewById(R.id.item_goodslist_btn_sub);
            holder.nuum=view.findViewById(R.id.item_goodslist_txt_num);
            holder.price=view.findViewById(R.id.item_goodslist_txt_price);
            holder.item_layout_booklist=view.findViewById(R.id.item_layout_goodslist);


            view.setTag(holder);
        }else {
            holder= (Holder) view.getTag();
//            holder=new Holder();
        }
        //加载图片
        String imgurl = d.getGoods_image();
        Glide.with(context).load(imgurl).into(holder.item_booklist_image);
        //商品
        String title=d.getGoods_name();
        holder.item_booklist_title.setText(title);
        //店名
        String catalog = d.getStore_name();
        holder.item_booklist_catalog.setText(catalog);
        //销量
        Integer goods_month_much = d.getGoods_month_much();
        holder.item_booklist_bytime.setText(String.valueOf(goods_month_much));
        //简介
        String tags = d.getGoods_introduction();
        holder.item_booklist_tags.setText(tags);

        String p=d.getGoods_price()+"元";
        holder.price.setText(p);

        holder.nuum.setText(String.valueOf(d.getNum()));
        if (!holder.nuum.getText().toString().equals("0")){
            holder.nuum.setTextColor(context.getResources().getColor(R.color.yellow));
        }else {
            holder.nuum.setTextColor(context.getResources().getColor(R.color.black));
        }

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = holder.nuum.getText().toString();
                count=Integer.parseInt(s);
                count++;
                d.setNum(count);
                myOnClickNum.myNumClick(i,1);
                holder.nuum.setText(String.valueOf(d.getNum()));
                if (!holder.nuum.getText().toString().equals("0")){
                    holder.nuum.setTextColor(context.getResources().getColor(R.color.yellow));
                }else {
                    holder.nuum.setTextColor(context.getResources().getColor(R.color.black));
                }
            }
        });
        holder.btn_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = holder.nuum.getText().toString();
                count=Integer.parseInt(s);
                if (count>0){
                    count--;
                    d.setNum(count);
                    myOnClickNum.myNumClick(i,2);
                }
                holder.nuum.setText(String.valueOf(d.getNum()));
                if (!holder.nuum.getText().toString().equals("0")){
                    holder.nuum.setTextColor(context.getResources().getColor(R.color.yellow));
                }else {
                    holder.nuum.setTextColor(context.getResources().getColor(R.color.black));
                }

            }
        });
        return view;
    }
    class Holder{
        ImageView item_booklist_image;
        TextView item_booklist_title,item_booklist_catalog
                ,item_booklist_bytime,item_booklist_tags,nuum,price;
        LinearLayout item_layout_booklist;
        Button btn_add,btn_sub;

    }
    public interface MyOnClickNum{
        void myNumClick(int position,int operation);//operation:1,加；2,减；3,乘；4,除
    }
    public void setMyOnClickNum(MyOnClickNum onClickNum){
        this.myOnClickNum = onClickNum;
    }
}
