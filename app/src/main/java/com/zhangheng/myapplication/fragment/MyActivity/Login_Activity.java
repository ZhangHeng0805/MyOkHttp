package com.zhangheng.myapplication.fragment.MyActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zhangheng.myapplication.Object.Resuilt;
import com.zhangheng.myapplication.Object.User;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.bean.shop.Customer;
import com.zhangheng.myapplication.bean.shop.UserCustomer;
import com.zhangheng.myapplication.fragment.MyFragment;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.PhoneNumUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class Login_Activity extends Activity {
    private static final String TAG=Login_Activity.class.getSimpleName();
    private TextView m15_myfragment_login_txt_zhuce;
    private EditText m15_myfragment_login_et_username,m15_myfragment_login_et_password;
    private Button m15_myfragment_login_btn_submit;
    private ImageView login_iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m15_myfragment_activity_login);
        m15_myfragment_login_btn_submit=findViewById(R.id.m15_myfragment_login_btn_submit);
        m15_myfragment_login_txt_zhuce=findViewById(R.id.m15_myfragment_login_txt_zhuce);
        m15_myfragment_login_et_username=findViewById(R.id.m15_myfragment_login_et_username);
        m15_myfragment_login_et_password=findViewById(R.id.m15_myfragment_login_et_password);
        login_iv_back=findViewById(R.id.login_iv_back);
        login_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        m15_myfragment_login_txt_zhuce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Registered_Activity.class);
                startActivity(intent);
            }
        });
        m15_myfragment_login_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone=m15_myfragment_login_et_username.getText().toString();
                String password=m15_myfragment_login_et_password.getText().toString();
                if (phone.length()==11){
                    if (PhoneNumUtil.isMobile(phone)) {
                        if (password.length() >= 6 && password.length() <= 18) {
                            UserCustomer user = new UserCustomer();
                            user.setUsername(phone);
                            user.setPassword(password);
//                            Log.d(TAG, "onClick: " + user.toString());
                            submit(user);
                        } else {
                            dialog("输入错误", "密码不能为空，且限制6~18个字符以内");
                        }
                    }else {
                        dialog("输入错误", "非法手机号，请输入正确的手机号码");
                    }
                }else {
                    dialog("输入错误","请输入11位格式的手机号");
                }
            }
        });
    }
    private void submit(final UserCustomer user){
        final ProgressDialog progressDialog=new ProgressDialog(Login_Activity.this);
        progressDialog.setMessage("刷新中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=getResources().getString(R.string.zhangheng_url)+"Customer/Login";
        Gson gson = new Gson();
        String json = gson.toJson(user);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("CustomerLogin",json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        dialog("登录错误",error);

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson1 = new Gson();
                        Resuilt resuilt = new Resuilt();
                        try {
                            resuilt = gson1.fromJson(response, Resuilt.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                dialog("错误",e.getMessage());
                            }else {
                                dialog("错误",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (resuilt!=null){
                            if (resuilt.getTitle().equals("登录成功")){
                                SharedPreferences sharedPreferences = getSharedPreferences("customeruser", MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("phone",user.getUsername());
                                editor.putString("name",resuilt.getMessage());
                                editor.putString("password",user.getPassword());
                                editor.commit();
                                finish();
                            }else {
                                dialog(resuilt.getTitle(),resuilt.getMessage());
                            }
                        }else {
                            AlertDialog.Builder builder=new AlertDialog.Builder(Login_Activity.this);
                            builder.setCancelable(false);
                            builder.setTitle("加载失败");
                            builder.setMessage("无法进行登录");
                            builder.setPositiveButton("退出",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                            builder.create().show();
                        }


                    }
                });

    }
    private void dialog(String title,String message){
        AlertDialog.Builder d=new AlertDialog.Builder(Login_Activity.this);
        d.setTitle(title);
        d.setMessage(message);
        d.create().show();
    }
}
