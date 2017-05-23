package com.example.joseph.slimdiary.MainFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.joseph.slimdiary.DB.LikeOrCollectOrCommentOrFollow;
import com.example.joseph.slimdiary.DB.User;
import com.example.joseph.slimdiary.DB.UserLikeUser;
import com.example.joseph.slimdiary.DIYView.BitmapCache;
import com.example.joseph.slimdiary.DIYView.CircleImageView;
import com.example.joseph.slimdiary.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.example.joseph.slimdiary.R.menu.menu_null;

/**
 * Created by Joseph on 2017/3/24.
 */

public class ATYMyFriends extends AppCompatActivity {
    private ListView mListView;
    private ATYMyFriends.FollowAdapter mAdapterFollow;
    private List<LikeOrCollectOrCommentOrFollow> mListFollow;
    private RequestQueue mQueueFollow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mythings);
        mListView = (ListView) findViewById(R.id.listview);


        mQueueFollow = Volley.newRequestQueue(ATYMyFriends.this);
        mAdapterFollow = new ATYMyFriends.FollowAdapter(ATYMyFriends.this,mQueueFollow);
        mListFollow=new ArrayList<>();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorBigBigGray));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);
        }



        mListFollow.clear();
        mListView.setAdapter(mAdapterFollow);
        getFollowList();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu_null,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }



    public void getFollowList() {
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        List<UserLikeUser> list= (List<UserLikeUser>) msg.obj;
                        for (final UserLikeUser userLikeUserlist : list) {

                            final String item0= userLikeUserlist.getObjectId();
                            final String item1= userLikeUserlist.getUsernameFriend().getUsername();
                            final String item2= userLikeUserlist.getUsernameFriend().getNickName();
                            final String item4= userLikeUserlist.getCreatedAt().toString();
                            final BmobFile item3= userLikeUserlist.getUsernameFriend().getUserHeadPic();
                            LikeOrCollectOrCommentOrFollow data=new LikeOrCollectOrCommentOrFollow(item0,item1,item2,item3,item4);
                            mListFollow.add(data);
                            mAdapterFollow.notifyDataSetChanged();


                        }
                        break;
                }

            }
        };
        BmobQuery<UserLikeUser> query = new BmobQuery<UserLikeUser>();
        query.addWhereEqualTo("usernameMe", BmobUser.getCurrentUser( User.class));
        query.include("usernameFriend");
        query.order("-createdAt");
        query.findObjects(new FindListener<UserLikeUser>() {
            @Override
            public void done(final List<UserLikeUser> list, BmobException e) {
                if (e == null) {
                    Message message = handler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = list;//这里的list就是查询出list
                    //向handler发送消息
                    handler.sendMessage(message);
                }
            }
        });



    }














    class FollowAdapter extends BaseAdapter {

        private RequestQueue mQueue;
        private ImageLoader mImageLoader;
        public FollowAdapter(Context context, RequestQueue queue) {
            super();
            mQueue = queue;
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }

        @Override
        public int getCount() {
            return mListFollow.size();
        }

        @Override
        public Object getItem(int position) {
            return mListFollow.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ATYMyFriends.FollowAdapter.ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getLayoutInflater().inflate(R.layout.layout_my_friends, null);
                viewHolder = new ATYMyFriends.FollowAdapter.ViewHolder();
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_time);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_head_pic);


                view.setTag(viewHolder);
            } else {
                viewHolder = (ATYMyFriends.FollowAdapter.ViewHolder) view.getTag();
            }

            final LikeOrCollectOrCommentOrFollow Follow = (LikeOrCollectOrCommentOrFollow) getItem(position);
            viewHolder.mTimeTextView.setText(Follow.getTime());
            if(Follow.getNickname()==""||Follow.getNickname()==null){
                viewHolder.mNicknameTextView.setText(Follow.getUsername());
            }else{
                viewHolder.mNicknameTextView.setText(Follow.getNickname());
            }
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mHeadPicCircleImageView,R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
            mImageLoader.get(Follow.getHeadPic().getUrl(), listener);

            viewHolder.mHeadPicCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AtyTheirsList.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", Follow.getUsername());
                    bundle.putString("nickname", Follow.getNickname());
                    bundle.putString("headpic", Follow.getHeadPic().getUrl());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            return view;
        }

        class ViewHolder {
            protected TextView mNicknameTextView;
            protected CircleImageView mHeadPicCircleImageView;
            protected TextView mTimeTextView;
        }
    }
}
