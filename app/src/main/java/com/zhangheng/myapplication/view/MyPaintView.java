package com.zhangheng.myapplication.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zhangheng.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 张恒 on 2020/11/14 0014.
 */

public class MyPaintView extends View {
    //    private Resources myResources;
    //画笔，定义绘制属性
    private Paint myPaint;
    private Paint mBitmapPaint;
    //绘制路径
    private Path myPath;
    //画布及其底层位图
    private Bitmap myBitmap;
    private Canvas myCanvas;
    private Context context;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    //记录宽度和高度
    private int mWidth, mHeight;
    String paintcolor = "black";
    int paintsize = 20;

    private List<Map<String, Object>> data = new ArrayList<>();


    boolean flag_change = true;

    public void setMyPaintColor(String paintcolor) {
        this.paintcolor = paintcolor;
        init();
    }

    public void setMyPaintSize(int paintsize) {
        this.paintsize = paintsize;
        init();
    }

    public MyPaintView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MyPaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public MyPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    //初始化
    private void init() {
//        myResources=getResources();
        //绘制自由曲线的画笔

//        if (myPaint == null)
            myPaint = new Paint();

        myPaint.setAntiAlias(true);
        myPaint.setDither(true);
        setColor();
        //myPaint.setColor(myResources.getColor(R.color.red));
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setStrokeWidth(paintsize);
        if (myPath == null)
            myPath = new Path();
        if (mBitmapPaint == null)
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    private void setColor() {
        switch (paintcolor) {
            case "red":
                myPaint.setColor(context.getColor(R.color.red));
                //Log.d("color","red");
                break;
            case "pink":
                myPaint.setColor(context.getColor(R.color.pink));
                //Log.d("color","red");
                break;
            case "orange":
                myPaint.setColor(context.getColor(R.color.orange));
                break;
            case "yellow":
                myPaint.setColor(context.getColor(R.color.yellow));
                break;
            case "green":
                myPaint.setColor(context.getColor(R.color.green));
                //Log.d("color","green");
                break;
            case "cyan":
                myPaint.setColor(context.getColor(R.color.cyan));
                break;
            case "skyblue":
                myPaint.setColor(context.getColor(R.color.skyblue));
                //Log.d("color","red");
                break;
            case "blue":
                myPaint.setColor(context.getColor(R.color.blue));
                break;
            case "mediumpurple":
                myPaint.setColor(context.getColor(R.color.mediumpurple));
                break;
            case "purple":
                myPaint.setColor(context.getColor(R.color.purple));
                break;

            case "black":
                myPaint.setColor(context.getColor(R.color.black));
                break;
            case "white":
                myPaint.setColor(context.getColor(R.color.white));
                break;
        }
    }

    //画布大小改变时
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        myBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas(myBitmap);
    }

    //手指触摸屏幕时的各种动作处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            //手机开始触摸屏幕时
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                //Log.d("ontouchenvent","1");
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                //Log.d("ontouchenvent","2");
                break;
            case MotionEvent.ACTION_UP:
                touch_up(x, y);
                invalidate();
                //Log.d("ontouchenvent","3");
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景颜色
        canvas.drawColor(context.getColor(R.color.white));
//        System.out.println("draw**************");

        //若不调用此方法，绘制结束后画布将清空
//        canvas.drawBitmap(myBitmap, 0, 0, mBitmapPaint);

        if (flag_change) {
            for (Map<String, Object> m : data) {
                //绘制路径
                canvas.drawPath((Path) m.get("path"), (Paint) m.get("paint"));
            }
        } else {
            boolean f=true;
            if (f) {
                for (Map<String, Object> m : data) {
                    canvas.drawPath((Path) m.get("path"), (Paint) m.get("paint"));
                }
            }
            canvas.drawPath(myPath,myPaint);
            f=false;
        }

    }

    private void touch_up(float x, float y) {
        myPath.lineTo(mX, mY);
        Map<String, Object> map = new HashMap<>();
        map.put("path", myPath);
        map.put("paint", myPaint);
        data.add(map);
        flag_change = true;
//        myCanvas.drawPath(myPath, myPaint);
        //myPath.reset();

    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            myPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            flag_change = false;
        }
    }

    private void touch_start(float x, float y) {
        //myPath.reset();
        myPath = new Path();
        myPath.moveTo(x, y);
        mX = x;
        mY = y;
        flag_change = true;
    }
    //清除整个绘制的图像


    public Bitmap getBitmap() {
        return this.myBitmap;
    }


    public Paint getMyPaint() {
        return myPaint;
    }

    public Path getMyPath() {
        return myPath;
    }

    public void setMyDraw(Path path, Paint paint, Bitmap bitmap) {
//        clear();
        this.myPath = path;
        this.myPaint = paint;
        this.myBitmap = bitmap;
        init();
        invalidate();
    }

    public void clear() {
        //清除方式：重新生成位图
        myBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas(myBitmap);
        myPath.reset();
        data.clear();
        //重新绘制
        invalidate();
    }

    private Integer index = 0;

    public boolean recall() {
        index = data.size() - 1;
        if (!data.isEmpty() && index > 0) {
            data.remove(data.get(index));
            invalidate();
            if (index == 1) {
                return false;
            }
            return true;
        }
        return false;
    }

}
