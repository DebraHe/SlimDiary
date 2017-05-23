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
import com.example.joseph.slimdiary.DB.UserLikeDiary;
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

public class ATYMyLikes extends AppCompatActivity {
    private ListView mListView;
    private ATYMyLikes.LikeAdapter mAdapterLike;
    private List<LikeOrCollectOrCommentOrFollow> mListLike;
    private RequestQueue mQueueLike;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mythings);
        mListView = (ListView) findViewById(R.id.listview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorBigBigGray));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);
        }

        mQueueLike = Volley.newRequestQueue(ATYMyLikes.this);
        mAdapterLike = new ATYMyLikes.LikeAdapter(ATYMyLikes.this,mQueueLike);
        mListLike=new ArrayList<>();

        mListLike.clear();
        mListView.setAdapter(mAdapterLike);
        getLikeList();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO 取消所有未执行完的网络请求
        mQueueLike.cancelAll(this);
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
    public void getLikeList() {
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        List<UserLikeDiary> list= (List<UserLikeDiary>) msg.obj;
                        for (final UserLikeDiary userLikeDiarylist : list) {

                            final String item0 = userLikeDiarylist.getUserdiaryID().getObjectId();
                            final String item1 = userLikeDiarylist.getUserdiaryID().getUsername().getUsername();
                            final String item2 = userLikeDiarylist.getUserdiaryID().getUsername().getNickName();
                            final String item4 = userLikeDiarylist.getUserdiaryID().getDiarySelfEvaluation();
                            final String item5 = userLikeDiarylist.getCreatedAt().toString();
                            final BmobFile item3 = userLikeDiarylist.getUserdiaryID().getUsername().getUserHeadPic();
                            LikeOrCollectOrCommentOrFollow data = new LikeOrCollectOrCommentOrFollow(item0, item1, item2, item3, item4, item5);
                            mListLike.add(data);
                            mAdapterLike.notifyDataSetChanged();

                        }
                        break;
                }

            }
        };
        BmobQuery<UserLikeDiary> query = new BmobQuery<UserLikeDiary>();
        query.addWhereEqualTo("username", BmobUser.getCurrentUser( User.class));
        query.include("userdiaryID,userdiaryID.username");
        query.order("-createdAt");
        query.findObjects(new FindListener<UserLikeDiary>() {
            @Override
            public void done(final List<UserLikeDiary> list, BmobException e) {
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


    class LikeAdapter extends BaseAdapter {

        private RequestQueue mQueue;
        private ImageLoader mImageLoader;
        public LikeAdapter(Context context, RequestQueue queue) {
            super();
            mQueue = queue;
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }

        @Override
        public int getCount() {
            return mListLike.size();
        }

        @Override
        public Object getItem(int position) {
            return mListLike.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ATYMyLikes.LikeAdapter.ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getLayoutInflater().inflate(R.layout.layout_my_likes, null);
                viewHolder = new ATYMyLikes.LikeAdapter.ViewHolder();
                viewHolder.mSelfEvaluationTextView = (TextView) view.findViewById(R.id.tv_diary_self_evaluation);
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_time);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_head_pic);


                view.setTag(viewHolder);
            } else {
                viewHolder = (ATYMyLikes.LikeAdapter.ViewHolder) view.getTag();
            }

            final LikeOrCollectOrCommentOrFollow Like = (LikeOrCollectOrCommentOrFollow) getItem(position);
            viewHolder.mSelfEvaluationTextView.setText(Like.getSelfEvaluation());
            viewHolder.mTimeTextView.setText(Like.getTime());
            if(Like.getNickname()==""||Like.getNickname()==null){
                viewHolder.mNicknameTextView.setText(Like.getUsername());
            }else{
                viewHolder.mNicknameTextView.setText(Like.getNickname());
            }
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mHeadPicCircleImageView,R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
            mImageLoader.get(Like.getHeadPic().getUrl(), listener);


            viewHolder.mHeadPicCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AtyTheirsList.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", Like.getUsername());
                    bundle.putString("nickname", Like.getNickname());
                    bundle.putString("headpic", Like.getHeadPic().getUrl());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });


            return view;
        }

        class ViewHolder {
            protected TextView mSelfEvaluationTextView;
            protected TextView mNicknameTextView;
            protected CircleImageView mHeadPicCircleImageView;
            protected TextView mTimeTextView;
        }
    }


}
