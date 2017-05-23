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
import com.example.joseph.slimdiary.DB.UserDiaryCollection;
import com.example.joseph.slimdiary.DIYView.BitmapCache;
import com.example.joseph.slimdiary.DIYView.CircleImageView;
import com.example.joseph.slimdiary.R;

import org.w3c.dom.Comment;

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

public class ATYMyCollects extends AppCompatActivity {
    private ListView mListView;
    private ATYMyCollects.CollectAdapter mAdapterCollect;
    private List<LikeOrCollectOrCommentOrFollow> mListCollect;
    private RequestQueue mQueueCollect;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mythings);
        mListView = (ListView) findViewById(R.id.listview);

        mQueueCollect = Volley.newRequestQueue(ATYMyCollects.this);
        mAdapterCollect = new ATYMyCollects.CollectAdapter(ATYMyCollects.this,mQueueCollect);
        mListCollect=new ArrayList<>();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorBigBigGray));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);
        }


        mListCollect.clear();
        mListView.setAdapter(mAdapterCollect);
        getCollectList();

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


    public void getCollectList() {
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        List<UserDiaryCollection> list= (List<UserDiaryCollection>) msg.obj;
                        for (final UserDiaryCollection userDiaryCollectionlist : list) {
                            final String item0= userDiaryCollectionlist.getUserdiaryID().getObjectId();
                            final String item1= userDiaryCollectionlist.getUserdiaryID().getUsername().getUsername();
                            final String item2= userDiaryCollectionlist.getUserdiaryID().getUsername().getNickName();
                            final String item4= userDiaryCollectionlist.getUserdiaryID().getDiarySelfEvaluation();
                            final String item5= userDiaryCollectionlist.getCreatedAt().toString();
                            final BmobFile item3= userDiaryCollectionlist.getUserdiaryID().getUsername().getUserHeadPic();
                            LikeOrCollectOrCommentOrFollow data=new LikeOrCollectOrCommentOrFollow(item0,item1,item2,item3,item4,item5);
                            mListCollect.add(data);
                            mAdapterCollect.notifyDataSetChanged();


                        }
                        break;
                }

            }
        };
        BmobQuery<UserDiaryCollection> query = new BmobQuery<UserDiaryCollection>();
        query.addWhereEqualTo("username", BmobUser.getCurrentUser( User.class));
        query.include("userdiaryID,userdiaryID.username");
        query.order("-createdAt");
        query.findObjects(new FindListener<UserDiaryCollection>() {
            @Override
            public void done(final List<UserDiaryCollection> list, BmobException e) {
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

    class CollectAdapter extends BaseAdapter {

        private RequestQueue mQueue;
        private ImageLoader mImageLoader;
        public CollectAdapter(Context context, RequestQueue queue) {
            super();
            mQueue = queue;
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }

        @Override
        public int getCount() {
            return mListCollect.size();
        }

        @Override
        public Object getItem(int position) {
            return mListCollect.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ATYMyCollects.CollectAdapter.ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getLayoutInflater().inflate(R.layout.layout_my_collects, null);
                viewHolder = new ATYMyCollects.CollectAdapter.ViewHolder();
                viewHolder.mSelfEvaluationTextView = (TextView) view.findViewById(R.id.tv_diary_self_evaluation);
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_time);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_head_pic);


                view.setTag(viewHolder);
            } else {
                viewHolder = (ATYMyCollects.CollectAdapter.ViewHolder) view.getTag();
            }

            final LikeOrCollectOrCommentOrFollow Collect = (LikeOrCollectOrCommentOrFollow) getItem(position);
            viewHolder.mSelfEvaluationTextView.setText(Collect.getSelfEvaluation());
            viewHolder.mTimeTextView.setText(Collect.getTime());
            if(Collect.getNickname()==""||Collect.getNickname()==null){
                viewHolder.mNicknameTextView.setText(Collect.getUsername());
            }else{
                viewHolder.mNicknameTextView.setText(Collect.getNickname());
            }
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mHeadPicCircleImageView,R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
            mImageLoader.get(Collect.getHeadPic().getUrl(), listener);


            viewHolder.mHeadPicCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AtyTheirsList.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", Collect.getUsername());
                    bundle.putString("nickname", Collect.getNickname());
                    bundle.putString("headpic", Collect.getHeadPic().getUrl());
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
