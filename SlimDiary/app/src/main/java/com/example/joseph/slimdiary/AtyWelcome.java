package com.example.joseph.slimdiary;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.joseph.slimdiary.DB.User;
import com.example.joseph.slimdiary.DIYView.SdCardUtil;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.smssdk.SMSSDK;

/**
 * Created by Joseph on 2017/3/13.
 */

public class AtyWelcome extends Activity {

    //设置延迟时间
    private final int SKIP_DELAY_TIME = 3000;

    String MOBAPPKEY="1bfa64da24a9c";
    String MOBAPPSECRETE="0c6ce695bc0e3413037fa3ac5c1c7fdb";
    String BMOBAPPLICATIONID="336dbc92c0aba451510c0fd15805d92a";

    private final int CODE_GALLERY_PERMISSION  = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_welcome);

        //初始化SDK
        SMSSDK.initSDK(this,MOBAPPKEY,MOBAPPSECRETE);
        Bmob.initialize(this, BMOBAPPLICATIONID);

        Timer time = new Timer();
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 23) {
                    //android 6.0权限问题
                    if ( ContextCompat.checkSelfPermission(AtyWelcome.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AtyWelcome.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_GALLERY_PERMISSION);
                    }
                    else {
                        init();
                    }
                }
                else {
                    init();
                }

                User bmobUser = BmobUser.getCurrentUser( User.class);
                if(bmobUser != null){
                    // 允许用户使用应用
                    startActivity(new Intent(AtyWelcome.this, MainActivity.class));
                    finish();
                }else{
                    //缓存用户对象为空时， 可打开用户注册界面…
                    startActivity(new Intent(AtyWelcome.this, AtyLogin.class));
                    finish();
                }


            }
        };
        time.schedule(task, SKIP_DELAY_TIME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CODE_GALLERY_PERMISSION){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                init();
            }else {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void init(){

        SdCardUtil.createFileDir(SdCardUtil.FILEDIR);

        SdCardUtil.createFileDir(SdCardUtil.FILEDIR+"/"+SdCardUtil.FILEIMAGE);

        SdCardUtil.createFileDir(SdCardUtil.FILEDIR+"/"+SdCardUtil.FILECACHE);

        SdCardUtil.createFileDir(SdCardUtil.FILEDIR+"/"+SdCardUtil.FILEUSER+"/icon");

    }

}
