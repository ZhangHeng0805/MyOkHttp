package com.zhangheng.myapplication.getphoneMessage;

import android.content.Context;
import android.os.Environment;

import com.zhangheng.myapplication.util.LocalFileTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GetPhoto {

    private Context context;
    public final static String BasePath=Environment.getExternalStorageDirectory().getAbsolutePath();

    public GetPhoto(Context context) {
        this.context = context;
    }

    public static String getFileSizeString(Long size) {
        double length = Double.valueOf(String.valueOf(size));
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少意义
        if (length < 1024) {
            return length + "B";
        } else {
            length = length / 1024.0;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (length < 1024) {
            return Math.round(length * 100) / 100.0 + "KB";
        } else {
            length = length / 1024.0;
        }
        if (length < 1024) {
            //因为如果以 MB为单位的话，要保留最后1位小数，因此把此数乘以100之后再取余
            return Math.round(length * 100) / 100.0 + "MB";
        } else {
            //否则如果要以为单位的，先除于1024再作同样的处理 GB
            return Math.round(length / 1024 * 100) / 100.0 + "GB";
        }
    }

    public List<String> getPhoto() {
        List<String> list = new ArrayList<>();
        LocalFileTool.readFile(LocalFileTool.imageType, context, new LocalFileTool.IReadCallBack() {
            @Override
            public void callBack(List<String> localPath) {
                for (String path : localPath) {
                    File file = new File(path);
//                    Log.d("图片地址:", file.getName() + "(" + getFileSizeString(file.length()) + ")");
                    if (file.length() > 1024 * 1024 * 100) {
                        list.add(path);
                    }
                }
            }
        });
        return list;
    }
    public List<String> getPhoto(String[] path,long size){
        List<String> photo = getPhoto();
        List<String> pList=new ArrayList<>();
        for (String s : photo) {
            String replace = s.replace(GetPhoto.BasePath, "");
            for (String s1 : path) {
                if (replace.startsWith(s1)){
                    if (new File(s).length()>size) {
                        pList.add(s);
                    }
                    break;
                }
            }
        }
        return pList;
    }
}
