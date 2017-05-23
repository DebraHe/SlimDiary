package com.example.joseph.slimdiary.MainFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.joseph.slimdiary.DB.User;
import com.example.joseph.slimdiary.DB.UserDiary;
import com.example.joseph.slimdiary.DIYView.BitmapCache;
import com.example.joseph.slimdiary.DIYView.CircleImageView;
import com.example.joseph.slimdiary.DIYView.ListViewForScrollView;
import com.example.joseph.slimdiary.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.example.joseph.slimdiary.R.menu.menu_null;

/**
 * Created by Joseph on 2017/3/24.
 */

public class AtyTheirsList extends AppCompatActivity {

    private ListViewForScrollView mListView;
    private AtyTheirsList.TheirsListViewContentAdapter mAdapterTheirsListViewContent;
    private List<AtyTheirsList.TheirsListViewContent> mListTheirsListViewContent;

    String username="";
    private RequestQueue mQueue;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mypage);

        Bundle bundle = this.getIntent().getExtras();
        username = bundle.getString("username");
        final String nickname = bundle.getString("nickname");
        final String headpic = bundle.getString("headpic");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorBigBigGray));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);
        }

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if(nickname==""||nickname==null){
            collapsingToolbar.setTitle(username);
        }else{
            collapsingToolbar.setTitle(nickname);
        }

        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        RequestQueue mQueueHeadpic = Volley.newRequestQueue(getApplicationContext());
        ImageLoader imageLoader = new ImageLoader(mQueueHeadpic, new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {

            }
        });
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView,R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
        imageLoader.get(headpic, listener);
        FloatingActionButton fab_add_diary= (FloatingActionButton) findViewById(R.id.fab_add_diary);
        fab_add_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent( AtyTheirsList.this,ATYMineAddDiary.class);
                startActivity(intent);
            }
        });

        mListView = (ListViewForScrollView) findViewById(R.id.listview);

        // TODO 初始化VolleyRequestQueue对象,这个对象是Volley访问网络的直接入口
        mQueue = Volley.newRequestQueue(AtyTheirsList.this);
        mAdapterTheirsListViewContent = new AtyTheirsList.TheirsListViewContentAdapter(AtyTheirsList.this,mQueue);
        mListTheirsListViewContent=new ArrayList<>();
        mListView.setAdapter(mAdapterTheirsListViewContent);
        getTheirsListViewContentList();
        mListView.setOnItemClickListener(mTheirsListViewContentAdapterListener);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO 取消所有未执行完的网络请求
        mQueue.cancelAll(this);
    }

    AdapterView.OnItemClickListener mTheirsListViewContentAdapterListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AtyTheirsList.TheirsListViewContent theirsListViewContent = (AtyTheirsList.TheirsListViewContent) mAdapterTheirsListViewContent.getItem(position);

            Intent intent = new Intent(AtyTheirsList.this, ATYTheirsContent.class);
            Bundle bundle = new Bundle();
            bundle.putString("ID", theirsListViewContent.getID());
            intent.putExtras(bundle);
            startActivity(intent);

        }
    };

    public void getTheirsListViewContentList() {
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        List<UserDiary> list= (List<UserDiary>) msg.obj;
                        for (final UserDiary userdiarylist : list) {

                            final String item0= userdiarylist.getObjectId();
                            final String item1= userdiarylist.getUsername().getUsername();
                            final String item2= userdiarylist.getUsername().getNickName();
                            final String item4= userdiarylist.getDiarySelfEvaluation();
                            final String item5= userdiarylist.getCreatedAt().toString();
                            final BmobFile item3= userdiarylist.getUsername().getUserHeadPic();
                            AtyTheirsList.TheirsListViewContent data=new AtyTheirsList.TheirsListViewContent(item0,item1,item2,item3,item4,item5);
                            mListTheirsListViewContent.add(data);
                            mAdapterTheirsListViewContent.notifyDataSetChanged();
                        }
                        break;
                }

            }
        };
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username", username);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    for (User userget : list) {
                        BmobQuery<UserDiary> query = new BmobQuery<UserDiary>();
                        query.addWhereEqualTo("username", userget);
                        query.include("username");
                        query.order("-createdAt");
                        query.findObjects(new FindListener<UserDiary>() {
                            @Override
                            public void done(final List<UserDiary> list, BmobException e) {
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
                }
            }
        });

    }


    class TheirsListViewContent {
        //缺少一个头像
        private String Nickname;
        private String username;
        private BmobFile HeadPic;
        private String ID;
        private String SelfEvaluation;
        private String Time;
        public TheirsListViewContent(String ID,String username,String Nickname,BmobFile HeadPic,String SelfEvaluation,String Time ){

            this.ID=ID;
            this.username=username;
            this.HeadPic=HeadPic;
            this.Nickname=Nickname;
            this.SelfEvaluation=SelfEvaluation;
            this.Time=Time;
        }

        public String getUsername() {
            return username;
        }


        public String getID() {
            return ID;
        }

        public BmobFile getHeadPic() {
            return HeadPic;
        }

        public String getSelfEvaluation() {
            return SelfEvaluation;
        }

        public String getNickname() {
            return Nickname;
        }

        public String getTime() {
            return Time;
        }



    }

    class TheirsListViewContentAdapter extends BaseAdapter {

        private RequestQueue mQueue;
        private ImageLoader mImageLoader;
        public TheirsListViewContentAdapter(Context context, RequestQueue queue) {
            super();
            mQueue = queue;
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }

        @Override
        public int getCount() {
            return mListTheirsListViewContent.size();
        }

        @Override
        public Object getItem(int position) {
            return mListTheirsListViewContent.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AtyTheirsList.TheirsListViewContentAdapter.ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getLayoutInflater().inflate(R.layout.layout_theirs_item, null);
                viewHolder = new AtyTheirsList.TheirsListViewContentAdapter.ViewHolder();
                viewHolder.mSelfEvaluationTextView = (TextView) view.findViewById(R.id.tv_layout_theirs_item_Self_evaluation);
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_layout_theirs_item_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_layout_theirs_item_Time);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_layout_theirs_item_head_pic);


                view.setTag(viewHolder);
            } else {
                viewHolder = (AtyTheirsList.TheirsListViewContentAdapter.ViewHolder) view.getTag();
            }

            final AtyTheirsList.TheirsListViewContent TheirsListViewContent = (AtyTheirsList.TheirsListViewContent) getItem(position);
            viewHolder.mSelfEvaluationTextView.setText(TheirsListViewContent.getSelfEvaluation());
            viewHolder.mTimeTextView.setText(TheirsListViewContent.getTime());
            if(TheirsListViewContent.getNickname()==""||TheirsListViewContent.getNickname()==null){
                viewHolder.mNicknameTextView.setText(TheirsListViewContent.getUsername());
            }else{
                viewHolder.mNicknameTextView.setText(TheirsListViewContent.getNickname());
            }
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mHeadPicCircleImageView,R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
            mImageLoader.get(TheirsListViewContent.getHeadPic().getUrl(), listener);




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
