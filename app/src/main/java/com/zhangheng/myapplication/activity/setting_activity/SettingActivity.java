package com.zhangheng.myapplication.activity.setting_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.setting.ServerSetting;

import cn.hutool.core.util.StrUtil;

import static android.app.AlertDialog.Builder;

public abstract class SettingActivity {
    protected Context context;
    protected String Tag;
    protected ServerSetting setting;


    public SettingActivity(Context context) {
        this.context = context;
        Tag=context.getClass().getSimpleName();
        setting = new ServerSetting(context);
    }

    public abstract void OnItemClick();

    /**
     * 设置检查验证密码弹窗
     *
     * @param title 弹窗标题
     * @param msg   弹窗内容
     */
    protected void checkingPwdDialog(String title, String msg) {

        Builder builder = new Builder(context);
        AlertDialog dialog = builder.create();
        int width = dialog.getWindow().getWindowManager().getDefaultDisplay().getWidth();
        EditText pwd = new EditText(context);
        pwd.setBackground(context.getDrawable(R.drawable.btn));
        pwd.setHint("请输入密码:");
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
        dialog.setTitle(title);
        dialog.setMessage(msg);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = pwd.getText().toString();
                if (!StrUtil.isEmpty(s)) {
                    String code = "";
                    try {
                        code = makePwd();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (code!=null&&code.equals(s)) {
                        try {
                            checkingPwdCallBack();
                        } catch (Exception e) {
                            e.printStackTrace();
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

    /**
     *
     * 重写各自的加密方法
     * @return
     * @throws Exception
     */
    protected abstract String makePwd() throws Exception;

    /**
     * 验证密码成功后的回调方法
     * @throws Exception
     */
    protected abstract void checkingPwdCallBack() throws Exception;

}
