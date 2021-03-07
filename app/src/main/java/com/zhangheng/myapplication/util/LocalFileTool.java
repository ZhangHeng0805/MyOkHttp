package com.zhangheng.myapplication.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LocalFileTool {
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


                List<String> paths = new ArrayList<String>();
                Uri[] fileUri = null;
                fileUri = new Uri[]{MediaStore.Files.getContentUri("external")};

                String[] colums = new String[]{MediaStore.Files.FileColumns.DATA};
                String[] extension = mimeType;

                //构造筛选语句
                String selection = "";
                for (int i = 0; i < extension.length; i++) {
                    if (i != 0) {
                        selection = selection + " OR ";
                    }
                    selection = selection + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE '%" + extension[i] + "'";
                }

                //获取内容解析器对象
                ContentResolver resolver = context1.getContentResolver();
                //获取游标
                for (int i = 0; i < fileUri.length; i++) {
                    Cursor cursor = resolver.query(fileUri[i], colums, selection, null, null);
                    if (cursor == null) {
                        return null;
                    }//游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
                    long beginTime = System.currentTimeMillis();
                    if (cursor.moveToLast()) {

                        do {
                            //输出文件的完整路径
                            String data = cursor.getString(0);
                            paths.add(data);

                        } while (cursor.moveToPrevious());

                    }
                    cursor.close();
                    android.util.Log.e("endTime", System.currentTimeMillis() - beginTime + "");
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

}
