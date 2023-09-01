package com.zhangheng.myapplication.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LocalFileTool {
//    public final static String BasePath= Environment.getExternalStorageDirectory().getAbsolutePath();
    public final static String BasePath= Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String[] imageType=new String[]{"image/bmp","image/jpeg","image/png"};
    public static final String[] videoType=new String[]{"video/3gpp","video/x-ms-asf",
            "video/x-msvideo", "video/vnd.mpegurl","video/x-m4v","video/quicktime",
            "video/mp4","video/mpeg",};
    public static  final String[] audioType=new String[]{"audio/x-mpegurl",
            "audio/mp4a-latm","audio/x-mpeg","audio/mpeg","audio/ogg","audio/x-wav",
            "audio/x-ms-wma"};
    public static final String[] docType=new String[]{"application/msword","application/pdf",
            "application/vnd.ms-powerpoint","text/plain","application/vnd.ms-works",
            "application/vnd.android.package-archive"};
    public static final String[] zipType=new String[]{"application/x-gtar","application/x-gzip",
            "application/x-compress","application/zip"};

    // final static Object lock=new Object();

    public static void readFile(final String[] mimeType,Context context, final IReadCallBack iReadCallBack)
    {
        Observable.just(context).map(new Func1<Context, List<String>>() {
            @Override
            public List<String> call(Context context1) {
                long beginTime = System.currentTimeMillis();
                List<String> paths = new LinkedList<>();
                Uri[] fileUri = null;
                fileUri = new Uri[]{MediaStore.Files.getContentUri("external")};

                String[] colums = new String[]{MediaStore.Files.FileColumns.DATA};
                String[] extension = mimeType;

                //构造筛选语句
                StringBuilder selection = new StringBuilder();
                for (int i = 0; i < extension.length; i++) {
                    if (i != 0) {
                        selection.append(" OR ");
                    }
                    selection.append(MediaStore.Files.FileColumns.MIME_TYPE + " LIKE '%" + extension[i] + "'");
                }
                //获取内容解析器对象
                ContentResolver resolver = context1.getContentResolver();
                //获取游标
                for (int i = 0; i < fileUri.length; i++) {
                    Cursor cursor = resolver.query(fileUri[i], colums, selection.toString(), null, null);
                    if (cursor == null) {
                        return null;
                    }//游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
                    if (cursor.moveToLast()) {
                        do {
                            //输出文件的完整路径
//                            String data = cursor.getString(0);
                            paths.add(cursor.getString(0));
                        } while (cursor.moveToPrevious());
                    }
                    cursor.close();
                    Log.d("文件检索耗时", TimeUtil.format((int) (System.currentTimeMillis() - beginTime)));
                }
                return paths;


            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).
                subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> strings) {
                        iReadCallBack.callBack(strings);
                    }
                });

    }

    public interface IReadCallBack
    {
        void callBack(List<String> localPath);
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
    /**
     * 文件转换byte数组
     * @param file 文件
     * @return byte[]
     */
    public static byte[] fileToBytes(File file){
        FileInputStream is = null;
        byte[] fileBytes=null;
        try {
            is = new FileInputStream(file);
            long length = file.length();
            fileBytes= new byte[(int) length];
            is.read(fileBytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is!=null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileBytes;
    }
}
