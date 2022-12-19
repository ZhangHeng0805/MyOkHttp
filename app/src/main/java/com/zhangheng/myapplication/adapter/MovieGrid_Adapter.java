package com.zhangheng.myapplication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhangheng.myapplication.R;

import java.util.List;
import java.util.Map;

public class MovieGrid_Adapter extends BaseAdapter {

    private List<Map<String,String>> data;
    private Context context;

    public MovieGrid_Adapter(List<Map<String, String>> data, Context context) {
        this.data = data;
        this.context = context;
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
        Holder holder;
        if (view==null){
            holder=new Holder();
            view=View.inflate(context, R.layout.item_grid_movies2,null);
            holder.img= view.findViewById(R.id.item_movie_img);
            holder.name= view.findViewById(R.id.item_movie_name);
            holder.rating= view.findViewById(R.id.item_movie_rating);
            view.setTag(holder);
        }else {
            holder= (Holder) view.getTag();
        }
        Map<String, String> map = data.get(i);

        Glide.with(context).load(map.get("src")).into(holder.img);

        holder.rating.setText(map.get("rating"));
        holder.name.setText(map.get("name"));

        return view;
    }
    class Holder{
        private ImageView img;
        private TextView name,rating;
    }
}
