package com.zhangheng.myapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/***
 * 固定大小不滚动的GridView
 */
public class FixedGridView extends GridView {

    private Context context;
    public FixedGridView(Context context) {
        super(context);
        this.context=context;
    }

    public FixedGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> Integer.parseInt("2"),MeasureSpec.AT_MOST);//把测量的值传入onMeasure 重绘布局的高度
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
