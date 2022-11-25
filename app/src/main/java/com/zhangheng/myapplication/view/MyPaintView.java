package com.zhangheng.myapplication.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zhangheng.myapplication.R;

/**
 * Created by 张恒 on 2020/11/14 0014.
 */

public class MyPaintView extends View {
    private Resources myResources;
    //画笔，定义绘制属性
    private Paint myPaint;
    private Paint mBitmapPaint;
    //绘制路径
    private Path myPath;
    //画布及其底层位图
    private Bitmap myBitmap;
    private Canvas myCanvas;
    private float mX,mY;
    private static final float TOUCH_TOLERANCE=4;
    //记录宽度和高度
    private int mWidth,mHeight;
    String paintcolor="black";
    int paintsize=20;

    public void setMyPaintColor(String paintcolor){
        this.paintcolor=paintcolor;
        initialize();
    }
    public void setMyPaintSize(int paintsize){
        this.paintsize=paintsize;
        initialize();
    }

    public MyPaintView(Context context) {
        super(context);
        initialize();
    }

    public MyPaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public MyPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    //初始化
      private void initialize() {
        myResources=getResources();
        //绘制自由曲线的画笔
        myPaint=new Paint();
        myPaint.setAntiAlias(true);
        myPaint.setDither(true);
          switch (paintcolor){
              case "red":
                  myPaint.setColor(myResources.getColor(R.color.red));
                  //Log.d("color","red");
                  break;
              case "pink":
                  myPaint.setColor(myResources.getColor(R.color.pink));
                  //Log.d("color","red");
                  break;
              case "orange":
                  myPaint.setColor(myResources.getColor(R.color.orange));
                  break;
              case "yellow":
                  myPaint.setColor(myResources.getColor(R.color.yellow));
                  break;
              case "green":
                  myPaint.setColor(myResources.getColor(R.color.green));
                  //Log.d("color","green");
                  break;
              case "cyan":
                  myPaint.setColor(myResources.getColor(R.color.cyan));
                  break;
              case "skyblue":
                  myPaint.setColor(myResources.getColor(R.color.skyblue));
                  //Log.d("color","red");
                  break;
              case "blue":
                  myPaint.setColor(myResources.getColor(R.color.blue));
                  break;
              case "mediumpurple":
                  myPaint.setColor(myResources.getColor(R.color.mediumpurple));
                  break;
              case "purple":
                  myPaint.setColor(myResources.getColor(R.color.purple));
                  break;

              case "black":
                  myPaint.setColor(myResources.getColor(R.color.black));
                  break;
              case "white":
                  myPaint.setColor(myResources.getColor(R.color.white));
                  break;
          }
        //myPaint.setColor(myResources.getColor(R.color.red));
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setStrokeWidth(paintsize);
        myPath=new Path();
        mBitmapPaint= new Paint(Paint.DITHER_FLAG);

    }
    //画布大小改变时
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //super.onSizeChanged(w, h, oldw, oldh);
        mWidth= w;
        mHeight= h;
        myBitmap= Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        myCanvas=new Canvas(myBitmap);
    }
    //手指触摸屏幕时的各种动作处理

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()){
            //手机开始触摸屏幕时
            case MotionEvent.ACTION_DOWN:
                touch_start(x,y);
                invalidate();
                //Log.d("ontouchenvent","1");
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x,y);
                invalidate();
                //Log.d("ontouchenvent","2");
                break;
            case MotionEvent.ACTION_UP:
                touch_up(x,y);
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
        canvas.drawColor(getResources().getColor(R.color.white));
        //若不调用此方法，绘制结束后画布将清空
        canvas.drawBitmap(myBitmap,0,0,mBitmapPaint);
        //绘制路径
        canvas.drawPath(myPath,myPaint);
    }

    private void touch_up(float x, float y) {
        myPath.lineTo(mX,mY);
        myCanvas.drawPath(myPath,myPaint);
        //myPath.reset();
    }

    private void touch_move(float x, float y) {
        float dx= Math.abs(x-mX);
        float dy= Math.abs(y-mY);
        if (dx>=TOUCH_TOLERANCE||dy>=TOUCH_TOLERANCE){
            myPath.quadTo(mX,mY,(x+mX)/2,(y+mY)/2);
            mX=x;
            mY=y;
        }
    }

    private void touch_start(float x, float y) {
        //myPath.reset();
        myPath.moveTo(x,y);
        mX=x;
        mY=y;
    }
    //清除整个绘制的图像


    public Bitmap getBitmap(){
        return this.myBitmap;
    }
    public void clear() {
        //清除方式：重新生成位图
        myBitmap= Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        myCanvas=new Canvas(myBitmap);
        myPath.reset();
        //重新绘制
        invalidate();
    }

}
