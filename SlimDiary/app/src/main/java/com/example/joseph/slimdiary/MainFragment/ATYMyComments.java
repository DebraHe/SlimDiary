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
import com.example.joseph.slimdiary.DB.UserComment;
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

public class ATYMyComments extends AppCompatActivity {

    private ListView mListView;
    private ATYMyComments.CommentAdapter mAdapterComment;
    private List<LikeOrCollectOrCommentOrFollow> mListComment;
    private RequestQueue mQueueComment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mythings);
        mListView = (ListView) findViewById(R.id.listview);

        mQueueComment = Volley.newRequestQueue(ATYMyComments.this);
        mAdapterComment = new ATYMyComments.CommentAdapter(ATYMyComments.this,mQueueComment);
        mListComment=new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorBigBigGray));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);
        }



        mListComment.clear();
        mListView.setAdapter(mAdapterComment);
        getCommentList();

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

    public void getCommentList() {
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        List<UserComment> list= (List<UserComment>) msg.obj;
                        for (final UserComment userCommentList : list) {
                            final String item0= userCommentList.getUserdiaryID().getObjectId();
                            final String item1= userCommentList.getUserdiaryID().getUsername().getUsername();
                            final String item2= userCommentList.getUserdiaryID().getUsername().getNickName();
                            final String item4= userCommentList.getUserdiaryID().getDiarySelfEvaluation();
                            final String item5= userCommentList.getCreatedAt().toString();
                            final BmobFile item3= userCommentList.getUserdiaryID().getUsername().getUserHeadPic();
                            final String item6=userCommentList.getCommentContent();
                            LikeOrCollectOrCommentOrFollow data=new LikeOrCollectOrCommentOrFollow(item0,item1,item2,item3,item4,item5,item6);
                            mListComment.add(data);
                            mAdapterComment.notifyDataSetChanged();
                        }
                        break;
                }


            }
        };

        BmobQuery<UserComment> query = new BmobQuery<UserComment>();
        query.addWhereEqualTo("username", BmobUser.getCurrentUser(User.class));
        query.include("userdiaryID,userdiaryID.username");
        query.order("-createdAt");
        query.findObjects(new FindListener<UserComment>() {
            @Override
            public void done(final List<UserComment> list, BmobException e) {
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

    class CommentAdapter extends BaseAdapter {

        private RequestQueue mQueue;
        private ImageLoader mImageLoader;
        public CommentAdapter(Context context, RequestQueue queue) {
            super();
            mQueue = queue;
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }

        @Override
        public int getCount() {
            return mListComment.size();
        }

        @Override
        public Object getItem(int position) {
            return mListComment.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ATYMyComments.CommentAdapter.ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getLayoutInflater().inflate(R.layout.layout_my_comments, null);
                viewHolder = new ATYMyComments.CommentAdapter.ViewHolder();
                viewHolder.mSelfEvaluationTextView = (TextView) view.findViewById(R.id.tv_diary_self_evaluation);
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_time);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_head_pic);
                viewHolder.mCommentTextView =(TextView)view.findViewById(R.id.tv_diary_comment);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ATYMyComments.CommentAdapter.ViewHolder) view.getTag();
            }

            final LikeOrCollectOrCommentOrFollow Comment = (LikeOrCollectOrCommentOrFollow) getItem(position);
            viewHolder.mSelfEvaluationTextView.setText(Comment.getSelfEvaluation());
            viewHolder.mCommentTextView.setText(Comment.getComment());
            viewHolder.mTimeTextView.setText(Comment.getTime());
            if(Comment.getNickname()==""||Comment.getNickname()==null){
                viewHolder.mNicknameTextView.setText(Comment.getUsername());
            }else{
                viewHolder.mNicknameTextView.setText(Comment.getNickname());
            }
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mHeadPicCircleImageView,R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
            mImageLoader.get(Comment.getHeadPic().getUrl(), listener);


            viewHolder.mHeadPicCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AtyTheirsList.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", Comment.getUsername());
                    bundle.putString("nickname", Comment.getNickname());
                    bundle.putString("headpic", Comment.getHeadPic().getUrl());
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
            protected TextView mCommentTextView;
        }
    }
}
