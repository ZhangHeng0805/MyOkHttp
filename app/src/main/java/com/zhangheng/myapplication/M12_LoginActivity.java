package com.zhangheng.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.zhangheng.myapplication.Object.Flag;
import com.zhangheng.myapplication.Object.Resuilt;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class M12_LoginActivity extends AppCompatActivity {
    private String name,pwd;
    private Button btn_zhuce,btn_login;
    private EditText et2,et2_pwd,et2_pwd_true;
    private EditText et1,et1_pwd;
    private TextView tv2_notic,tv_wangji,tv_zhuce,tv_zhuxiao,iv_login,tv1_notic;
    private ImageView logo;
    private CheckBox rb_jizhu;
    final int NOTIFYID=1;
    private boolean open=false;
    private SlidingDrawer sd_1;
    private SharedPreferences sharedPreferences,sharedPreferences1,sharedPreferences2;
    private ProgressDialog progressDialog,progressDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m12_login);

        btn_zhuce= (Button) findViewById(R.id.bnt_zhuce);
        btn_login= (Button) findViewById(R.id.bnt_login);
        et2= (EditText) findViewById(R.id.et2);
        et2_pwd= (EditText) findViewById(R.id.et2_pwd);
        et2_pwd_true= (EditText) findViewById(R.id.et2_pwd_true);
        tv2_notic= (TextView) findViewById(R.id.tv2_notic);
        et1= (EditText) findViewById(R.id.et1);
        et1_pwd= (EditText) findViewById(R.id.et1_pwd);
        sd_1= (SlidingDrawer) findViewById(R.id.sd_1);
        tv_wangji= (TextView) findViewById(R.id.tv_wangji);
        tv_zhuce= (TextView) findViewById(R.id.tv_zhuce);
        tv_zhuxiao= (TextView) findViewById(R.id.tv_zhuxiao);
        logo= (ImageView) findViewById(R.id.logo);
        iv_login= (TextView) findViewById(R.id.iv_login);
        rb_jizhu= (CheckBox) findViewById(R.id.rb_jizhu);
        tv1_notic=findViewById(R.id.tv1_notic);
        setOnClickListener();
        yanzheng();
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("???????????????????????????????????????");
        progressDialog.setIndeterminate(true);// ??????????????????????????????  true?????????????????????????????????????????????  false ????????????????????????
        progressDialog.setCancelable(false);//?????????????????????dialog??????????????????dialog  true?????????????????? false??????????????????
        progressDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences2=getSharedPreferences("Check",MODE_PRIVATE);
        String check=sharedPreferences2.getString("check","0");
        //Log.d("check2",check);
        if ("1".equals(check)){
            sharedPreferences1=getSharedPreferences("myuser",MODE_PRIVATE);
            et1.setText(sharedPreferences1.getString("myname",""));
            et1_pwd.setText(sharedPreferences1.getString("mypwd",""));
            rb_jizhu.setChecked(true);
            //Log.d("check1","1");
        }else if ("0".equals(check)){
            rb_jizhu.setChecked(false);
            //Log.d("check1","0");
        }
    }

    public void setOnClickListener(){
        OnClick onClick=new OnClick();
        btn_zhuce.setOnClickListener(onClick);
        btn_login.setOnClickListener(onClick);
        tv_wangji.setOnClickListener(onClick);
        tv_zhuce.setOnClickListener(onClick);
        tv_zhuxiao.setOnClickListener(onClick);
        logo.setOnClickListener(onClick);
        iv_login.setOnClickListener(onClick);
    }
    private class OnClick implements View.OnClickListener{
        public void onClick(View v){
            switch (v.getId()){

                case R.id.bnt_zhuce:
                    if(et2.getText().toString().trim().length()>=1&&et2.getText().toString().trim().length()<=18){
                        if (et2_pwd.getText().toString().trim().length()>=6
                                &&et2_pwd.getText().toString().trim().length()<=18) {
                            if (et2_pwd.getText().toString().equals(et2_pwd_true.getText().toString())) {
                                et1.setText(et2.getText().toString().trim());
                                //et1_pwd.setText(et2_pwd_true.getText().toString());
                                zhuCe(et2.getText().toString().trim(), et2_pwd_true.getText().toString().trim());
                                Toast.makeText(M12_LoginActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                                et2_pwd.setText("");
                                et2_pwd_true.setText("");
                            } else {
                                tv2_notic.setText("?????????????????????????????????????????????");
                            }
                        }else {
                            tv2_notic.setText("??????????????????????????????????????????6~18???");
                        }
                    }else {
                        tv2_notic.setText("?????????????????????,?????????????????????18???");
                    }
                    break;

                case R.id.bnt_login:
                    progressDialog1= new ProgressDialog(M12_LoginActivity.this);
                    progressDialog1.setMessage("?????????????????????????????????");
                    progressDialog1.setIndeterminate(true);// ??????????????????????????????  true?????????????????????????????????????????????  false ????????????????????????
                    progressDialog1.setCancelable(false);//?????????????????????dialog??????????????????dialog  true?????????????????? false??????????????????
                    progressDialog1.show();
                    if (et1.getText().toString().trim().length()>=1
                            &&et1_pwd.getText().toString().trim().length()>=1) {
                        sharedPreferences=getSharedPreferences("userinfo",MODE_PRIVATE);
                        String username=sharedPreferences.getString("username","");
                        String userpwd=sharedPreferences.getString("userpwd","");

                        //?????????????????????
                        //???????????????????????????
                        if (open){
//                            Toast.makeText(M12_LoginActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                            boolean[] login = login(et1.getText().toString().trim(), et1_pwd.getText().toString().trim());
                           /* if (login[0]){
                                Intent intent = new Intent();
                                intent.putExtra("name", et1.getText().toString().trim());
                                intent.setClass(M12_LoginActivity.this, Main12Activity.class);
                                startActivity(intent);
                                Toast.makeText(M12_LoginActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                                //??????????????????????????????????????????
                                else {
                                progressDialog1.dismiss();
                                //??????????????????
                                if (et1.getText().toString().trim().equals(getResources().getString(R.string.username))
                                        && et1_pwd.getText().toString().trim().equals(getResources().getString(R.string.password))){
                                    Intent intent = new Intent(M12_LoginActivity.this, Main12Activity.class);
                                    startActivity(intent);

                                }else {
                                    if (open){

                                    }else {
                                        tv1_notic.setText("???????????????????????????");
                                        et1_pwd.setText("");
                                    }

                                }

                        }*/
                        } else {
                            progressDialog1.dismiss();
                                //??????????????????
                                if (et1.getText().toString().trim().equals(getResources().getString(R.string.username))
                                        && et1_pwd.getText().toString().trim().equals(getResources().getString(R.string.password))){
                                    Intent intent = new Intent(M12_LoginActivity.this, Main12Activity.class);
                                    startActivity(intent);

                                }else {
                                    if (open){

                                    }else {
                                        tv1_notic.setText("???????????????????????????");
                                        et1_pwd.setText("");
                                    }
                                }
                            }


                    }else {
                        tv1_notic.setText("??????????????????????????????");
                    }
                    break;

                case R.id.tv_wangji:
                    sharedPreferences=getSharedPreferences("userinfo",MODE_PRIVATE);
                    String username1=sharedPreferences.getString("username","???");
                    String userpwd1=sharedPreferences.getString("userpwd","???");
                    AlertDialog.Builder builder = new AlertDialog.Builder(M12_LoginActivity.this);
                    builder.setTitle("????????????");
                    if ("???".equals(username1)){
                        builder.setMessage("????????????" + username1 + "\n" + "?????????" + userpwd1
                        +"\n\n"+"<??????????????????????????????????????????>");
                    }else {
                        builder.setMessage("????????????" + username1 + "\n" + "?????????" + userpwd1);
                    }
                    builder.setCancelable(false);
                    builder.setPositiveButton("??????",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                    break;

                case R.id.tv_zhuce:
                    sd_1.open();
                    break;

                case R.id.tv_zhuxiao:
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(M12_LoginActivity.this);
                    builder1.setTitle("??????");
                    builder1.setMessage("??????????????????????????????????????????");
                    builder1.setCancelable(false);

                    builder1.setPositiveButton("??????",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferences=getSharedPreferences("userinfo",MODE_PRIVATE);
                            SharedPreferences.Editor editor1=sharedPreferences.edit();
                            editor1.clear();
                            editor1.commit();
                            String name= sharedPreferences.getString("username","???");
                            if ("???".equals(name)){
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(M12_LoginActivity.this);
                            builder2.setTitle("????????????????????????");
                            builder2.setMessage("????????????????????????????????????????????????????????????????????????????????????????????????");
                            builder2.setCancelable(false);
                            builder2.setPositiveButton("??????",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.cancel();
                                    sd_1.open();
                                }
                            });builder2.create().show();
                            }else {
                                Toast.makeText(M12_LoginActivity.this,"????????????",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder1.setNegativeButton("??????",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder1.create().show();
                    break;
                case R.id.logo:
                    sharedPreferences=getSharedPreferences("userinfo",MODE_PRIVATE);
                    String name=sharedPreferences.getString("username","");
                    String pwd=sharedPreferences.getString("userpwd","");
                    if (TextUtils.isEmpty(name)&&TextUtils.isEmpty(pwd)){
                        Toast.makeText(M12_LoginActivity.this,"?????????????????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
                        sd_1.open();
                    }else {
                        et1.setText(name);
                        et1_pwd.setText("");
                    }
                    break;
                case R.id.iv_login:
                    sharedPreferences=getSharedPreferences("userinfo",MODE_PRIVATE);
                    String name1=sharedPreferences.getString("username","");
                    String pwd1=sharedPreferences.getString("userpwd","");
                    if (TextUtils.isEmpty(name1)&&TextUtils.isEmpty(pwd1)){
                        Toast.makeText(M12_LoginActivity.this,"?????????????????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
                        sd_1.open();
                    }else {
                        et1.setText(name1);
                        et1_pwd.setText("");
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + v.getId());
            }

        }
    }
    public void yanzheng(){
        String url=getResources().getString(R.string.zhangheng_url)+"yanzheng";
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (e.getMessage().indexOf("404") > 1 || e.getMessage().indexOf("not found") > 1) {
                            Toast.makeText(M12_LoginActivity.this,"??????????????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
                            if (!open) {
                                tv1_notic.setText("??????????????????????????????????????????????????????" +
                                        "\n?????????" + getResources().getString(R.string.username) +
                                        "\n?????????" + getResources().getString(R.string.password));
                                et1.setText(getResources().getString(R.string.username));
                                et1_pwd.setText(getResources().getString(R.string.password));
                                sharedPreferences=getSharedPreferences("userinfo",MODE_PRIVATE);
                                SharedPreferences.Editor editor1=sharedPreferences.edit();
                                editor1.clear();
                                editor1.commit();
                                open=false;
                            }
                            else {
                                tv1_notic.setText("?????????" + e.getMessage());
                                open=true;
                            }
                        }else if (e.getMessage().startsWith("Unable to resolve host")){
                            Toast.makeText(M12_LoginActivity.this,"????????????",Toast.LENGTH_SHORT).show();
                            tv1_notic.setText("??????????????????????????????????????????" +
                                    "\n?????????" + getResources().getString(R.string.username) +
                                    "\n?????????" + getResources().getString(R.string.password));
                            et1.setText(getResources().getString(R.string.username));
                            et1_pwd.setText(getResources().getString(R.string.password));
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response.indexOf("?????????")>0) {
                            Toast.makeText(M12_LoginActivity.this,"??????????????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
                            if (!open) {
                                tv1_notic.setText("??????????????????????????????????????????????????????" +
                                        "\n?????????" + getResources().getString(R.string.username) +
                                        "\n?????????" + getResources().getString(R.string.password));
                                et1.setText(getResources().getString(R.string.username));
                                et1_pwd.setText(getResources().getString(R.string.password));
                                sharedPreferences=getSharedPreferences("userinfo",MODE_PRIVATE);
                                SharedPreferences.Editor editor1=sharedPreferences.edit();
                                editor1.clear();
                                editor1.commit();
                            }
                            open=false;
                        }else {
                            if (response.equals("true")) {
                                Toast.makeText(M12_LoginActivity.this,"?????????????????????????????????",Toast.LENGTH_SHORT).show();
                                open = true;
                            } else {
                                tv1_notic.setText(response);
                                open = true;
                            }
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    public boolean[] login(final String username, final String password){
        final boolean[] b = {false};
        String url=getResources().getString(R.string.zhangheng_url)+"login";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("username",username)
                .addParams("password",password)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog1.dismiss();
                        if (e.getMessage().indexOf("404") > 1 || e.getMessage().indexOf("not found") > 1) {
                            Toast.makeText(M12_LoginActivity.this,"??????????????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
                        }else if (e.getMessage().startsWith("Unable to resolve host")){
                            Toast.makeText(M12_LoginActivity.this,"????????????",Toast.LENGTH_SHORT).show();
                            tv1_notic.setText("??????????????????????????????????????????" +
                                    "\n?????????" + getResources().getString(R.string.username) +
                                    "\n?????????" + getResources().getString(R.string.password));
                            et1.setText(getResources().getString(R.string.username));
                            et1_pwd.setText(getResources().getString(R.string.password));
                        }
                        else {
                            tv1_notic.setText("?????????" + e.getMessage());
                        }
                        et1_pwd.setText("");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson=new Gson();
                            Resuilt resuilt = gson.fromJson(response, Resuilt.class);
                            if (resuilt.getTitle().equals("????????????")) {
                                //  ??????????????????????????????
                                if (rb_jizhu.isChecked()==true) {
                                    sharedPreferences1 = getSharedPreferences("myuser", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                                    editor.putString("myname", et1.getText().toString().trim());
                                    editor.putString("mypwd", et1_pwd.getText().toString().trim());
                                    editor.commit();
                                    sharedPreferences2 = getSharedPreferences("Check", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                                    editor2.putString("check", "1");
                                    editor2.commit();
                                    //Log.d("check","1");
                                } else {
                                    sharedPreferences1=getSharedPreferences("myuser",MODE_PRIVATE);
                                    SharedPreferences.Editor editor1=sharedPreferences1.edit();
                                    editor1.clear();
                                    editor1.commit();
                                    sharedPreferences2=getSharedPreferences("Check",MODE_PRIVATE);
                                    SharedPreferences.Editor editor2=sharedPreferences2.edit();
                                    editor2.clear();
                                    editor2.commit();
                                    //Log.d("check","0"+sharedPreferences1.getString("Check","0"));

                                }Intent intent = new Intent();
                                intent.putExtra("name", et1.getText().toString().trim());
                                intent.setClass(M12_LoginActivity.this, Main12Activity.class);
                                startActivity(intent);
                                Toast.makeText(M12_LoginActivity.this, "???????????????"+resuilt.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                                b[0] = true;
                                tv1_notic.setText("");
                                et1_pwd.setText("");
                            } else {
                                et1_pwd.setText("");
                                if (open) {
                                    tv1_notic.setText(resuilt.getTitle() + "\n" + resuilt.getMessage());
                                }
                            }
                        progressDialog1.dismiss();
                        }

                });
        return b;
    }
    private void zhuCe(final String username, final String password){
        String url=getResources().getString(R.string.zhangheng_url)+"zhuce";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("username",username)
                .addParams("password",password)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        tv2_notic.setText("?????????"+e.getMessage());
                        if (e.getMessage().indexOf("404")>1||e.getMessage().indexOf("not found")>1){
                            Toast.makeText(M12_LoginActivity.this,"??????????????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
                            tv2_notic.setText("??????????????????????????????????????????????????????"+
                                    "\n?????????"+getResources().getString(R.string.username)+
                                    "\n?????????"+getResources().getString(R.string.password));
                        }else if (e.getMessage().startsWith("Unable to resolve host")){
                                Toast.makeText(M12_LoginActivity.this,"????????????",Toast.LENGTH_SHORT).show();
                            tv2_notic.setText("??????????????????????????????????????????" +
                                        "\n?????????" + getResources().getString(R.string.username) +
                                        "\n?????????" + getResources().getString(R.string.password));
                                et1.setText(getResources().getString(R.string.username));
                                et1_pwd.setText(getResources().getString(R.string.password));

                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson=new Gson();
                        Resuilt resuilt = gson.fromJson(response, Resuilt.class);
                        if (resuilt.getTitle().equals("????????????")){
                            sharedPreferences=getSharedPreferences("userinfo",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("username",username);
                            editor.putString("userpwd",password);
                            editor.commit();
                            sd_1.close();sd_1.close();
                            Toast.makeText(M12_LoginActivity.this,"???????????????????????????",Toast.LENGTH_SHORT).show();
                            tv2_notic.setText(resuilt.getTitle()+"\n"+resuilt.getMessage());
                        }else {
                            tv2_notic.setText(resuilt.getTitle()+"\n"+resuilt.getMessage());
                        }
                    }
                });
    }

}
