package com.zhangheng.myapplication.activity.setting_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.bean.app.AppLife;
import com.zhangheng.myapplication.getphoneMessage.PhoneSystem;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.EncryptUtil;
import com.zhangheng.util.TimeUtil;

import java.util.Date;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;

public class Setting_AppVerificationCode extends SettingActivity {
    public Setting_AppVerificationCode(Context context) {
        super(context);
    }

    @Override
    public void OnItemClick() {
        appVerificationCode();
    }

    private void appVerificationCode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        int width = dialog.getWindow().getWindowManager().getDefaultDisplay().getWidth();
        EditText pwd = new EditText(context);
        pwd.setBackground(context.getDrawable(R.drawable.btn));
        pwd.setHint("请输入口令秘钥码:");
        pwd.setWidth((int) (width * 0.7));
        pwd.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button btn1 = new Button(context);
        btn1.setText("验证");
        btn1.setBackground(context.getDrawable(R.drawable.btn1));

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
        dialog.setTitle("验证APP口令密钥");
        dialog.setMessage("请输入作者分享的口令密钥");

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = pwd.getText().toString();
                if (!StrUtil.isBlank(s)) {
                    if (s.indexOf(":") == 1) {
                        char flag = s.charAt(0);
                        switch (flag) {
                            case 'm':
                                appRenewal(s);
                                break;
                            default:
                                DialogUtil.dialog(context, "口令错误", "口令格式格式不存在");
                                break;
                        }
                    } else {
                        DialogUtil.dialog(context, "口令错误", "口令格式错误");
                    }
                }
            }
        });
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * APP续期
     * @param s 验证口令
     */
    private void appRenewal(String s) {
        String tap = TimeUtil.toTime(new Date(), "yyyyMMddHH");
        try {
            String myMd5 = EncryptUtil.getMyMd5(tap + PhoneSystem.getVersionCode(context));
            String pwd = StrUtil.subAfter(s, ":", false);
            String base64=null;
            if (pwd.indexOf(":")>0){
                base64=StrUtil.subAfter(pwd,":",false);
                pwd=StrUtil.subBefore(pwd,":",false);
            }
            if (pwd.endsWith(myMd5)) {
                AppLife appLife = new AppLife();
                appLife.setMaxDay(7);
                appLife.setCreateTime(new Date().getTime());
                if (!StrUtil.isBlank(base64)) {
                    if (Base64.isBase64(base64)) {
                        String res = EncryptUtil.deBase64Str(base64);
                        if (res.indexOf("||")>0) {
                            String sign = StrUtil.subAfter(res, "||", true);
                            String indexs = StrUtil.subBefore(res, "||", true);
                            String md5 = EncryptUtil.getMyMd5(indexs);
                            if (md5.equals(sign)) {
                                String[] split = indexs.split(",");
                                Integer[] index=new Integer[split.length];
                                for (int i = 0; i < split.length; i++) {
                                    if (Validator.isNumber(split[i])) {
                                        index[i] = Convert.toInt(split[i]);
                                    }
                                }
                                appLife.setIndex(index);
                            }
                        }
                    }
                }
                setting.setService_life_info(new Gson().toJson(appLife));
                boolean b = setting.setSetting(setting.flag_service_life, true);
                if (b)
                    DialogUtil.dialog(context, "验证成功", "您的APP使用期限续签成功！您可以继续使用了");
                else
                    DialogUtil.dialog(context, "验证失败", "抱歉，您的续签失败，请联系作者！");
            } else {
                DialogUtil.dialog(context, "验证失败", "口令错误或口令已失效");
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.dialog(context, "验证错误", e.getMessage());
        }
    }
    public static void main(String[] args) throws Exception {
        String a="3,5,6,7";
        String myMd51 = EncryptUtil.getMyMd5(a);
        String s = com.zhangheng.util.TimeUtil.toTime(new Date(), "yyyyMMddHH");
        String myMd52 = EncryptUtil.getMyMd5(s+"V23.02.16");
        String b="m:"+myMd52+":"+EncryptUtil.enBase64Str(a+"||"+myMd51);
        System.out.println(b);
    }
    @Override
    protected String makePwd() throws Exception {
        return null;
    }

    @Override
    protected void checkingPwdCallBack() throws Exception {

    }
}
