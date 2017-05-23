package com.example.joseph.slimdiary.DIYView;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Joseph on 2017/3/18.
 */

public class SdCardUtil {
    // 项目文件根目录

    public static final String FILEDIR = "/SlimDiary";


    // 应用程序图片存放

    public static final String FILEIMAGE = "/images";

    // 应用程序缓存

    public static final String FILECACHE = "/cache";

    // 用户信息目录

    public static final String FILEUSER = "user";

    public static final String FILEROOT = getSdPath()+FILEDIR;
   /*

    * 获取sd卡的文件路径

    * getExternalStorageDirectory 获取路径

    */

    public static String getSdPath(){

        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            return Environment.getExternalStorageDirectory().toString();
        } else
            return "/data/data/package";

    }

   /*

    * 创建一个文件夹

    */

    public static  void  createFileDir(String fileDir){

        String path = getSdPath()+fileDir;

        File path1=new File(path);

        if(!path1.exists())

        {

            path1.mkdirs();

            Log.i("yang", "我被创建了");

        }

    }
}
