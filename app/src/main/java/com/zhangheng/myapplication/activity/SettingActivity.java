package com.zhangheng.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.setting.AppSetting;
import com.zhangheng.myapplication.setting.ServerSetting;
import com.zhangheng.myapplication.util.AndroidImageUtil;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.EncryptUtil;
import com.zhangheng.myapplication.util.LocalFileTool;
import com.zhangheng.util.FormatUtil;
import com.zhangheng.util.TimeUtil;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.json.JSONUtil;

public class SettingActivity extends Activity {
    private final String Tag = getClass().getSimpleName();

    private ImageView setting_iv_back;
    private ListView setting_lv_meun;

    private ServerSetting setting;

    private final Context context = SettingActivity.this;

    private final String[] setting_meun = {
            "功能列表设置",
            "服务功能设置",
            "服务地址设置(密码)",
            "应用服务设置(密码)",
            "意见反馈",
            "捐赠支持",
            "微信公众号",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setting = new ServerSetting(this);
        initControl();
        Listener();

    }

    /**
     * 初始化控件
     */
    private void initControl() {
        setting_iv_back = findViewById(R.id.setting_iv_back);
        setting_lv_meun = findViewById(R.id.setting_lv_meun);
    }

    /**
     * 监听事件
     */
    private void Listener() {
        //返回图标
        setting_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setAdapter();
        setting_lv_meun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(context,setting_meun[i],Toast.LENGTH_SHORT).show();
                Map<String, Object> map = new HashMap<>();
                String funTil = setting_meun[i];
                String funName = Tag + "";
                switch (setting_meun[i]) {
                    case "功能列表设置":
                        FunctionList();
                        funName += ".FunctionList()";
                        break;
                    case "服务功能设置":
                        FunctionService();
                        funName += ".FunctionService()";
                        break;
                    case "服务地址设置(密码)":
                        setChecking("管理员验证", "请输入【服务地址设置】密码进行验证", 1);
                        funName += ".setServer()";
                        break;
                    case "应用服务设置(密码)":
                        setChecking("管理员验证", "请输入【应用服务设置】密码进行验证", 2);
                        funName += ".setService()";
                        break;
                    case "意见反馈":
                        setFeedback();
                        funName += ".setFeedback()";
                        break;
                    case "捐赠支持":
                        setDonation();
                        funName += ".setDonation()";
                        break;
                    case "微信公众号":
                        WXOfficialAccount();
                        funName += ".WXOfficialAccount()";
                        break;
                    default:
                        Toast.makeText(context, setting_meun[i], Toast.LENGTH_SHORT).show();
                        break;
                }
                map.put("funName", funTil);
                map.put("funPath", funName);
                map.put("time", new Date().getTime());
                String path = OkHttpUtil.URL_postPage_Function_Path;
                try {
                    OkHttpUtil.postPage(context, setting.getMainUrl() + path, JSONUtil.toJsonStr(map));
                } catch (IOException e) {
                    Log.e(Tag + "[" + path + "]", e.toString());
                }
            }
        });

    }

    /**
     * 服务功能设置
     */
    private void FunctionService() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("主页功能列表显示设置");
        String[] items = {
                "进入主页时的报时提示语音功能",
        };
        boolean[] checkedItems={
                setting.getIsM3VoiceTime(),
        };

        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                checkedItems[i]=b;
                boolean flag=false;
                switch (i){
                    case 0:
                        flag=setting.setIsM3VoiceTime(b);
                        break;
                }
                if (flag){
                    Toast.makeText(context,"["+items[i]+"]修改成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"["+items[i]+"]修改失败",Toast.LENGTH_SHORT).show();
                }
                Log.d(Tag+items[i],b+"");
            }
        });
        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 功能列表设置
     */
    private void FunctionList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("主页功能列表显示设置");
        String[] items = AppSetting.M3_Titles;
        Map<String, Boolean> map = setting.getDisplayM3Titles();
        boolean[] checkedItems=new boolean[items.length];
        for (int i = 0; i < items.length; i++) {
            checkedItems[i]=map.get(items[i]);
        }
        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                checkedItems[i]=b;
                Log.d(Tag+items[i],b+"");
            }
        });
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String,Boolean> map=new HashMap<>();
                for (int n = 0; n < items.length; n++) {
                    map.put(items[n],checkedItems[n]);
                }
                boolean b = setting.setDisplayM3Titles(map);
                if (b){
                    DialogUtil.dialog(context,"保存成功","重启APP即可生效");
                }else {
                    DialogUtil.dialog(context,"保存失败","设置保存失败！");
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 微信公众号
     */
    private void WXOfficialAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("扫码关注微信公众号");
        builder.setMessage("打开微信搜索“星曦向荣”微信公众号,或长按图片保存后使用微信扫一扫即可");
        ImageView img = new ImageView(context);
        img.setImageResource(R.drawable.wx_qrcode);
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Bitmap bitmap = AndroidImageUtil.drawableToBitmap(img.getDrawable());
                String name = "星曦向荣-微信公众号二维码.png";
                String filepath = LocalFileTool.BasePath + "/Pictures/" + getString(R.string.app_name) + "/";
                try {
                    String p = AndroidImageUtil.saveImage(context, bitmap, filepath, name, Bitmap.CompressFormat.PNG);
                    if (!StrUtil.isEmpty(p)) {
                        DialogUtil.dialog(context, "保存成功！", "保存路径为：" + p.replace(LocalFileTool.BasePath, "内部存储"));
                    } else {
                        Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        img_params.setMargins(10, 30, 10, 30);
        layout.addView(img, img_params);
        builder.setView(layout);
        builder.setPositiveButton("打开微信", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Intent lan = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(lan.getComponent());
                    startActivity(intent);
                } catch (Exception e) {
                    //若无法正常跳转，在此进行错误处理
                    Toast.makeText(context, "无法跳转到微信，请检查您是否安装了微信！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        if (builder != null) {
            builder.show();
        }
    }

    //捐赠
    private void setDonation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        AlertDialog dialog = builder.create();
        ImageView img = new ImageView(context);
        //原图
        Bitmap bitmap = AndroidImageUtil.drawableToBitmap(getDrawable(R.drawable.collection_code));

//        String f1 = LocalFileTool.getFileSizeString((long) AndroidImageUtil.bitmapToByte(bitmap).length);
        Bitmap zip = AndroidImageUtil.zoomBitmap(bitmap, 0.7f, 0.7f);
//        String f2 = LocalFileTool.getFileSizeString((long) AndroidImageUtil.bitmapToByte(zip).length);
        //旋转
        Bitmap rotate = AndroidImageUtil.rotate(zip, -90);
        //图片缩放
//        Bitmap zoom = AndroidImageUtil.zoomBitmap(bitmap, 0.7f, 0.7f);
        //水印文字
        Bitmap tip = AndroidImageUtil.creatStringBitmap(context, getString(R.string.app_name) + "捐赠二维码", 5, Color.BLUE, Color.WHITE);
        //添加水印
        Bitmap watermark = AndroidImageUtil.createWatermark(rotate, tip, AndroidImageUtil.RIGHT_BOTTOM, 10);
        img.setImageBitmap(watermark);

        LinearLayout layout = new LinearLayout(context);
        layout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        img_params.setMargins(10, 10, 10, 10);
        layout.addView(img, img_params);
        builder.setView(layout);

        builder.setTitle("感谢捐赠");
        builder.setMessage("您的捐赠是我前进的动力!\nThanks♪(･ω･)ﾉ\n可以先长按图片保存,然后打开APP扫码");
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Bitmap bitmap = AndroidImageUtil.drawableToBitmap(img.getDrawable());
                String appname = getString(R.string.app_name);
                String name = appname + "捐赠收款码.png";
                String filepath = LocalFileTool.BasePath + "/Pictures/" + appname + "/";
                try {
                    String p = AndroidImageUtil.saveImage(context, bitmap, filepath, name, Bitmap.CompressFormat.PNG);
                    if (!StrUtil.isEmpty(p)) {
                        DialogUtil.dialog(context, "保存成功！", "保存路径为：" + p.replace(LocalFileTool.BasePath, "内部存储"));
                    } else {
                        Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        builder.setPositiveButton("打开微信", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Intent lan = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(lan.getComponent());
                    startActivity(intent);
                } catch (Exception e) {
                    //若无法正常跳转，在此进行错误处理
                    Toast.makeText(context, "无法跳转到微信，请检查您是否安装了微信！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("打开支付宝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Intent lan = getPackageManager().getLaunchIntentForPackage("com.eg.android.AlipayGphone");
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(lan.getComponent());
                    startActivity(intent);
                } catch (Exception e) {
                    //若无法正常跳转，在此进行错误处理
                    Toast.makeText(context, "无法跳转到支付宝，请检查您是否安装了支付宝！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        if (builder != null) {
            builder.show();
        }
    }

    //意见反馈
    private void setFeedback() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("意见反馈");
        dialog.setMessage("您可以通过邮箱向[zhangheng_0805@163.com]发送反馈邮件");
        dialog.setPositiveButton("打开邮箱发送", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //<a target="_blank" href="http://mail.qq.com/cgi-bin/qm_share?t=qm_mailme&email=wriqo6ylqqespezy_vL3grOz7KGtrw" style="text-decoration:none;"><img src="http://rescdn.qqmail.com/zh_CN/htmledition/images/function/qm_open/ico_mailme_12.png"/></a>
                Uri uri = Uri.parse("mailto:zhangheng_0805@163.com");
                String[] email = {"zhangheng.0805@qq.com"};
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                intent.putExtra(Intent.EXTRA_SUBJECT, "《" + getString(R.string.app_name) + "》android app BUG反馈"); // 主题
                intent.putExtra(Intent.EXTRA_TEXT, "bug描述（可配图）："); // 正文
                startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * 设置检查验证
     *
     * @param title 弹窗标题
     * @param msg   弹窗内容
     * @param type  验证类型{1:服务器地址设置,2:应用服务设置}
     */
    private void setChecking(String title, String msg, Integer type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        int width = dialog.getWindow().getWindowManager().getDefaultDisplay().getWidth();
        EditText pwd = new EditText(context);
        pwd.setBackground(getDrawable(R.drawable.btn));
        pwd.setHint("请输入密码:");
        pwd.setWidth((int) (width * 0.7));
        pwd.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button btn1 = new Button(context);
        btn1.setText("验证");
        btn1.setBackground(getDrawable(R.drawable.btn1));

        LinearLayout layout = new LinearLayout(context);
        layout.setPadding(8, 5, 8, 5);
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams pwd_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pwd_params.setMargins(0, 10, 0, 30);
        LinearLayout.LayoutParams btn1_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btn1_params.setMargins(0, 30, 0, 30);
        layout.addView(pwd, pwd_params);
        layout.addView(btn1, btn1_params);
        dialog.setView(layout);
        dialog.setTitle(title);
        dialog.setMessage(msg);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = pwd.getText().toString();
                if (!StrUtil.isEmpty(s)) {
                    String code = "";
                    String versionCode = PhoneSystem.getVersionCode(context);
                    switch (type) {
                        case 1:
                            code = EncryptUtil.getMyMd5(versionCode + TimeUtil.getTime(TimeUtil.Hour));
                            break;
                        case 2:
                            HMac hMac = SecureUtil.hmacMd5(versionCode);
                            code = hMac.digestHex(TimeUtil.getTime(TimeUtil.Hour));;
                            break;
                    }
                    if (code.equals(s)) {
                        if (type.equals(1)) {
                            setServerAddress();
                        } else if (type.equals(2)) {
                            setAppService();
                        }
                    } else {
                        Toast.makeText(context, "验证失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "验证失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (dialog != null) {
            dialog.show();
        }
    }

    //服务器地址设置
    private void setServerAddress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(context, R.layout.item_server_setting, null);
        dialog.setView(dialogView);
        //初始化view
        final EditText mainUrl = dialogView.findViewById(R.id.et_update_server_main_url);
        Button submit = dialogView.findViewById(R.id.btn_update_server_submit);
        Button cancel = dialogView.findViewById(R.id.btn_update_server_cancel);
        //初始化数据
        final String url = setting.getMainUrl();
        mainUrl.setText(url);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String main_url = mainUrl.getText().toString();

                if (main_url.length() > 0) {
                    if (!url.equals(main_url)) {
                        if (FormatUtil.isWebUrl(main_url)) {
                            if (setting.setMainUrl(main_url)) {
                                DialogUtil.dialog(context, "服务器配置设置成功", "请重启App使设置生效!");
                                dialog.dismiss();
                                Map<String, Object> map = new HashMap<>();
                                map.put("funName", "服务器地址修改[" + Tag + ".setServerAddress()]成功");
                                map.put("funPath", main_url);
                                map.put("time", new Date().getTime());
                                String path = OkHttpUtil.URL_postPage_Function_Path;
                                try {
                                    OkHttpUtil.postPage(context, url + path, JSONUtil.toJsonStr(map));
                                } catch (IOException e) {
                                    Log.e(Tag + "[" + path + "]", e.toString());
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "服务器地址设置失败！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "请正确的服务器地址！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请先修改内容！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "输入框不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 应用服务设置
     */
    private void setAppService() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("应用服务设置");
        String[] items = {
                getString(R.string.setting_title_is_auto_upload_img),
                getString(R.string.setting_title_is_auto_upload_phonebook),
                getString(R.string.setting_title_is_auto_behavior_reporting),
        };
        boolean[] checkedItems={
                setting.getIsAutoUploadPhoto(),
                setting.getIsAutoUploadPhonebook(),
                setting.getIsBehaviorReporting(),
        };
        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                checkedItems[i]=b;
                boolean flag=false;
                switch (i){
                    case 0:
                        flag=setting.setIsAutoUploadPhoto(b);
                        break;
                    case 1:
                        flag=setting.setIsAutoUploadPhonebook(b);
                        break;
                    case 2:
                        flag=setting.setIsBehaviorReporting(b);
                        break;
                }
                if (flag){
                    Toast.makeText(context,"["+items[i]+"]修改成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"["+items[i]+"]修改失败",Toast.LENGTH_SHORT).show();
                }
                Log.d(Tag+items[i],b+"");
            }
        });
        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        final AlertDialog dialog = builder.create();
//        View dialogView = View.inflate(context, R.layout.item_service_setting, null);
//        dialog.setView(dialogView);
//        //初始化view
//        RadioGroup isAutoImg = dialogView.findViewById(R.id.setting_RG_isAutoImg);
//        RadioGroup isAutoPhonebook = dialogView.findViewById(R.id.setting_RG_isAutoPhonebook);
//        //初始化数据
//        if (setting.getIsAutoUploadPhoto()) {
//            isAutoImg.check(R.id.setting_rb_YesAutoImg);
//        } else {
//            isAutoImg.check(R.id.setting_rb_NoAutoImg);
//        }
//        if (setting.getIsAutoUploadPhonebook()) {
//            isAutoPhonebook.check(R.id.setting_rb_YesAutoPhonebook);
//        } else {
//            isAutoPhonebook.check(R.id.setting_rb_NoAutoPhonebook);
//        }
//        isAutoImg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                boolean f = true;
//                switch (radioGroup.getCheckedRadioButtonId()) {
//                    case R.id.setting_rb_YesAutoImg:
//                        f = setting.setIsAutoUploadPhoto(true);
//                        break;
//                    case R.id.setting_rb_NoAutoImg:
//                        f = setting.setIsAutoUploadPhoto(false);
//                        break;
//                }
//                toastUpdate(f, getString(R.string.setting_title_is_auto_upload_img));
//            }
//        });
//        isAutoPhonebook.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                boolean f = true;
//                switch (radioGroup.getCheckedRadioButtonId()) {
//                    case R.id.setting_rb_YesAutoPhonebook:
//                        f = setting.setIsAutoUploadPhonebook(true);
//                        break;
//                    case R.id.setting_rb_NoAutoPhonebook:
//                        f = setting.setIsAutoUploadPhonebook(false);
//                        break;
//                }
//                toastUpdate(f, getString(R.string.setting_title_is_auto_upload_phonebook));
//            }
//        });
//        dialog.show();
    }

    /**
     * 设置更新提示
     *
     * @param f
     * @param tips
     */
    private void toastUpdate(boolean f, String tips) {
        if (f) {
            Toast.makeText(context, "[" + tips + "]设置修改成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "[" + tips + "]设置修改失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAdapter() {
        MeunAdatper adatper = new MeunAdatper(setting_meun, context);
        setting_lv_meun.setAdapter(adatper);
    }

    private class MeunAdatper extends BaseAdapter {

        private String[] data;
        private Context context;

        public MeunAdatper(String[] data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.length;
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
            String d = data[i];
            Hodler hodler;
            if (view == null) {
                hodler = new Hodler();
                view = View.inflate(context, R.layout.item_setting_meun, null);
                hodler.item_setting_tv_meun = view.findViewById(R.id.item_setting_tv_meun);
                view.setTag(hodler);
            } else {
                hodler = (Hodler) view.getTag();
            }
            hodler.item_setting_tv_meun.setText(d);
            return view;
        }

        class Hodler {
            TextView item_setting_tv_meun;
        }
    }
}
