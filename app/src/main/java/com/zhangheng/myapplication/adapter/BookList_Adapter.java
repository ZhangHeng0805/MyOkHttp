package com.zhangheng.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

    public BookList_Adapter(Context context, List<Data> data) {
        this.context=context;
        this.data=data;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Holder holder;
        if (view==null){
            holder=new Holder();
            view = View.inflate(context, R.layout.item_book_list,null);
            holder.item_booklist_image=view.findViewById(R.id.item_booklist_image);
            holder.item_booklist_title=view.findViewById(R.id.item_booklist_title);
            holder.item_booklist_catalog=view.findViewById(R.id.item_booklist_catalog);
            holder.item_booklist_bytime=view.findViewById(R.id.item_booklist_bytime);
            holder.item_booklist_tags=view.findViewById(R.id.item_booklist_tags);
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
        return view;
    }

    class Holder{
        ImageView item_booklist_image;
        TextView item_booklist_title,item_booklist_catalog
                ,item_booklist_bytime,item_booklist_tags;

    }
}
