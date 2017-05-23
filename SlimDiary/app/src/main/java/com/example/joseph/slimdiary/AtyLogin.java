package com.example.joseph.slimdiary;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.joseph.slimdiary.DB.User;

import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

/**
 * Created by Joseph on 2017/3/14.
 */

public class AtyLogin extends Activity {



    EditText tvPhone,tvPassword;

    EditText PasswordOne,PasswordTwo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_login);




        tvPhone = (EditText)findViewById(R.id.login_edit_account);
        tvPassword=(EditText)findViewById(R.id.login_edit_pwd);
        //设置注册点击事件
        findViewById(R.id.login_text_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // registerUser("13340350421");
                //注册手机号
                RegisterPage registerPage=new RegisterPage();

                //注册回调事件
                registerPage.setRegisterCallback(new EventHandler(){

                    //事件完成后调用
                    @Override
                    public void afterEvent(int event, int result, Object data) {
                        //判断结果是否已经完成
                        if(result==SMSSDK.RESULT_COMPLETE){
                            //获取数据data
                            HashMap<String,Object> maps= (HashMap<String, Object>) data;
                            //手机号信息
                            String phone= (String) maps.get("phone");
                            registerUser(phone);
                        }
                    }
                });
                //显示注册界面
                registerPage.show(AtyLogin.this);
            }
        });

        //设置换密码点击事件
        findViewById(R.id.login_text_change_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // changePassword("13340350421");
                //注册手机号
                RegisterPage registerPage=new RegisterPage();

                //注册回调事件
                registerPage.setRegisterCallback(new EventHandler(){

                    //事件完成后调用
                    @Override
                    public void afterEvent(int event, int result, Object data) {
                        //判断结果是否已经完成
                        if(result==SMSSDK.RESULT_COMPLETE){
                            //获取数据data
                            HashMap<String,Object> maps= (HashMap<String, Object>) data;
                            //手机号信息
                            String phone= (String) maps.get("phone");
                            changePassword(phone);
                        }
                    }
                });
                //显示注册界面
                registerPage.show(AtyLogin.this);
            }
        });

        //登陆事件
        findViewById(R.id.login_btn_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = tvPhone.getText().toString();
                String password = tvPassword.getText().toString();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){

                    User.loginByAccount(username, password, new LogInListener<User>() {

                        @Override
                        public void done(User user, BmobException e) {
                            if(user!=null){
                                startActivity(new Intent(AtyLogin.this, MainActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(AtyLogin.this, "Register failed."+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

                }
            }
        });
    }

    /**
     * 提交用户信息
     * @param phone
     */
    public void registerUser(final String phone ){

        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.layout_set_password, (ViewGroup) findViewById(R.id.layout_set_password));
        PasswordOne = (EditText) layout.findViewById(R.id.tv_layout_set_password_one);
        PasswordTwo = (EditText) layout.findViewById(R.id.tv_layout_set_password_two);
        new AlertDialog.Builder(AtyLogin.this).setTitle("Set password").setView(layout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(vaildatePassword()){

                    //注册用户密码，以及手机号
                    final User registerUser = new User();
                    registerUser.setUsername(phone);
                    registerUser.setPassword(PasswordOne.getText().toString().trim());
                    registerUser.setMobilePhoneNumber(phone);
                    registerUser.setMobilePhoneNumberVerified(true);
                    registerUser.signUp(new SaveListener<User >() {
                        @Override
                        public void done(User  myUser, BmobException e) {
                            if (e == null) {
                                User.loginByAccount(phone, PasswordOne.getText().toString().trim(), new LogInListener<User>() {

                                    @Override
                                    public void done(User user, BmobException e) {
                                        if(user!=null){
                                            startActivity(new Intent(AtyLogin.this, MainActivity.class));
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(AtyLogin.this, "Register failed."+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                });
                            } else {
                                //注册失败
                                Toast.makeText(AtyLogin.this, "Register failed."+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
//                    final BmobFile bmobFile = new BmobFile(headpic);
//                    //上传头像
//                    registerUser.getUserHeadPic().uploadblock(new UploadFileListener() {
//
//                        @Override
//                        public void done(BmobException e) {
//
//                        }
//
//                    });

                }
            }
        }).setNegativeButton("Cancel", null).show();
    }


    /**
     * 修改密码
     * @param phone
     */
    public void changePassword(final String phone ){

        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.layout_set_password, (ViewGroup) findViewById(R.id.layout_set_password));
        PasswordOne = (EditText) layout.findViewById(R.id.tv_layout_set_password_one);
        PasswordTwo = (EditText) layout.findViewById(R.id.tv_layout_set_password_two);
        new AlertDialog.Builder(AtyLogin.this).setTitle("Change password").setView(layout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(vaildatePassword()){
                    BmobQuery<User> query = new BmobQuery<User>();
                    query.addWhereEqualTo("username", phone);
                    query.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if (e == null) {
                                for (User userget : list) {
                                    //获得playerName的信息
                                    userget.setPassword(PasswordOne.getText().toString().trim());
                                    userget.update(userget.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(AtyLogin.this, "Change password succeeded.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                //修改失败
                                                Toast.makeText(AtyLogin.this, "Change password failed."+ e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }

                            } else {
                                //修改失败
                                Toast.makeText(AtyLogin.this, "Change password failed."+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        }).setNegativeButton("Cancel", null).show();
    }

    private boolean vaildatePassword() {

        String passwordOne=PasswordOne.getText().toString().trim();
        String passwordTwo=PasswordTwo.getText().toString().trim();
        //首先要判断是否为空
        if (passwordOne.equals("") || null == passwordOne ||passwordTwo.equals("") || null == passwordTwo) {
            Toast.makeText(AtyLogin.this, "Register failed.Password can not be null.", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            if(passwordOne.length()>=8 &&passwordOne.length()<=16 &&passwordTwo.length()>=8 &&passwordTwo.length()<=16){
                return true;
            }
            else{
                Toast.makeText(AtyLogin.this, "Register failed.Password's length should be between 8 and 16.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

    }


}
