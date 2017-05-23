package com.example.joseph.slimdiary;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.joseph.slimdiary.DB.User;
import com.example.joseph.slimdiary.DB.UserLikeUser;
import com.example.joseph.slimdiary.DIYView.CircleImageView;
import com.example.joseph.slimdiary.DIYView.SdCardUtil;
import com.example.joseph.slimdiary.DIYView.ShadeView;
import com.example.joseph.slimdiary.MainFragment.ATYMyCollects;
import com.example.joseph.slimdiary.MainFragment.ATYMyComments;
import com.example.joseph.slimdiary.MainFragment.ATYMyFriends;
import com.example.joseph.slimdiary.MainFragment.ATYMyLikes;
import com.example.joseph.slimdiary.MainFragment.AtyTheirsList;
import com.example.joseph.slimdiary.MainFragment.FragmentMessage;
import com.example.joseph.slimdiary.MainFragment.FragmentMine;
import com.example.joseph.slimdiary.MainFragment.FragmentTheirs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static com.example.joseph.slimdiary.R.menu.toolbar;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {


    private DrawerLayout drawerLayout_main;
    Toolbar main_toolbar;
    private List<ShadeView> tabIndicators= new ArrayList<ShadeView>();

    EditText PasswordOne,PasswordTwo;
    /**
     * 作为页面容器的ViewPager
     */
    private ViewPager viewPager;
    /**
     * 页面集合
     */
    private List<Fragment> fragmentList=new ArrayList<Fragment>();
    /**
     * 3个Fragment（页面）
     */
    FragmentMine fragmentMine=new FragmentMine();
    FragmentTheirs fragmentTheirs=new FragmentTheirs();
    FragmentMessage fragmentMessage=new FragmentMessage();

    CircleImageView userHeadPic;
    TextView tvNickname;
    NavigationView leftNavView;
    NavigationView rightNavView;
    private final int CODE_GALLERY_REQUEST  = 1;
    private static final int CODE_RESULT_REQUEST = 2;//最终裁剪后的结果
    private  static final int  CODE_GALLERY_PERMISSION =4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        main_toolbar.setTitle("MyDiary");
        main_toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorBigBigGray));
        setSupportActionBar(main_toolbar);

        drawerLayout_main= (DrawerLayout) findViewById(R.id.drawer_layout_main);

        leftNavView= (NavigationView) findViewById(R.id.nav_left_view);
        rightNavView= (NavigationView) findViewById(R.id.nav_right_view);
        View leftNavViewhead = leftNavView.inflateHeaderView(R.layout.left_nav_header);
        userHeadPic = (CircleImageView)leftNavViewhead.findViewById(R.id.ib_settings_head_pic);
        tvNickname=(TextView)leftNavViewhead.findViewById(R.id.tv_settings_nickname);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_dehaze_black_24dp);
        }


        viewPager = (ViewPager) findViewById(R.id.id_viewpager);



        initData();


        viewPager.setAdapter(new MyFrageStatePagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);


    }

    private void initData(){
        fragmentList.add(fragmentMine);
        fragmentList.add(fragmentTheirs);
        fragmentList.add(fragmentMessage);

        initTabIndicator();


        slidingbuttonClick();

        initUserInfor();


    }

    private void initUserInfor() {
        //BmobUser中的特定属性
        User bmobUser = BmobUser.getCurrentUser( User.class);
        String username = bmobUser.getUsername();
        //MyUser中的扩展属性
        String nickName = bmobUser.getNickName();
        BmobFile userheadpic = bmobUser.getUserHeadPic();

        if(nickName==""||nickName==null){
            tvNickname.setText(username);
        }
        else{
            tvNickname.setText(nickName);
        }
        if(userheadpic!=null){
            RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
            ImageLoader imageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
                @Override
                public Bitmap getBitmap(String url) {
                    return null;
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {

                }
            });
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(userHeadPic,R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
            imageLoader.get(userheadpic.getUrl(), listener);

        }





    }

    private void slidingbuttonClick(){

        //设置侧滑栏点击不穿透
        drawerLayout_main.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                drawerView.setClickable(true);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
            }
            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        leftNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {

                switch (item.getItemId()){
                    case R.id.tv_nickname:
                        LayoutInflater inflater1 = getLayoutInflater();
                        final View layout1 = inflater1.inflate(R.layout.layout_set_nickname, (ViewGroup) findViewById(R.id.layout_set_nickname));
                        final EditText Nickname = (EditText) layout1.findViewById(R.id.et_layout_nickname);
                        new AlertDialog.Builder(MainActivity.this).setTitle("Change nickname").setView(layout1).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(Nickname.getText().toString().trim()!=null||Nickname.getText().toString().trim()!=""){
                                    User bmobUser = BmobUser.getCurrentUser( User.class);
                                    bmobUser.setNickName(Nickname.getText().toString().trim());
                                    bmobUser.update(bmobUser.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                tvNickname.setText(Nickname.getText().toString().trim());
                                                Toast.makeText(MainActivity.this, "Change nickname succeeded.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                //修改失败
                                                Toast.makeText(MainActivity.this, "Change nickname failed."+ e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(MainActivity.this, "Change nickname failed.", Toast.LENGTH_LONG).show();
                                }

                            }
                        }).setNegativeButton("Cancel", null).show();

                        return true;
                    case R.id.tv_head_pic:
                        if (Build.VERSION.SDK_INT >= 23) {
                            //android 6.0权限问题
                            if ( ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_GALLERY_PERMISSION);
                            }
                            else {
                                getCropImageIntent();
                            }
                        }
                        else {
                            getCropImageIntent();
                        }
                        return true;
                    case R.id.tv_password:
                        LayoutInflater inflater2 = getLayoutInflater();
                        final View layout2 = inflater2.inflate(R.layout.layout_set_password, (ViewGroup) findViewById(R.id.layout_set_password));
                        PasswordOne = (EditText) layout2.findViewById(R.id.tv_layout_set_password_one);
                        PasswordTwo = (EditText) layout2.findViewById(R.id.tv_layout_set_password_two);
                        new AlertDialog.Builder(MainActivity.this).setTitle("Change password").setView(layout2).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(vaildatePassword()){
                                    BmobQuery<User> query = new BmobQuery<User>();
                                    query.addWhereEqualTo("username", BmobUser.getCurrentUser( User.class).getUsername());
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
                                                                Toast.makeText(MainActivity.this, "Change password succeeded.", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                //修改失败
                                                                Toast.makeText(MainActivity.this, "Change password failed."+ e.getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }

                                            } else {
                                                //修改失败
                                                Toast.makeText(MainActivity.this, "Change password failed."+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                }
                            }
                        }).setNegativeButton("Cancel", null).show();
                        return true;
                    case R.id.btn_login_out:
                        new AlertDialog.Builder(MainActivity.this).setTitle("Switch user").setMessage("Are you sure to login out?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                User.logOut();   //清除缓存用户对象
                                User currentUser = User.getCurrentUser(User.class); // 现在的currentUser是null了
                                startActivity(new Intent(MainActivity.this, AtyLogin.class));
                                finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                item.setChecked(false);
                            }
                        }).show();
                        return true;
                    default:
                        return false;
                }
            }
        });

        rightNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {

                switch (item.getItemId()){
                    case R.id.tv_my_friends:
                        startActivity(new Intent(MainActivity.this, ATYMyFriends.class));
                        return true;
                    case R.id.tv_my_likes:
                        startActivity(new Intent(MainActivity.this, ATYMyLikes.class));
                        return true;
                    case R.id.tv_my_comments:
                        startActivity(new Intent(MainActivity.this, ATYMyComments.class));
                        return true;
                    case R.id.tv_my_collects:
                        startActivity(new Intent(MainActivity.this, ATYMyCollects.class));
                        return true;
                    case R.id.tv_new_friends:
                        LayoutInflater inflater = getLayoutInflater();
                        final View layout = inflater.inflate(R.layout.layout_new_friends, (ViewGroup) findViewById(R.id.layout_new_friends));

                       new AlertDialog.Builder(MainActivity.this).setTitle("Add friend").setView(layout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String et_newfriend=((EditText)layout.findViewById(R.id.et_layout_new_friends)).getText().toString();
                                if(et_newfriend==""||et_newfriend==null){
                                    Toast.makeText(MainActivity.this,"Add friend failed.",Toast.LENGTH_SHORT).show();
                                }
                                else{

                                    BmobQuery<User> query = new BmobQuery<User>();
                                    query.addWhereEqualTo("username", et_newfriend);
                                    query.findObjects(new FindListener<User>() {
                                        @Override
                                        public void done(List<User> list, BmobException e) {
                                            if (e == null) {
                                                for (User userget : list) {
                                                    //获得playerName的信息
                                                    UserLikeUser userLikeUser=new UserLikeUser();
                                                    userLikeUser.setUsernameMe(BmobUser.getCurrentUser(User.class));
                                                    userLikeUser.setUsernameFriend(userget);
                                                    userLikeUser.save(new SaveListener<String>() {
                                                        @Override
                                                        public void done(String s, BmobException e) {
                                                            if (e == null) {
                                                                Toast.makeText(MainActivity.this, "Add friend succeeded.", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                //修改失败
                                                                Toast.makeText(MainActivity.this, "Add friend failed."+ e.getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }

                                            } else {
                                                //修改失败
                                                Toast.makeText(MainActivity.this, "Add friend failed."+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }).setNegativeButton("Cancel", null).show();
                        return true;
                    default:
                        return false;
                }
            }
        });


        userHeadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, AtyTheirsList.class);
                Bundle bundle = new Bundle();
                User bmobUser = BmobUser.getCurrentUser( User.class);
                bundle.putString("username", bmobUser.getUsername());
                bundle.putString("nickname", bmobUser.getNickName());
                bundle.putString("headpic", bmobUser.getUserHeadPic().getUrl());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
    private boolean vaildatePassword() {

        String passwordOne=PasswordOne.getText().toString().trim();
        String passwordTwo=PasswordTwo.getText().toString().trim();
        //首先要判断是否为空
        if (passwordOne.equals("") || null == passwordOne ||passwordTwo.equals("") || null == passwordTwo) {
            Toast.makeText(MainActivity.this, "Register failed.Password can not be null.", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            if(passwordOne.length()>=8 &&passwordOne.length()<=16 &&passwordTwo.length()>=8 &&passwordTwo.length()<=16){
                return true;
            }
            else{
                Toast.makeText(MainActivity.this, "Register failed.Password's length should be between 8 and 16.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(toolbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout_main.openDrawer(GravityCompat.START);
                break;
            case R.id.friends:
                drawerLayout_main.openDrawer(GravityCompat.END);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 定义自己的ViewPager适配器。
     * 也可以使用FragmentPagerAdapter。关于这两者之间的区别，可以自己去搜一下。
     */
    class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter
    {

        public MyFrageStatePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }


    }

    private void initTabIndicator() {
        ShadeView Mine = (ShadeView) findViewById(R.id.id_indicator_Mine);
        ShadeView Theirs = (ShadeView) findViewById(R.id.id_indicator_Theirs);
        ShadeView Messages = (ShadeView) findViewById(R.id.id_indicator_Messages);

        tabIndicators.add(Mine);
        tabIndicators.add(Theirs);
        tabIndicators.add(Messages);

        Mine.setOnClickListener(this);
        Theirs.setOnClickListener(this);
        Messages.setOnClickListener(this);

        Mine.setIconAlpha(1.0f);
    }

    @Override
    public void onClick(View v) {
        //缺少获取之前的tab状态
        resetTabsStatus();
        switch (v.getId()) {
            case R.id.id_indicator_Mine:
                main_toolbar.setTitle("MyDiary");
                main_toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorBigBigGray));
                tabIndicators.get(0).setIconAlpha(1.0f);
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_Theirs:
                main_toolbar.setTitle("Discovery");
                main_toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorBigBigGray));
                tabIndicators.get(1).setIconAlpha(1.0f);
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_Messages:
                main_toolbar.setTitle("Notification");
                main_toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorBigBigGray));
                tabIndicators.get(2).setIconAlpha(1.0f);
                viewPager.setCurrentItem(2, false);
                break;
        }
    }

    /**
     * 重置Tab状态
     */
    private void resetTabsStatus() {
        for (int i = 0; i < tabIndicators.size(); i++) {
            tabIndicators.get(i).setIconAlpha(0);
        }
    }

    /**
     * 如果是直接点击图标来跳转页面的话，position值为0到3，positionOffset一直为0.0
     * 如果是通过滑动来跳转页面的话
     * 假如是从第一页滑动到第二页
     * 在这个过程中，positionOffset从接近0逐渐增大到接近1.0，滑动完成后又恢复到0.0，而position只有在滑动完成后才从0变为1
     * 假如是从第二页滑动到第一页
     * 在这个过程中，positionOffset从接近1.0逐渐减小到0.0，而position一直是0
     *
     * @param position             当前页面索引
     * @param positionOffset       偏移量
     * @param positionOffsetPixels 偏移量
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.e("TAG", "position==" + position);
        Log.e("TAG", "positionOffset==" + positionOffset);
        Log.e("TAG", "positionOffsetPixels==" + positionOffsetPixels);
        if (positionOffset > 0) {
            ShadeView leftTab = tabIndicators.get(position);
            ShadeView rightTab = tabIndicators.get(position + 1);
            leftTab.setIconAlpha(1 - positionOffset);
            rightTab.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

        if(position==0) {
            main_toolbar.setTitle("MyDiary");
            main_toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorBigBigGray));
        }
        if(position==1) {
            main_toolbar.setTitle("Discovery");
            main_toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorBigBigGray));
        }
        if(position==2) {
            main_toolbar.setTitle("Notification");
            main_toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorBigBigGray));
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {//取消
           // Toast.makeText(getApplication(), "Cancel.", Toast.LENGTH_LONG).show();
            return;
        }

        switch (requestCode) {
            case CODE_GALLERY_REQUEST://如果是来自本地的
                cropRawPhoto(intent.getData());//直接裁剪图片
                break;
            case CODE_RESULT_REQUEST:

                if (intent != null) {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        Drawable drawable = new BitmapDrawable(this.getResources(), photo);
                        userHeadPic.setImageDrawable(drawable);
                        //saveBitmap(photo);
                        File file=saveBitmap(photo);
                        upload(file);
                    }
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }


    public File saveBitmap(Bitmap bm) {
        Log.e("SaveBitmap", "保存图片");
        File f = new File(SdCardUtil.FILEROOT+"/"+SdCardUtil.FILEUSER+"/icon/", "icon.png");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 60, out);
            out.flush();
            out.close();
            Log.i("SaveBitmap", "已经保存");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return f;
    }
    /**
     * 上传文件到bmob后台
     * */
    public void upload(File file){


        final BmobFile bmobFile = new BmobFile(file);
        //上传头像
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                User bmobUser = BmobUser.getCurrentUser(User.class);
                bmobUser.setUserHeadPic(bmobFile);
                bmobUser.update(bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "Change head picture succeeded.", Toast.LENGTH_SHORT).show();
                        } else {
                            //修改失败
                            Toast.makeText(MainActivity.this, "Change head picture failed."+ e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        });


    }


    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 60);
        intent.putExtra("outputY", 60);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CODE_RESULT_REQUEST);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CODE_GALLERY_PERMISSION){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCropImageIntent();
            }else {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    /**
     * 调用图片剪辑程序
     */
    public void getCropImageIntent() {

        //获取到了权限
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");//选择图片
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        //如果你想在Activity中得到新打开Activity关闭后返回的数据，
        //你需要使用系统提供的startActivityForResult(Intent intent,int requestCode)方法打开新的Activity
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }
}
