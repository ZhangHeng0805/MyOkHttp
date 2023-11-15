package com.zhangheng.myapplication.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;
import com.zhangheng.myapplication.permissions.ReadAndWrite;
import com.zhangheng.myapplication.util.AndroidImageUtil;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.DisplyUtil;
import com.zhangheng.myapplication.util.LocalFileTool;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.QRCode.AndroidQRCodeUtil;
import com.zhangheng.util.TimeUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import okhttp3.Call;

public class Main8Activity extends AppCompatActivity {
    private EditText m8_et_message;
    private Button m8_btn_submit, m8_btn_save;
    private int Width;
    private ImageView m8_image;

    private RadioGroup m8_RG_select;

    private final Context context = Main8Activity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main8);
        Width = (int) (DisplyUtil.getScreenWidth(context) * 0.9);
        m8_et_message = findViewById(R.id.m8_et_message);
        m8_btn_submit = findViewById(R.id.m8_btn_submit);
        m8_image = findViewById(R.id.m8_image);
        m8_btn_save = findViewById(R.id.m8_btn_save);
        //长按监听

        m8_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                new AlertDialog.Builder(context)
                        .setTitle("识别二维码")
                        .setPositiveButton("识别", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String text = distinguishQRByLocal();
                                DialogUtil.dialog(context, "识别结果", text);
                            }
                        })
                        .setMessage("是否识别图中二维码？").show();

                return true;
            }
        });
        m8_RG_select = findViewById(R.id.m8_RG_select);
        //生成点击监听
        m8_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = m8_et_message.getText().toString();
                m8_btn_save.setVisibility(View.GONE);
                if (!StrUtil.isEmptyIfStr(text)) {
                    int checkedBtnId = m8_RG_select.getCheckedRadioButtonId();
                    if (checkedBtnId == R.id.m8_rb_net) {
                        try {
                            getQRImageByNet(text);
                            m8_btn_save.setVisibility(View.VISIBLE);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else if (checkedBtnId == R.id.m8_rb_local) {
                        getQRImageByLocal(text);
                        m8_btn_save.setVisibility(View.VISIBLE);
                    }
                } else {
                    DialogUtil.dialog(context, "输入错误", "生成内容不能为空");
                }
            }
        });
        //点击保存
        m8_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = ReadAndWrite.RequestPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);//写入权限
                if (b) {
                    Drawable drawable = m8_image.getDrawable();
                    if (drawable != null) {
                        String appname = getString(R.string.app_name);
                        Bitmap bitmap = AndroidImageUtil.drawableToBitmap(drawable);
                        String contents = "@"+appname + "_" + PhoneSystem.getVersionCode(context);
                        Bitmap stringBitmap = AndroidImageUtil.creatStringBitmap(context,
                                contents,
                                4, Color.BLACK, Color.WHITE);
                        Bitmap watermark = AndroidImageUtil.createWatermark(bitmap, stringBitmap, AndroidImageUtil.RIGHT_BOTTOM, 2);
                        String path = LocalFileTool.BasePath + "/Pictures/" + appname + "/"+appname+"二维码生成/";
                        String timetips = TimeUtil.toTime(new Date(), "yyyyMMdd-HHmmssSSS");
                        String name = "二维码" + timetips + ".png";
                        String s = AndroidImageUtil.saveImage(context, watermark, path, name, Bitmap.CompressFormat.PNG);
                        if (!StrUtil.isEmpty(s)) {
                            DialogUtil.dialog(context, "二维码保存成功！", "保存路径为：" + s.replace(LocalFileTool.BasePath, "内部存储"));
                        } else {
                            Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "请先生成二维码", Toast.LENGTH_SHORT).show();
                        m8_btn_save.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(Main8Activity.this, "没有打开文件写入权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 识别本地二维码
     *
     * @return
     */
    private String distinguishQRByLocal() {
        Bitmap bitmap = ((BitmapDrawable) (m8_image.getDrawable())).getBitmap();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] data = new int[width * height];
        bitmap.getPixels(data, 0, width, 0, 0, width, height);
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, data);
        BinaryBitmap bitmaps = new BinaryBitmap(new HybridBinarizer(source));
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8"); // 字符转码格式设置
        try {
            String text = new MultiFormatReader().decode(bitmaps, hints).getText();
            return text;
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 本地生成二维码
     *
     * @param text
     */
    private void getQRImageByLocal(String text) {
        try {
//            int width = (int) (Width * 0.95);
            Bitmap qrCodeBitmap = AndroidQRCodeUtil.createQRCodeBitmap(text, Width, Width);
            m8_image.setImageBitmap(qrCodeBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 通过网络生成
     *
     * @param text
     */
    private void getQRImageByNet(String text) throws UnsupportedEncodingException {

        String url = getString(R.string.qrcode_juhe_url);
        String key = getString(R.string.key_QRImage);
//        int width = DisplyUtil.getScreenWidth(this);
//        int w = (int) (width * 0.9);
        String W = String.valueOf(Width);
        Map<String, String> param = new HashMap<>();
        Map<String, Integer> param1 = new HashMap<>();
        param.put("key", key);
        param.put("text", text);//二维码内容
        param.put("el", "h");//纠错等级，el可用值：h\q\m\l，例如：h
        param.put("bgcolor", null);//背景色代码，例如：ffffff
        param.put("fgcolor", null);//前景色代码，例如：000000
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.logo);
//        String s = "data:image/png;base64," + EncryptUtil.enBase64(AndroidImageUtil.bitmapToByte(bitmap));
//        String encode = URLEncoder.encode(s, "UTF-8");
        param.put("logo", null);//logo图片URL地址或base64encode编码的图片内容，需要urlencode
        param.put("w", W);//尺寸大小（像素），例如：300
        int M= (int) (Width*0.05);
        param.put("m", String.valueOf(M));//边距大小（像素），例如：10
        param.put("lw", null);//logo宽度（像素），例如：60
        param.put("type", "2");//返回模式，1:二维码图片以base64encode编码返回 2:直接返回二维码图像，默认1

        final boolean b = ReadAndWrite.RequestPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);//写入权限
        if (b) {
            DialogUtil dialogUtil = new DialogUtil(context);
            dialogUtil.createProgressDialog();
            OkHttpUtils
                    .get()
                    .url(url)
                    .params(param)
                    .addHeader("Content-Type","application/x-www-form-urlencoded")
                    .build()
                    .execute(new BitmapCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            e.printStackTrace();
                            dialogUtil.closeProgressDialog();
                            Toast.makeText(Main8Activity.this, "错误" + OkHttpMessageUtil.error(e), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(final Bitmap response, int id) {
                            m8_image.setImageBitmap(response);
                            dialogUtil.closeProgressDialog();
                        }

                    });
        } else {
            Toast.makeText(Main8Activity.this, "没有打开文件写入权限", Toast.LENGTH_SHORT).show();
        }
    }

}
