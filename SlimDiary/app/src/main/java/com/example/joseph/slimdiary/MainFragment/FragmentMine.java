package com.example.joseph.slimdiary.MainFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.joseph.slimdiary.DB.User;
import com.example.joseph.slimdiary.DB.UserDiary;
import com.example.joseph.slimdiary.DB.UserLikeDiary;
import com.example.joseph.slimdiary.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Joseph on 2017/3/15.
 */

public class FragmentMine extends Fragment {


    private ListView mListView;
    private MineListViewContentAdapter mAdapterMineListViewContent;
    private List<MineListViewContent> mListMineListViewContent;

    private static final int RESULT_ADD_NEW_DIARY = 1;
    private static final int RESULT_ADD_NEW_LIKE = 2;
    private static final int RESULT_ADD_NEW_DIARY_TRUE = 3;
    private static final int RESULT_ADD_NEW_LIKE_TRUE = 4;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ViewMineListViewContent =  inflater.inflate(R.layout.layout_mine, container, false);

        mListView = (ListView) ViewMineListViewContent.findViewById(R.id.lv_layout_mine);
        mListMineListViewContent=new ArrayList<>();
        mAdapterMineListViewContent = new MineListViewContentAdapter();


        //getMineListViewContentList();
        mListMineListViewContent.clear();
        getMineListViewContentList();
        mListView.setAdapter(mAdapterMineListViewContent);
        mListView.setOnItemClickListener(mMineListViewContentAdapterListener);


        FloatingActionButton fab_add_diary= (FloatingActionButton) ViewMineListViewContent.findViewById(R.id.fab_add_diary);
        fab_add_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent( getActivity(),ATYMineAddDiary.class);
                startActivityForResult(intent,RESULT_ADD_NEW_DIARY);
            }
        });
        return ViewMineListViewContent;
    }




    @Override
    public void onResume() {
        super.onResume();
//        mListMineListViewContent.clear();
//        getMineListViewContentList();
        //Toast.makeText(getActivity(),"layout_mine_up_backpic",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==RESULT_ADD_NEW_DIARY) {

            if (resultCode == RESULT_ADD_NEW_DIARY_TRUE) {
                Bundle bundle = data.getExtras();
                if(bundle.getBoolean("IsAddNewDiary")==true){
                    mListMineListViewContent.clear();
                    getMineListViewContentList();
                }
            }
        }

        if (requestCode ==RESULT_ADD_NEW_LIKE) {

            if (resultCode == RESULT_ADD_NEW_LIKE_TRUE) {
                Bundle bundle = data.getExtras();
                if(bundle.getBoolean("IsAddLike")==true){
                    mListMineListViewContent.clear();
                    getMineListViewContentList();
                }
            }
        }
    }

    AdapterView.OnItemClickListener mMineListViewContentAdapterListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MineListViewContent mineListViewContent = (MineListViewContent) mAdapterMineListViewContent.getItem(position);

            Context context;
            context = getActivity().getApplicationContext();
            Intent intent = new Intent(context, ATYMineContent.class);
            Bundle bundle = new Bundle();
            bundle.putString("ID", mineListViewContent.getID());
            intent.putExtras(bundle);
            startActivityForResult(intent,RESULT_ADD_NEW_LIKE);
           // startActivity(intent);

        }
    };
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void getMineListViewContentList() {
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
                                        case 0:
                                            String item3=userdiarylist.getObjectId();
                                            String item0 = userdiarylist.getDiarySelfEvaluation();
                                            String item1 = userdiarylist.getDiaryPrivate();
                                            String item2 = userdiarylist.getCreatedAt().toString();
                                            MineListViewContent data = new MineListViewContent(item3,item0,item1,item2,msg1.obj.toString());
                                            mListMineListViewContent.add(data);
                                            mAdapterMineListViewContent.notifyDataSetChanged();
                                            break;
                                    }

                                }
                            };

                            BmobQuery<UserLikeDiary> query = new BmobQuery<UserLikeDiary>();
                            query.addWhereEqualTo("userdiaryID",new BmobPointer(userdiarylist));
                            query.count(UserLikeDiary.class, new CountListener() {
                                @Override
                                public void done(Integer count, BmobException e) {
                                    if(e==null){
                                        Message message1 = handler1.obtainMessage();
                                        message1.what = 0;
                                        //以消息为载体
                                        message1.obj = count;//这里的list就是查询出list
                                        //向handler发送消息
                                        handler1.sendMessage(message1);

                                    }else{
                                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
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

    class MineListViewContent {
        private String ID;
        private String SelfEvaluation;
        private String PrivateThing;
        private String Time;
        private String Likes;
        public MineListViewContent(String ID,String SelfEvaluation,String PrivateThing,String Time,String Likes) {
            this.ID=ID;
            this.SelfEvaluation=SelfEvaluation;
            this.PrivateThing=PrivateThing;
            this.Time=Time;
            this.Likes=Likes;
        }

        public String getID() {
            return ID;
        }

        public String getSelfEvaluation() {
            return SelfEvaluation;
        }

        public String getPrivateThing() {
            return PrivateThing;
        }

        public String getTime() {
            return Time;
        }

        public String getLikes() {
            return Likes;
        }

    }

    class MineListViewContentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListMineListViewContent.size();
        }

        @Override
        public Object getItem(int position) {
            return mListMineListViewContent.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.layout_mine_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mSelfEvaluationTextView = (TextView) view.findViewById(R.id.tv_layout_mime_item_Self_evaluation);
                viewHolder.mPrivateThingTextView = (TextView) view.findViewById(R.id.tv_layout_mime_item_Private_thing);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_layout_mime_item_Time);
                viewHolder.mLikesTextView = (TextView) view.findViewById(R.id.tv_layout_mime_item_my_likes);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            MineListViewContent mineListViewContent = (MineListViewContent) getItem(position);
            viewHolder.mSelfEvaluationTextView.setText(mineListViewContent.getSelfEvaluation());
            viewHolder.mPrivateThingTextView.setText(mineListViewContent.getPrivateThing());
            viewHolder.mTimeTextView.setText(mineListViewContent.getTime());
            viewHolder.mLikesTextView.setText(mineListViewContent.getLikes());
            return view;
        }

        class ViewHolder {
            protected TextView mSelfEvaluationTextView;
            protected TextView mPrivateThingTextView;
            protected TextView mTimeTextView;
            protected TextView mLikesTextView;
        }
    }
}
