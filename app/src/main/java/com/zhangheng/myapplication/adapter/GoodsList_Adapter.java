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

import com.bumptech.glide.Glide;
import com.zhangheng.myapplication.Main10Activity_2;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.bean.books.bookslist.Data;
import com.zhangheng.myapplication.bean.shop.Goods;

import java.util.List;

public class GoodsList_Adapter extends BaseAdapter {
    private final List<Goods> data;
    private final Context context;
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
        }
        Goods d = this.data.get(i);
        //加载图片
        String imgurl = d.getGoods_image();
        Glide.with(context).load(imgurl).into(holder.item_booklist_image);
        //书名
        String title=d.getGoods_name();
        holder.item_booklist_title.setText(title);
        //类型
        String catalog = d.getStore_name();
        holder.item_booklist_catalog.setText(catalog);
        //时间
        String bytime = d.getTime();
        holder.item_booklist_bytime.setText(bytime);
        //标签
        String tags = d.getGoods_introduction();
        holder.item_booklist_tags.setText(tags);

        String p=d.getGoods_price()+"元";
        holder.price.setText(p);

        count=0;
        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = holder.nuum.getText().toString();
                count=Integer.parseInt(s);
                count++;
                holder.nuum.setText(String.valueOf(count));
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
                }
                holder.nuum.setText(String.valueOf(count));
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
}
