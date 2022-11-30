package com.zhangheng.myapplication.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static android.graphics.Bitmap.createBitmap;

public class AndroidImageUtil {
    /**
     * 图片的八个位置
     **/
    public static final int TOP = 0;      //上
    public static final int BOTTOM = 1;      //下
    public static final int LEFT = 2;      //左
    public static final int RIGHT = 3;      //右
    public static final int LEFT_TOP = 4;    //左上
    public static final int LEFT_BOTTOM = 5;  //左下
    public static final int RIGHT_TOP = 6;    //右上
    public static final int RIGHT_BOTTOM = 7;  //右下

    /**
     * 图像的放大缩小方法
     *
     * @param src    源位图对象
     * @param scaleX 宽度比例系数
     * @param scaleY 高度比例系数
     * @return 返回位图对象
     */
    public static Bitmap zoomBitmap(Bitmap src, float scaleX, float scaleY) {
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        Bitmap t_bitmap = createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return t_bitmap;
    }

    /**
     * 图像放大缩小--根据宽度和高度
     *
     * @param src
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBimtap(Bitmap src, int width, int height) {
        return Bitmap.createScaledBitmap(src, width, height, true);
    }

    /**
     * 将view装换为Bitmap
     * @param view
     * @return
     */
    public static Bitmap createViewBitmap(View view){
        if (view == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(screenshot);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        return screenshot;
    }
    /**
     * 根据文字创建一个 bitmap，
     *
     * @param contents
     * @param context
     * @param textSize
     * @param textColor
     * @param backgroundColor
     * @return
     */
    public static Bitmap creatStringBitmap(Context context, String contents, int textSize, @ColorInt int textColor,@ColorInt int backgroundColor) {
        float scale=context.getResources().getDisplayMetrics().scaledDensity;
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setTextSize(scale*textSize);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextColor(textColor);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
        tv.setBackgroundColor(backgroundColor);
        if (tv == null) {
            return null;
        }
        return createViewBitmap(tv);
    }
    /**
     * 将Drawable转为Bitmap对象
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }


    /**
     * 将Bitmap转换为Drawable对象
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    /**
     * Bitmap转byte[]
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out.toByteArray();
    }

    /**
     * byte[]转Bitmap
     *
     * @param data
     * @return
     */
    public static Bitmap byteToBitmap(byte[] data) {
        if (data.length != 0) {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return null;
    }

    /**
     * 绘制带圆角的图像
     *
     * @param src
     * @param radius
     * @return
     */
    public static Bitmap createRoundedCornerBitmap(Bitmap src, int radius) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        // 高清量32位图
        Bitmap bitmap = createBitmap(w, h, Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xff424242);
        // 防止边缘的锯齿
        paint.setFilterBitmap(true);
        Rect rect = new Rect(0, 0, w, h);
        RectF rectf = new RectF(rect);
        // 绘制带圆角的矩形
        canvas.drawRoundRect(rectf, radius, radius, paint);

        // 取两层绘制交集，显示上层
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 绘制图像
        canvas.drawBitmap(src, rect, rect, paint);
        return bitmap;
    }

    /**
     * 创建选中带提示图片
     *
     * @param context
     * @param srcId
     * @param tipId
     * @return
     */
    public static Bitmap createSelectedTip(Context context, int srcId, int tipId) {
        Bitmap src = BitmapFactory.decodeResource(context.getResources(), srcId);
        Bitmap tip = BitmapFactory.decodeResource(context.getResources(), tipId);
        return createSelectedTip(src,tip);
    }
    public static Bitmap createSelectedTip(Bitmap src, Bitmap tip) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap bitmap = createBitmap(w, h, Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        //绘制原图
        Rect rect=new Rect(0,0,w,h);
        canvas.drawBitmap(src, rect, rect, paint);
        //绘制提示图片
        canvas.drawBitmap(tip, (w-tip.getWidth()), 0, paint);
        return bitmap;
    }

    /**
     * 带倒影的图像
     *
     * @param src
     * @return
     */
    public static Bitmap createReflectionBitmap(Bitmap src) {
        // 两个图像间的空隙
        final int spacing = 4;
        final int w = src.getWidth();
        final int h = src.getHeight();
        // 绘制高质量32位图
        Bitmap bitmap = createBitmap(w, h + h / 2 + spacing, Config.ARGB_8888);
        // 创建燕X轴的倒影图像
        Matrix m = new Matrix();
        m.setScale(1, -1);
        Bitmap t_bitmap = createBitmap(src, 0, h / 2, w, h / 2, m, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        //  绘制原图像
        canvas.drawBitmap(src, 0, 0, paint);
        // 绘制倒影图像
        canvas.drawBitmap(t_bitmap, 0, h + spacing, paint);
        // 线性渲染-沿Y轴高到低渲染
        Shader shader = new LinearGradient(0, h + spacing, 0, h + spacing + h / 2, 0x70ffffff, 0x00ffffff, Shader.TileMode.MIRROR);
        paint.setShader(shader);
        // 取两层绘制交集，显示下层。
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // 绘制渲染倒影的矩形
        canvas.drawRect(0, h + spacing, w, h + h / 2 + spacing, paint);
        return bitmap;
    }


    /**
     * 独立的倒影图像
     *
     * @param src
     * @return
     */
    public static Bitmap createReflectionBitmapForSingle(Bitmap src) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        // 绘制高质量32位图
        Bitmap bitmap = createBitmap(w, h / 2, Config.ARGB_8888);
        // 创建沿X轴的倒影图像
        Matrix m = new Matrix();
        m.setScale(1, -1);
        Bitmap t_bitmap = createBitmap(src, 0, h / 2, w, h / 2, m, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        // 绘制倒影图像
        canvas.drawBitmap(t_bitmap, 0, 0, paint);
        // 线性渲染-沿Y轴高到低渲染
        Shader shader = new LinearGradient(0, 0, 0, h / 2, 0x70ffffff,
                0x00ffffff, Shader.TileMode.MIRROR);
        paint.setShader(shader);
        // 取两层绘制交集。显示下层。
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // 绘制渲染倒影的矩形
        canvas.drawRect(0, 0, w, h / 2, paint);
        return bitmap;
    }

    /**
     * 图片灰化
     *
     * @param src
     * @return
     */
    public static Bitmap createGreyBitmap(Bitmap src) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        Bitmap bitmap = createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        // 颜色变换的矩阵
        ColorMatrix matrix = new ColorMatrix();
        // saturation 饱和度值，最小可设为0，此时对应的是灰度图；为1表示饱和度不变，设置大于1，就显示过饱和
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(src, 0, 0, paint);
        return bitmap;
    }

    /**
     * 保存图片
     *
     * @param src 图片源
     * @param filepath 保存路径
     * @param filename 文件名
     * @param format:[Bitmap.CompressFormat.PNG,Bitmap.CompressFormat.JPEG]
     * @return 保存成功返回保存路径，否则返回null
     */
    public static String saveImage(Context context, Bitmap src, String filepath, String filename, CompressFormat format) {
        String rs = null;
        File filePath = new File(filepath);
        if (!filePath.exists() && !filePath.isDirectory()) {
            filePath.mkdirs();
        }
        File file = new File(filepath, filename);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (src.compress(format, 100, out)) {
                out.flush();  //写入流
                rs = file.getAbsolutePath();
            }
            out.close();
            // 其次把文件插入到系统图库
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    file.getAbsolutePath(), file.getName(), null);
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * 添加水印效果
     *
     * @param src       源位图
     * @param watermark 水印
     * @param direction 方向 LEFT_TOP,LEFT_BOTTOM,RIGHT_TOP,RIGHT_BOTTOM
     * @param spacing   间距
     * @return
     */
    public static Bitmap createWatermark(Bitmap src, Bitmap watermark, int direction, int spacing) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        Bitmap bitmap = createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect rect=new Rect(0,0,w,h);
        canvas.drawBitmap(src, rect, rect, null);
        if (direction == LEFT_TOP) {
            canvas.drawBitmap(watermark, spacing, spacing, null);
        } else if (direction == LEFT_BOTTOM) {
            canvas.drawBitmap(watermark, spacing, h - watermark.getHeight() - spacing, null);
        } else if (direction == RIGHT_TOP) {
            canvas.drawBitmap(watermark, w - watermark.getWidth() - spacing, spacing, null);
        } else if (direction == RIGHT_BOTTOM) {
            canvas.drawBitmap(watermark, w - watermark.getWidth() - spacing, h - watermark.getHeight() - spacing, null);
        }
        return bitmap;
    }

    /**
     * 压缩大小
     * @param src
     * @param size 压缩大小 值1为原图。值越大，压缩图片越小[1-20]
     * @return
     */
    public static Bitmap zip(Bitmap src,int size){
        if (size>=1&&size<=20) {
            byte[] bytes = bitmapToByte(src);
            BitmapFactory.Options ontain = new BitmapFactory.Options();
            ontain.inSampleSize = size;//值1为原图。值越大，压缩图片越小1-20
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, ontain);
        }else {
            return src;
        }
    }

    /**
     * 旋转
     * @param src
     * @param angle 旋转度数
     * @return
     */
    public static Bitmap rotate(Bitmap src, float angle) {
        if (angle != 0 && src != null) {
            Matrix m = new Matrix();
            m.setRotate(angle, (float) src.getWidth() / 2,
                    (float) src.getHeight() / 2);

            Bitmap b2 = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                    src.getHeight(), m, true);
            if (src != b2) {
                src.recycle();
                src = b2;
            }
        }
        return src;
    }

    /**
     * 合成图像
     *
     * @param direction
     * @param bitmaps
     * @return
     */
    public static Bitmap composeBitmap(int direction, Bitmap... bitmaps) {
        if (bitmaps.length < 2) {
            return null;
        }
        Bitmap firstBitmap = bitmaps[0];
        for (int i = 0; i < bitmaps.length; i++) {
            firstBitmap = composeBitmap(firstBitmap, bitmaps[i], direction);
        }
        return firstBitmap;
    }

    /**
     * 合成两张图像
     *
     * @param firstBitmap
     * @param secondBitmap
     * @param direction
     * @return
     */
    private static Bitmap composeBitmap(Bitmap firstBitmap, Bitmap secondBitmap,
                                        int direction) {
        if (firstBitmap == null) {
            return null;
        }
        if (secondBitmap == null) {
            return firstBitmap;
        }
        final int fw = firstBitmap.getWidth();
        final int fh = firstBitmap.getHeight();
        final int sw = secondBitmap.getWidth();
        final int sh = secondBitmap.getHeight();
        Bitmap bitmap = null;
        Canvas canvas = null;
        if (direction == TOP) {
            bitmap = createBitmap(sw > fw ? sw : fw, fh + sh, Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            canvas.drawBitmap(secondBitmap, 0, 0, null);
            canvas.drawBitmap(firstBitmap, 0, sh, null);
        } else if (direction == BOTTOM) {
            bitmap = createBitmap(fw > sw ? fw : sw, fh + sh, Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            canvas.drawBitmap(firstBitmap, 0, 0, null);
            canvas.drawBitmap(secondBitmap, 0, fh, null);
        } else if (direction == LEFT) {
            bitmap = createBitmap(fw + sw, sh > fh ? sh : fh, Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            canvas.drawBitmap(secondBitmap, 0, 0, null);
            canvas.drawBitmap(firstBitmap, sw, 0, null);
        } else if (direction == RIGHT) {
            bitmap = createBitmap(fw + sw, fh > sh ? fh : sh,
                    Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            canvas.drawBitmap(firstBitmap, 0, 0, null);
            canvas.drawBitmap(secondBitmap, fw, 0, null);
        }
        return bitmap;
    }
}
