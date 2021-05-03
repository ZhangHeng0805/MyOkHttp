package com.zhangheng.myapplication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.bean.shop.submitgoods.goods;
import com.zhangheng.myapplication.fragment.MyActivity.OrderActivity;

import java.util.List;

public class GoodsOrderList_Adapter extends BaseAdapter {
    private Context context;
    private List<goods> goodsList;

    private MyOnClick myOnClick;

    public GoodsOrderList_Adapter(Context context, List<goods> goodsList) {
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
        final goods g = goodsList.get(i);
//        isSimlir(i);
        if (view==null){
            holder=new Holder();
            view = View.inflate(context, R.layout.item_list_goods_order,null);
            holder.btn_OK=view.findViewById(R.id.LL_Order_btn_OK);
            holder.btn_NO=view.findViewById(R.id.LL_Order_btn_No);
            holder.name=view.findViewById(R.id.item_goodsmeunlist_txt_goodsname);
            holder.catalog=view.findViewById(R.id.item_goodsmeunlist_txt_goodsintro);
            holder.price=view.findViewById(R.id.item_goodsmeunlist_txt_price);
            holder.nuum=view.findViewById(R.id.item_goodsmeunlist_txt_num);
            holder.LL_Order_result=view.findViewById(R.id.LL_Order_result);
            view.setTag(holder);
        }else {
            holder= (Holder) view.getTag();
        }

        holder.LL_Order_result.setVisibility(View.GONE);
        String goods_name = g.getGoods_name();
        holder.name.setText(goods_name);
        String state = g.getState();

        switch (state){
            case "订单确认":
                holder.catalog.setTextColor(context.getResources().getColor(R.color.green));
                holder.LL_Order_result.setVisibility(View.VISIBLE);
                break;
            case "订单拒绝":
                holder.catalog.setTextColor(context.getResources().getColor(R.color.red));
                break;
            case "未处理":
                holder.catalog.setTextColor(context.getResources().getColor(R.color.yellow));
                break;
            case "已收货":
                holder.catalog.setTextColor(context.getResources().getColor(R.color.blue));
                break;
            case "退货":
                holder.catalog.setTextColor(context.getResources().getColor(R.color.red));
                break;
        }
        holder.catalog.setText(state);
        double goods_price = g.getGoods_price();
        holder.price.setText(String.valueOf(goods_price)+"元");

        holder.nuum.setText(String.valueOf(g.getNum())+"件");
        if (!holder.nuum.getText().toString().equals("0")){
            holder.nuum.setTextColor(context.getResources().getColor(R.color.yellow));
        }else {
            holder.nuum.setTextColor(context.getResources().getColor(R.color.black));
        }
        holder.btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myOnClick.myClick(1,g.getNum(),g.getList_id(),g.getGoods_id());
            }
        });
        holder.btn_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myOnClick.myClick(2,g.getNum(),g.getList_id(),g.getGoods_id());
            }
        });

        return view;
    }
    class Holder{
        TextView name,catalog
                ,nuum,price;
        LinearLayout LL_Order_result;
        Button btn_OK,btn_NO;

    }
    public interface MyOnClick{
        void myClick(int position,int num,String submit_id,int goods_id);//position 1.确认收货；2.退货
    }
    public void setMyOnClick(MyOnClick onClick){
        this.myOnClick = onClick;
    }
}
