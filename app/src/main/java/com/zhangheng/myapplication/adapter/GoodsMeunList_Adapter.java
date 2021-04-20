package com.zhangheng.myapplication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.bean.shop.Goods;

import java.util.List;

public class GoodsMeunList_Adapter extends BaseAdapter {
    private Context context;
    private List<Goods> goodsList;
    private int count;

    public GoodsMeunList_Adapter(Context context, List<Goods> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
    }

    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public Object getItem(int i) {
        return goodsList.size();
    }

    @Override
    public long getItemId(int i) {
        return goodsList.size();
    }
    public void removegoods(int i){
        if (goodsList.get(i).getNum()>1) {
            goodsList.remove(i);
            notifyDataSetChanged();
        }

    }
    public void isSimlir(int i){
        if (i>0){
            if (goodsList.get(i).getGoods_id().equals(goodsList.get(i-1).getGoods_id())){
                removegoods(i);
            }
        }
    }
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Holder holder;
        final Goods goods = goodsList.get(i);
//        isSimlir(i);
        if (view==null){
            holder=new Holder();
            view = View.inflate(context, R.layout.item_list_goods_meun,null);
            /*holder.btn_add=view.findViewById(R.id.item_goodsmeunlist_btn_add);
            holder.btn_sub=view.findViewById(R.id.item_goodsmeunlist_btn_sub);*/
            holder.name=view.findViewById(R.id.item_goodsmeunlist_txt_goodsname);
            holder.catalog=view.findViewById(R.id.item_goodsmeunlist_txt_goodsintro);
            holder.price=view.findViewById(R.id.item_goodsmeunlist_txt_price);
            holder.nuum=view.findViewById(R.id.item_goodsmeunlist_txt_num);
            view.setTag(holder);
        }else {
            holder= (Holder) view.getTag();
        }
        String goods_name = goods.getGoods_name();
        holder.name.setText(goods_name);
        String goods_introduction = goods.getGoods_introduction();
        holder.catalog.setText(goods_introduction);
        double goods_price = goods.getGoods_price();
        holder.price.setText(String.valueOf(goods_price)+"元");

        holder.nuum.setText(String.valueOf(goods.getNum())+"件");
        if (!holder.nuum.getText().toString().equals("0")){
            holder.nuum.setTextColor(context.getResources().getColor(R.color.yellow));
        }else {
            holder.nuum.setTextColor(context.getResources().getColor(R.color.black));
        }

        /*holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = holder.nuum.getText().toString();
                count=Integer.parseInt(s);
                count++;
                goods.setNum(count);
//                myOnClickNum.myNumClick(i,1);
                holder.nuum.setText(String.valueOf(goods.getNum()));
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
                    goods.setNum(count);
//                    myOnClickNum.myNumClick(i,2);
                }
                holder.nuum.setText(String.valueOf(goods.getNum()));
                if (!holder.nuum.getText().toString().equals("0")){
                    holder.nuum.setTextColor(context.getResources().getColor(R.color.yellow));
                }else {
                    holder.nuum.setTextColor(context.getResources().getColor(R.color.black));
                }

            }
        });*/
        return view;
    }
    class Holder{
        TextView name,catalog
                ,nuum,price;
        Button btn_add,btn_sub;

    }
}
