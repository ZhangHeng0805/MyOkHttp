package com.zhangheng.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhangheng.myapplication.R;

import java.util.List;
import java.util.Map;

public class ImgText1Adapter extends BaseAdapter {
    private Context context;
    private List<Map<String,String>> data;

    public ImgText1Adapter(Context context, List<Map<String, String>> data) {
        this.context = context;
        this.data = data;
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
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Map<String, String> d = data.get(i);
        Holder holder=null;
        if (view==null){
            holder=new Holder();
            view=View.inflate(context, R.layout.item_list_img_text1,null);
            holder.img=view.findViewById(R.id.item_imgText1_img);
            holder.text=view.findViewById(R.id.item_imgText1_text);
            view.setTag(holder);
        }else {
            holder = (Holder) view.getTag();
        }
        //图片路径
        String img = d.get("img");
        String text = d.get("text");

        Bitmap bitmap = getBitmap(img);
//        Bitmap bitmap = AndroidImageUtil.createReflectionBitmap(src);

        holder.img.setImageBitmap(bitmap);
        holder.text.setText(text);
        return view;
    }
    private Bitmap getBitmap(String path){
        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds=true;
        options.inPreferredConfig= Bitmap.Config.RGB_565;
        options.inSampleSize=2;
        Bitmap src = BitmapFactory.decodeFile(path,options);
        return src;
    }
    class Holder{
        private ImageView img;
        private TextView text;
    }
}
