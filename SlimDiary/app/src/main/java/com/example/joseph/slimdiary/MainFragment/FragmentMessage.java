package com.example.joseph.slimdiary.MainFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.joseph.slimdiary.DB.User;
import com.example.joseph.slimdiary.DB.UserComment;
import com.example.joseph.slimdiary.DB.UserDiary;
import com.example.joseph.slimdiary.DB.UserDiaryCollection;
import com.example.joseph.slimdiary.DB.UserLikeDiary;
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

/**
 * Created by Joseph on 2017/3/15.
 */

public class FragmentMessage extends Fragment {

    private ListView mListView;
    private FragmentMessage.LikeAdapter mAdapterLike;
    private List<FragmentMessage.LikeOrCollectOrCommentOrFollow> mListLike;

    private FragmentMessage.CollectAdapter mAdapterCollect;
    private List<FragmentMessage.LikeOrCollectOrCommentOrFollow> mListCollect;

    private FragmentMessage.FollowAdapter mAdapterFollow;
    private List<FragmentMessage.LikeOrCollectOrCommentOrFollow> mListFollow;

    private FragmentMessage.CommentAdapter mAdapterComment;
    private List<FragmentMessage.LikeOrCollectOrCommentOrFollow> mListComment;

    private RequestQueue mQueueLike,mQueueCollect,mQueueFollow,mQueueComment;
    
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        final View view=inflater.inflate(R.layout.layout_message,container,false);


        mListView = (ListView) view.findViewById(R.id.lv_layout_message);
        // TODO 初始化VolleyRequestQueue对象,这个对象是Volley访问网络的直接入口
        mQueueLike = Volley.newRequestQueue(getActivity());
        mAdapterLike = new FragmentMessage.LikeAdapter(getActivity(),mQueueLike);
        mListLike=new ArrayList<>();

        mQueueCollect = Volley.newRequestQueue(getActivity());
        mAdapterCollect = new FragmentMessage.CollectAdapter(getActivity(),mQueueCollect);
        mListCollect=new ArrayList<>();

        mQueueFollow = Volley.newRequestQueue(getActivity());
        mAdapterFollow = new FragmentMessage.FollowAdapter(getActivity(),mQueueFollow);
        mListFollow=new ArrayList<>();


        mQueueComment = Volley.newRequestQueue(getActivity());
        mAdapterComment = new FragmentMessage.CommentAdapter(getActivity(),mQueueComment);
        mListComment=new ArrayList<>();

        mListView.setAdapter(mAdapterLike);

        // mListTheirsListViewContent.clear();
        getLikeList();

     //   mListView.setOnItemClickListener(mTheirsListViewContentAdapterListener);



        ((TextView)view.findViewById(R.id.tv_my_guding_Likes)).setTextColor(Color.parseColor("#212121"));
        ((View)view.findViewById(R.id.view_Like)).setBackgroundColor(Color.parseColor("#fb8b8f"));

        //查看点赞
        view.findViewById(R.id.relativelayout_Like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)view.findViewById(R.id.tv_my_guding_Likes)).setTextColor(Color.parseColor("#212121"));
                ((View)view.findViewById(R.id.view_Like)).setBackgroundColor(Color.parseColor("#fb8b8f"));

                ((TextView)view.findViewById(R.id.tv_my_guding_Comments)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)view.findViewById(R.id.view_Comment)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                ((TextView)view.findViewById(R.id.tv_my_guding_Collects)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)view.findViewById(R.id.view_Collect)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                ((TextView)view.findViewById(R.id.tv_my_guding_Follows)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)view.findViewById(R.id.view_Follow)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                mListLike.clear();
                mListView.setAdapter(mAdapterLike);
                getLikeList();

            }
        });

        //查看评论
        view.findViewById(R.id.relativelayout_Comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)view.findViewById(R.id.tv_my_guding_Comments)).setTextColor(Color.parseColor("#212121"));
                ((View)view.findViewById(R.id.view_Comment)).setBackgroundColor(Color.parseColor("#fb8b8f"));

                ((TextView)view.findViewById(R.id.tv_my_guding_Collects)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)view.findViewById(R.id.view_Collect)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                ((TextView)view.findViewById(R.id.tv_my_guding_Likes)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)view.findViewById(R.id.view_Like)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                ((TextView)view.findViewById(R.id.tv_my_guding_Follows)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)view.findViewById(R.id.view_Follow)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                mListComment.clear();
                mListView.setAdapter(mAdapterComment);
                getCommentList();
            }
        });
        //查看收藏
        view.findViewById(R.id.relativelayout_Collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)view.findViewById(R.id.tv_my_guding_Collects)).setTextColor(Color.parseColor("#212121"));
                ((View)view.findViewById(R.id.view_Collect)).setBackgroundColor(Color.parseColor("#fb8b8f"));



                ((TextView)view.findViewById(R.id.tv_my_guding_Comments)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)view.findViewById(R.id.view_Comment)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                ((TextView)view.findViewById(R.id.tv_my_guding_Likes)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)view.findViewById(R.id.view_Like)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                ((TextView)view.findViewById(R.id.tv_my_guding_Follows)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)view.findViewById(R.id.view_Follow)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                mListCollect.clear();
                mListView.setAdapter(mAdapterCollect);
                getCollectList();
            }
        });

        //查看跟随
        view.findViewById(R.id.relativelayout_Follow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((TextView)view.findViewById(R.id.tv_my_guding_Follows)).setTextColor(Color.parseColor("#212121"));
                ((View)view.findViewById(R.id.view_Follow)).setBackgroundColor(Color.parseColor("#fb8b8f"));



                ((TextView)view.findViewById(R.id.tv_my_guding_Collects)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)view.findViewById(R.id.view_Collect)).setBackgroundColor(Color.parseColor("#d7d7d7"));
                ((TextView)view.findViewById(R.id.tv_my_guding_Comments)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)view.findViewById(R.id.view_Comment)).setBackgroundColor(Color.parseColor("#d7d7d7"));
                ((TextView)view.findViewById(R.id.tv_my_guding_Likes)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)view.findViewById(R.id.view_Like)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                mListFollow.clear();
                mListView.setAdapter(mAdapterFollow);
                getFollowList();
            }
        });



        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO 取消所有未执行完的网络请求
        mQueueLike.cancelAll(this);
        mQueueCollect.cancelAll(this);
        mQueueFollow.cancelAll(this);
        mQueueComment.cancelAll(this);
    }




//    AdapterView.OnItemClickListener mLikeAdapterListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            FragmentMessage.LikeOrCollect Like = (FragmentMessage.LikeOrCollect) mAdapterLike.getItem(position);
//
//            Context context;
//            context = getActivity().getApplicationContext();
//            Intent intent = new Intent(context, ATYTheirsContent.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("ID", Like.getID());
//            intent.putExtras(bundle);
//            startActivity(intent);
//
//        }
//    };




    public void getLikeList() {
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        List<UserDiary> list= (List<UserDiary>) msg.obj;
                        for (final UserDiary userdiarylist : list) {

                            final Handler handler1=new Handler(){
                                @Override
                                public void handleMessage(Message msg1) {
                                    switch (msg1.what){
                                        case 1:
                                            List<UserLikeDiary> list= (List<UserLikeDiary>) msg1.obj;
                                            for (final UserLikeDiary userLikeDiarylist : list) {
                                                final String item0= userLikeDiarylist.getUserdiaryID().getObjectId();
                                                final String item1= userLikeDiarylist.getUsername().getUsername();
                                                final String item2= userLikeDiarylist.getUsername().getNickName();
                                                final String item4= userdiarylist.getDiarySelfEvaluation();
                                                final String item5= userLikeDiarylist.getCreatedAt().toString();
                                                final BmobFile item3= userLikeDiarylist.getUsername().getUserHeadPic();
                                                FragmentMessage.LikeOrCollectOrCommentOrFollow data=new FragmentMessage.LikeOrCollectOrCommentOrFollow(item0,item1,item2,item3,item4,item5);
                                                mListLike.add(data);
                                                mAdapterLike.notifyDataSetChanged();
                                            }
                                            break;
                                     }


                                }
                            };

                            BmobQuery<UserLikeDiary> query = new BmobQuery<UserLikeDiary>();
                            query.addWhereEqualTo("userdiaryID", userdiarylist);
                            query.include("username");
                            query.order("-createdAt");
                            query.findObjects(new FindListener<UserLikeDiary>() {
                                @Override
                                public void done(final List<UserLikeDiary> listUserLikeDiary, BmobException e) {
                                    if (e == null) {
                                        Message message1 = handler1.obtainMessage();
                                        message1.what = 1;
                                        //以消息为载体
                                        message1.obj = listUserLikeDiary;//这里的list就是查询出list
                                        //向handler发送消息
                                        handler1.sendMessage(message1);
                                    }
                                }
                            });
                        }
                        break;
                }

            }
        };
        BmobQuery<UserDiary> query = new BmobQuery<UserDiary>();
        query.addWhereEqualTo("username", BmobUser.getCurrentUser( User.class));
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


    public void getCollectList() {
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        List<UserDiary> list= (List<UserDiary>) msg.obj;
                        for (final UserDiary userdiarylist : list) {

                            final Handler handler1=new Handler(){
                                @Override
                                public void handleMessage(Message msg1) {
                                    switch (msg1.what){
                                        case 1:
                                            List<UserDiaryCollection> list= (List<UserDiaryCollection>) msg1.obj;
                                            for (final UserDiaryCollection userDiaryCollectionylist : list) {
                                                final String item0= userDiaryCollectionylist.getUserdiaryID().getObjectId();
                                                final String item1= userDiaryCollectionylist.getUsername().getUsername();
                                                final String item2= userDiaryCollectionylist.getUsername().getNickName();
                                                final String item4= userdiarylist.getDiarySelfEvaluation();
                                                final String item5= userDiaryCollectionylist.getCreatedAt().toString();
                                                final BmobFile item3= userDiaryCollectionylist.getUsername().getUserHeadPic();
                                                FragmentMessage.LikeOrCollectOrCommentOrFollow data=new FragmentMessage.LikeOrCollectOrCommentOrFollow(item0,item1,item2,item3,item4,item5);
                                                mListCollect.add(data);
                                                mAdapterCollect.notifyDataSetChanged();
                                            }
                                            break;
                                    }


                                }
                            };

                            BmobQuery<UserDiaryCollection> query = new BmobQuery<UserDiaryCollection>();
                            query.addWhereEqualTo("userdiaryID", userdiarylist);
                            query.include("username");
                            query.order("-createdAt");
                            query.findObjects(new FindListener<UserDiaryCollection>() {
                                @Override
                                public void done(final List<UserDiaryCollection> listUserDiaryCollection, BmobException e) {
                                    if (e == null) {
                                        Message message1 = handler1.obtainMessage();
                                        message1.what = 1;
                                        //以消息为载体
                                        message1.obj = listUserDiaryCollection;//这里的list就是查询出list
                                        //向handler发送消息
                                        handler1.sendMessage(message1);
                                    }
                                }
                            });
                        }
                        break;
                }

            }
        };
        BmobQuery<UserDiary> query = new BmobQuery<UserDiary>();
        query.addWhereEqualTo("username", BmobUser.getCurrentUser( User.class));
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



    public void getFollowList() {
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        List<UserLikeUser> list= (List<UserLikeUser>) msg.obj;
                        for (final UserLikeUser userLikeUserlist : list) {

                            final String item0= userLikeUserlist.getObjectId();
                            final String item1= userLikeUserlist.getUsernameMe().getUsername();
                            final String item2= userLikeUserlist.getUsernameMe().getNickName();
                            final String item4= userLikeUserlist.getCreatedAt().toString();
                            final BmobFile item3= userLikeUserlist.getUsernameMe().getUserHeadPic();
                            FragmentMessage.LikeOrCollectOrCommentOrFollow data=new FragmentMessage.LikeOrCollectOrCommentOrFollow(item0,item1,item2,item3,item4);
                            mListFollow.add(data);
                            mAdapterFollow.notifyDataSetChanged();


                        }
                        break;
                }

            }
        };
        BmobQuery<UserLikeUser> query = new BmobQuery<UserLikeUser>();
        query.addWhereEqualTo("usernameFriend", BmobUser.getCurrentUser( User.class));
        query.include("usernameMe");
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

    public void getCommentList() {
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        List<UserDiary> list= (List<UserDiary>) msg.obj;
                        for (final UserDiary userdiarylist : list) {

                            final Handler handler1=new Handler(){
                                @Override
                                public void handleMessage(Message msg1) {
                                    switch (msg1.what){
                                        case 1:
                                            List<UserComment> list= (List<UserComment>) msg1.obj;
                                            for (final UserComment userCommentList : list) {
                                                final String item0= userCommentList.getUserdiaryID().getObjectId();
                                                final String item1= userCommentList.getUsername().getUsername();
                                                final String item2= userCommentList.getUsername().getNickName();
                                                final String item4= userdiarylist.getDiarySelfEvaluation();
                                                final String item5= userCommentList.getCreatedAt().toString();
                                                final BmobFile item3= userCommentList.getUsername().getUserHeadPic();
                                                final String item6=userCommentList.getCommentContent();
                                                FragmentMessage.LikeOrCollectOrCommentOrFollow data=new FragmentMessage.LikeOrCollectOrCommentOrFollow(item0,item1,item2,item3,item4,item5,item6);
                                                mListComment.add(data);
                                                mAdapterComment.notifyDataSetChanged();
                                            }
                                            break;
                                    }


                                }
                            };

                            BmobQuery<UserComment> query = new BmobQuery<UserComment>();
                            query.addWhereEqualTo("userdiaryID", userdiarylist);
                            query.include("username");
                            query.order("-createdAt");
                            query.findObjects(new FindListener<UserComment>() {
                                @Override
                                public void done(final List<UserComment> listUserComment, BmobException e) {
                                    if (e == null) {
                                        Message message1 = handler1.obtainMessage();
                                        message1.what = 1;
                                        //以消息为载体
                                        message1.obj = listUserComment;//这里的list就是查询出list
                                        //向handler发送消息
                                        handler1.sendMessage(message1);
                                    }
                                }
                            });
                        }
                        break;
                }

            }
        };
        BmobQuery<UserDiary> query = new BmobQuery<UserDiary>();
        query.addWhereEqualTo("username", BmobUser.getCurrentUser( User.class));
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


    class LikeOrCollectOrCommentOrFollow {
        //缺少一个头像
        private String Nickname;
        private String username;
        private BmobFile HeadPic;
        private String ID;
        private String SelfEvaluation;
        private String Time;
        private String Comment;
        public LikeOrCollectOrCommentOrFollow(String ID,String username,String Nickname,BmobFile HeadPic,String Time ){

            this.ID=ID;
            this.username=username;
            this.HeadPic=HeadPic;
            this.Nickname=Nickname;
            this.Time=Time;
        }

        public LikeOrCollectOrCommentOrFollow(String ID,String username,String Nickname,BmobFile HeadPic,String SelfEvaluation,String Time ){

            this.ID=ID;
            this.username=username;
            this.HeadPic=HeadPic;
            this.Nickname=Nickname;
            this.SelfEvaluation=SelfEvaluation;
            this.Time=Time;
        }

        public LikeOrCollectOrCommentOrFollow(String ID,String username,String Nickname,BmobFile HeadPic,String SelfEvaluation,String Time,String Comment ){

            this.ID=ID;
            this.username=username;
            this.HeadPic=HeadPic;
            this.Nickname=Nickname;
            this.SelfEvaluation=SelfEvaluation;
            this.Time=Time;
            this.Comment=Comment;
        }

        public String getComment() {
            return Comment;
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
            FragmentMessage.LikeAdapter.ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.layout_message_like, null);
                viewHolder = new FragmentMessage.LikeAdapter.ViewHolder();
                viewHolder.mSelfEvaluationTextView = (TextView) view.findViewById(R.id.tv_diary_self_evaluation);
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_time);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_head_pic);


                view.setTag(viewHolder);
            } else {
                viewHolder = (FragmentMessage.LikeAdapter.ViewHolder) view.getTag();
            }

            final FragmentMessage.LikeOrCollectOrCommentOrFollow Like = (FragmentMessage.LikeOrCollectOrCommentOrFollow) getItem(position);
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
                    Context context;
                    context = getActivity().getApplicationContext();
                    Intent intent = new Intent(context, AtyTheirsList.class);
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
            FragmentMessage.CollectAdapter.ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.layout_message_collect, null);
                viewHolder = new FragmentMessage.CollectAdapter.ViewHolder();
                viewHolder.mSelfEvaluationTextView = (TextView) view.findViewById(R.id.tv_diary_self_evaluation);
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_time);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_head_pic);


                view.setTag(viewHolder);
            } else {
                viewHolder = (FragmentMessage.CollectAdapter.ViewHolder) view.getTag();
            }

            final FragmentMessage.LikeOrCollectOrCommentOrFollow Collect = (FragmentMessage.LikeOrCollectOrCommentOrFollow) getItem(position);
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
                    Context context;
                    context = getActivity().getApplicationContext();
                    Intent intent = new Intent(context, AtyTheirsList.class);
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
            FragmentMessage.FollowAdapter.ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.layout_message_follow, null);
                viewHolder = new FragmentMessage.FollowAdapter.ViewHolder();
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_time);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_head_pic);


                view.setTag(viewHolder);
            } else {
                viewHolder = (FragmentMessage.FollowAdapter.ViewHolder) view.getTag();
            }

            final FragmentMessage.LikeOrCollectOrCommentOrFollow Follow = (FragmentMessage.LikeOrCollectOrCommentOrFollow) getItem(position);
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
                    Context context;
                    context = getActivity().getApplicationContext();
                    Intent intent = new Intent(context, AtyTheirsList.class);
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
            FragmentMessage.CommentAdapter.ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.layout_message_comment, null);
                viewHolder = new FragmentMessage.CommentAdapter.ViewHolder();
                viewHolder.mSelfEvaluationTextView = (TextView) view.findViewById(R.id.tv_diary_self_evaluation);
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_time);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_head_pic);
                viewHolder.mCommentTextView =(TextView)view.findViewById(R.id.tv_diary_comment);

                view.setTag(viewHolder);
            } else {
                viewHolder = (FragmentMessage.CommentAdapter.ViewHolder) view.getTag();
            }

            final FragmentMessage.LikeOrCollectOrCommentOrFollow Comment = (FragmentMessage.LikeOrCollectOrCommentOrFollow) getItem(position);
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
                    Context context;
                    context = getActivity().getApplicationContext();
                    Intent intent = new Intent(context, AtyTheirsList.class);
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
