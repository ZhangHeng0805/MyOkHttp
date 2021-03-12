package com.zhangheng.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.bean.books.bookslist.Data;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.List;

import okhttp3.Call;

public class BookList_Adapter extends BaseAdapter {
    private final List<Data> data;
    private final Context context;
    private OnItemClickListen onItemClickListen;

    public BookList_Adapter(Context context, List<Data> data,OnItemClickListen onItemClickListen) {
        this.context=context;
        this.data=data;
        this.onItemClickListen=onItemClickListen;

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
            view = View.inflate(context, R.layout.item_book_list,null);
            holder.item_booklist_image=view.findViewById(R.id.item_booklist_image);
            holder.item_booklist_title=view.findViewById(R.id.item_booklist_title);
            holder.item_booklist_catalog=view.findViewById(R.id.item_booklist_catalog);
            holder.item_booklist_bytime=view.findViewById(R.id.item_booklist_bytime);
            holder.item_booklist_tags=view.findViewById(R.id.item_booklist_tags);
            holder.item_layout_booklist=view.findViewById(R.id.item_layout_booklist);
            view.setTag(holder);
        }else {
            holder= (Holder) view.getTag();
        }
        Data d = this.data.get(i);
        //加载图片
        String imgurl = d.getImg();
        Glide.with(context).load(imgurl).into(holder.item_booklist_image);
//        OkHttpUtils
//                .get()
//                .url(imgurl)
//                .build()
//                .execute(new BitmapCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//                    @Override
//                    public void onResponse(Bitmap response, int id) {
//                        holder.item_booklist_image.setImageBitmap(response);
//                    }
//                });
        //书名
        String title="《"+d.getTitle()+"》";
        holder.item_booklist_title.setText(title);
        //类型
        String catalog = d.getCatalog();
        holder.item_booklist_catalog.setText(catalog);
        //时间
        String bytime = d.getBytime();
        holder.item_booklist_bytime.setText(bytime);
        //标签
        String tags = d.getTags();
        holder.item_booklist_tags.setText(tags);
        holder.item_layout_booklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListen.onItemClick(i);
            }
        });

        return view;
    }

    class Holder{
        ImageView item_booklist_image;
        TextView item_booklist_title,item_booklist_catalog
                ,item_booklist_bytime,item_booklist_tags;
        ViewGroup item_layout_booklist;

    }
    public interface OnItemClickListen{
        void onItemClick(int position);
    }
}
