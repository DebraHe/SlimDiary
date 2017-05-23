package com.example.joseph.slimdiary.MainFragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.joseph.slimdiary.DB.User;
import com.example.joseph.slimdiary.DB.UserDiary;
import com.example.joseph.slimdiary.DIYView.CircleImageView;
import com.example.joseph.slimdiary.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Joseph on 2017/3/18.
 */

public class ATYMineAddDiary extends Activity {

    private static final int RESULT_ADD_NEW_DIARY_TRUE = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_new_diary);
        initUserInfor();
        findViewById(R.id.btn_layout_add_new_diary_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_layout_add_new_diary_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                UserDiary useraddDiary = new UserDiary();
                useraddDiary.setUsername(BmobUser.getCurrentUser( User.class));
                useraddDiary.setDiaryLunch(((EditText)findViewById(R.id.tv_layout_mime_item_content_Lunch)).getText().toString());
                useraddDiary.setDiaryExercise(((EditText)findViewById(R.id.tv_layout_mime_item_content_Exercise)).getText().toString());
                useraddDiary.setDiarySelfEvaluation(((EditText)findViewById(R.id.tv_layout_mime_item_content_Self_evaluation)).getText().toString());
                useraddDiary.setDiaryPrivate(((EditText)findViewById(R.id.tv_layout_mime_item_content_Private_thing)).getText().toString());

                useraddDiary.save(new SaveListener() {

                    @Override
                    public void done(Object o, Object o2) {

                        Intent intent = new Intent(ATYMineAddDiary.this, FragmentMine.class);
                        intent.putExtra("IsAddNewDiary", true);
                        setResult(RESULT_ADD_NEW_DIARY_TRUE, intent);
                        finish();
                    }

                    @Override
                    public void done(Object o, BmobException e) {
                        if(e!=null) {
                            Toast.makeText(ATYMineAddDiary.this, "Save failed." + e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }


                });

            }
        });

    }
    private void initUserInfor() {
        //BmobUser中的特定属性
        User bmobUser = BmobUser.getCurrentUser( User.class);
        String username = bmobUser.getUsername();
        //MyUser中的扩展属性
        String nickName = bmobUser.getNickName();
        BmobFile userheadpic = bmobUser.getUserHeadPic();
        TextView tvNickname=(TextView)findViewById(R.id.tv_layout_theirs_item_nickname);
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
            ImageLoader.ImageListener listener = ImageLoader.getImageListener((CircleImageView)findViewById(R.id.iv_layout_theirs_item_head_pic),R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
            imageLoader.get(userheadpic.getUrl(), listener);

        }

    }
}
