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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.joseph.slimdiary.DB.User;
import com.example.joseph.slimdiary.DB.UserDiary;
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

public class FragmentTheirs extends Fragment {

    private ListView mListView;
    private FragmentTheirs.TheirsListViewContentAdapter mAdapterTheirsListViewContent;
    private List<FragmentTheirs.TheirsListViewContent> mListTheirsListViewContent;

    private FragmentTheirs.TheirsListViewContentFollowAdapter mAdapterTheirsListViewContentFollow;
    private List<FragmentTheirs.TheirsListViewContent> mListTheirsListViewContentFollow;

    private RequestQueue mQueue,mQueueFollow;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View ViewTheirsListViewContent =  inflater.inflate(R.layout.layout_theirs, container, false);


        mListView = (ListView) ViewTheirsListViewContent.findViewById(R.id.lv_layout_theirs);
        // TODO 初始化VolleyRequestQueue对象,这个对象是Volley访问网络的直接入口
        mQueue = Volley.newRequestQueue(getActivity());
        mAdapterTheirsListViewContent = new FragmentTheirs.TheirsListViewContentAdapter(getActivity(),mQueue);
        mListTheirsListViewContent=new ArrayList<>();

        mQueueFollow = Volley.newRequestQueue(getActivity());
        mAdapterTheirsListViewContentFollow = new FragmentTheirs.TheirsListViewContentFollowAdapter(getActivity(),mQueueFollow);
        mListTheirsListViewContentFollow=new ArrayList<>();

        mListView.setAdapter(mAdapterTheirsListViewContent);

       // mListTheirsListViewContent.clear();
        getTheirsListViewContentList();
        mListView.setOnItemClickListener(mTheirsListViewContentAdapterListener);


        ((TextView)ViewTheirsListViewContent.findViewById(R.id.tv_my_guding_Recommends)).setTextColor(Color.parseColor("#212121"));
        ((View)ViewTheirsListViewContent.findViewById(R.id.view_Recommend)).setBackgroundColor(Color.parseColor("#fb8b8f"));

        //查看推荐
        ViewTheirsListViewContent.findViewById(R.id.relativelayout_Recommend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)ViewTheirsListViewContent.findViewById(R.id.tv_my_guding_Recommends)).setTextColor(Color.parseColor("#212121"));
                ((View)ViewTheirsListViewContent.findViewById(R.id.view_Recommend)).setBackgroundColor(Color.parseColor("#fb8b8f"));

                ((TextView)ViewTheirsListViewContent.findViewById(R.id.tv_my_guding_Follows)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)ViewTheirsListViewContent.findViewById(R.id.view_Follow)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                ((TextView)ViewTheirsListViewContent.findViewById(R.id.tv_my_guding_Asks)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)ViewTheirsListViewContent.findViewById(R.id.view_Ask)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                mListTheirsListViewContent.clear();
                mListView.setAdapter(mAdapterTheirsListViewContent);
                getTheirsListViewContentList();

            }
        });

        //查看关注
        ViewTheirsListViewContent.findViewById(R.id.relativelayout_Follow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)ViewTheirsListViewContent.findViewById(R.id.tv_my_guding_Follows)).setTextColor(Color.parseColor("#212121"));
                ((View)ViewTheirsListViewContent.findViewById(R.id.view_Follow)).setBackgroundColor(Color.parseColor("#fb8b8f"));

                ((TextView)ViewTheirsListViewContent.findViewById(R.id.tv_my_guding_Asks)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)ViewTheirsListViewContent.findViewById(R.id.view_Ask)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                ((TextView)ViewTheirsListViewContent.findViewById(R.id.tv_my_guding_Recommends)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)ViewTheirsListViewContent.findViewById(R.id.view_Recommend)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                mListTheirsListViewContentFollow.clear();
                mListView.setAdapter(mAdapterTheirsListViewContentFollow);
                getTheirsListViewContentFollowList();
            }
        });
        //查看收藏
        ViewTheirsListViewContent.findViewById(R.id.relativelayout_Ask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)ViewTheirsListViewContent.findViewById(R.id.tv_my_guding_Asks)).setTextColor(Color.parseColor("#212121"));
                ((View)ViewTheirsListViewContent.findViewById(R.id.view_Ask)).setBackgroundColor(Color.parseColor("#fb8b8f"));



                ((TextView)ViewTheirsListViewContent.findViewById(R.id.tv_my_guding_Follows)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)ViewTheirsListViewContent.findViewById(R.id.view_Follow)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                ((TextView)ViewTheirsListViewContent.findViewById(R.id.tv_my_guding_Recommends)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)ViewTheirsListViewContent.findViewById(R.id.view_Recommend)).setBackgroundColor(Color.parseColor("#d7d7d7"));
//                mListTheirsListViewContentAsk.clear();
//                mListView.setAdapter(mAdapterTheirsListViewContentAsk);
//                getTheirsListViewContentAskList(userdiaryID);
            }
        });

        return ViewTheirsListViewContent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO 取消所有未执行完的网络请求
        mQueue.cancelAll(this);
        mQueueFollow.cancelAll(this);
    }




    AdapterView.OnItemClickListener mTheirsListViewContentAdapterListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TheirsListViewContent theirsListViewContent = (TheirsListViewContent) mAdapterTheirsListViewContent.getItem(position);

            Context context;
            context = getActivity().getApplicationContext();
            Intent intent = new Intent(context, ATYTheirsContent.class);
            Bundle bundle = new Bundle();
            bundle.putString("ID", theirsListViewContent.getID());
            intent.putExtras(bundle);
            startActivity(intent);

        }
    };

    @Override
    public void onResume() {
        super.onResume();
//        mListTheirsListViewContent.clear();
//        getTheirsListViewContentList();
        //Toast.makeText(getActivity(),"layout_mine_up_backpic",Toast.LENGTH_SHORT).show();
    }


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
                            FragmentTheirs.TheirsListViewContent data=new FragmentTheirs.TheirsListViewContent(item0,item1,item2,item3,item4,item5);
                            mListTheirsListViewContent.add(data);
                            mAdapterTheirsListViewContent.notifyDataSetChanged();
                        }
                        break;
                }

            }
        };
        BmobQuery<UserDiary> query = new BmobQuery<UserDiary>();
        query.addWhereNotEqualTo("username", BmobUser.getCurrentUser( User.class));
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


    public void getTheirsListViewContentFollowList() {
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        List<UserLikeUser> listUserLikeUser= (List<UserLikeUser>) msg.obj;
                        for (final UserLikeUser userLikeUserlist : listUserLikeUser) {

                            final Handler handler1=new Handler() {
                                @Override
                                public void handleMessage(Message msg1) {
                                    switch (msg1.what) {
                                        case 1:
                                            List<UserDiary> list= (List<UserDiary>) msg1.obj;
                                            for (final UserDiary userdiarylist : list) {

                                                final String item0= userdiarylist.getObjectId();
                                                final String item1= userdiarylist.getUsername().getUsername();
                                                final String item2= userdiarylist.getUsername().getNickName();
                                                final String item4= userdiarylist.getDiarySelfEvaluation();
                                                final String item5= userdiarylist.getCreatedAt().toString();
                                                final BmobFile item3= userdiarylist.getUsername().getUserHeadPic();
                                                FragmentTheirs.TheirsListViewContent data=new FragmentTheirs.TheirsListViewContent(item0,item1,item2,item3,item4,item5);
                                                mListTheirsListViewContentFollow.add(data);
                                                mAdapterTheirsListViewContentFollow.notifyDataSetChanged();
                                            }
                                            break;
                                    }
                                }
                            };

                            BmobQuery<UserDiary> query = new BmobQuery<UserDiary>();
                            query.addWhereEqualTo("username", userLikeUserlist.getUsernameFriend());
                            query.include("username");
                            query.order("-createdAt");
                            query.findObjects(new FindListener<UserDiary>() {
                                @Override
                                public void done(final List<UserDiary> userDiaryList, BmobException e) {
                                    if (e == null) {
                                        Message message1 = handler1.obtainMessage();
                                        message1.what = 1;
                                        //以消息为载体
                                        message1.obj = userDiaryList;//这里的list就是查询出list
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

        BmobQuery<UserLikeUser> queryUserLikeUserMe = new BmobQuery<UserLikeUser>();
        queryUserLikeUserMe.addWhereEqualTo("usernameMe", BmobUser.getCurrentUser(User.class));
        queryUserLikeUserMe.findObjects(new FindListener<UserLikeUser>() {
            @Override
            public void done(List<UserLikeUser> object, BmobException e) {
                if(object.size()!=0){
                    Message message = handler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = object;//这里的list就是查询出list
                    //向handler发送消息
                    handler.sendMessage(message);
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
            FragmentTheirs.TheirsListViewContentAdapter.ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.layout_theirs_item, null);
                viewHolder = new FragmentTheirs.TheirsListViewContentAdapter.ViewHolder();
                viewHolder.mSelfEvaluationTextView = (TextView) view.findViewById(R.id.tv_layout_theirs_item_Self_evaluation);
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_layout_theirs_item_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_layout_theirs_item_Time);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_layout_theirs_item_head_pic);


                view.setTag(viewHolder);
            } else {
                viewHolder = (FragmentTheirs.TheirsListViewContentAdapter.ViewHolder) view.getTag();
            }

            final FragmentTheirs.TheirsListViewContent TheirsListViewContent = (FragmentTheirs.TheirsListViewContent) getItem(position);
            viewHolder.mSelfEvaluationTextView.setText(TheirsListViewContent.getSelfEvaluation());
            viewHolder.mTimeTextView.setText(TheirsListViewContent.getTime());
            if(TheirsListViewContent.getNickname()==""||TheirsListViewContent.getNickname()==null){
                viewHolder.mNicknameTextView.setText(TheirsListViewContent.getUsername());
            }else{
                viewHolder.mNicknameTextView.setText(TheirsListViewContent.getNickname());
            }
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mHeadPicCircleImageView,R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
            mImageLoader.get(TheirsListViewContent.getHeadPic().getUrl(), listener);

            viewHolder.mHeadPicCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context;
                    context = getActivity().getApplicationContext();
                    Intent intent = new Intent(context, AtyTheirsList.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", TheirsListViewContent.getUsername());
                    bundle.putString("nickname", TheirsListViewContent.getNickname());
                    bundle.putString("headpic", TheirsListViewContent.getHeadPic().getUrl());
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


    class TheirsListViewContentFollowAdapter extends BaseAdapter {

        private RequestQueue mQueue;
        private ImageLoader mImageLoader;
        public TheirsListViewContentFollowAdapter(Context context, RequestQueue queue) {
            super();
            mQueue = queue;
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }

        @Override
        public int getCount() {
            return mListTheirsListViewContentFollow.size();
        }

        @Override
        public Object getItem(int position) {
            return mListTheirsListViewContentFollow.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FragmentTheirs.TheirsListViewContentFollowAdapter.ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.layout_theirs_item, null);
                viewHolder = new FragmentTheirs.TheirsListViewContentFollowAdapter.ViewHolder();
                viewHolder.mSelfEvaluationTextView = (TextView) view.findViewById(R.id.tv_layout_theirs_item_Self_evaluation);
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_layout_theirs_item_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_layout_theirs_item_Time);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_layout_theirs_item_head_pic);


                view.setTag(viewHolder);
            } else {
                viewHolder = (FragmentTheirs.TheirsListViewContentFollowAdapter.ViewHolder) view.getTag();
            }

            final FragmentTheirs.TheirsListViewContent TheirsListViewContent = (FragmentTheirs.TheirsListViewContent) getItem(position);
            viewHolder.mSelfEvaluationTextView.setText(TheirsListViewContent.getSelfEvaluation());
            viewHolder.mTimeTextView.setText(TheirsListViewContent.getTime());
            if(TheirsListViewContent.getNickname()==""||TheirsListViewContent.getNickname()==null){
                viewHolder.mNicknameTextView.setText(TheirsListViewContent.getUsername());
            }else{
                viewHolder.mNicknameTextView.setText(TheirsListViewContent.getNickname());
            }
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mHeadPicCircleImageView,R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
            mImageLoader.get(TheirsListViewContent.getHeadPic().getUrl(), listener);

            viewHolder.mHeadPicCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context;
                    context = getActivity().getApplicationContext();
                    Intent intent = new Intent(context, AtyTheirsList.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", TheirsListViewContent.getUsername());
                    bundle.putString("nickname", TheirsListViewContent.getNickname());
                    bundle.putString("headpic", TheirsListViewContent.getHeadPic().getUrl());
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
