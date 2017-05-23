package com.example.joseph.slimdiary.MainFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.joseph.slimdiary.DB.User;
import com.example.joseph.slimdiary.DB.UserComment;
import com.example.joseph.slimdiary.DB.UserDiary;
import com.example.joseph.slimdiary.DB.UserDiaryCollection;
import com.example.joseph.slimdiary.DB.UserLikeDiary;
import com.example.joseph.slimdiary.DIYView.BitmapCache;
import com.example.joseph.slimdiary.DIYView.CircleImageView;
import com.example.joseph.slimdiary.DIYView.CollectView;
import com.example.joseph.slimdiary.DIYView.ListViewForScrollView;
import com.example.joseph.slimdiary.DIYView.PraiseView;
import com.example.joseph.slimdiary.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.example.joseph.slimdiary.R.menu.menu_null;

/**
 * Created by Joseph on 2017/3/15.
 */

public class ATYMineContent extends AppCompatActivity {


    private ListViewForScrollView mListView;   //listview控件

    //适配器
    private ATYMineContent.MineListViewContentCommentAdapter mAdapterMineListViewContentComment;
    private ATYMineContent.MineListViewContentLikeAdapter mAdapterMineListViewContentLike;
    private ATYMineContent.MineListViewContentCollectAdapter mAdapterMineListViewContentCollect;
    //内容列表
    private List<ATYMineContent.MineListViewContentComment> mListMineListViewContentComment;
    private List<ATYMineContent.MineListViewContentLikeOrCollect> mListMineListViewContentLike;
    private List<ATYMineContent.MineListViewContentLikeOrCollect> mListMineListViewContentCollect;

    //volley缓存序列
    private RequestQueue mQueuelike,mQueuecomment,mQueuecollect;

    TextView tv_mycommentNum,tv_mylikeNum,tv_mycollectNum;


    //添加为朋友，点赞，收藏按钮
    PraiseView likeTextview;
    CollectView collectButton;

    private static final int RESULT_ADD_NEW_LIKE_TRUE = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mine_item_content);

        // TODO 初始化VolleyRequestQueue对象,这个对象是Volley访问网络的直接入口
        mQueuelike = Volley.newRequestQueue(this);
        mQueuecomment = Volley.newRequestQueue(this);
        mQueuecollect = Volley.newRequestQueue(this);

        //传过来日记的对象名
        Bundle bundle = this.getIntent().getExtras();
        final String userdiaryID = bundle.getString("ID");
        LoadInit(userdiaryID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.layout_mine_item_content_toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorBigBigGray));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);
        }


        mListView = (ListViewForScrollView) findViewById(R.id.lv_layout_mine_item_content);
        tv_mycommentNum=(TextView)findViewById(R.id.tv_layout_mine_item_content_my_comments);
        tv_mylikeNum=(TextView)findViewById(R.id.tv_layout_mine_item_content_my_likes);
        tv_mycollectNum=(TextView)findViewById(R.id.tv_layout_mine_item_content_my_collects);
        collectButton= (CollectView) findViewById(R.id.btn_layout_mine_item_collect);
        likeTextview= (PraiseView) findViewById(R.id.praise_view);

        //初始化适配器数组
        mAdapterMineListViewContentComment = new ATYMineContent.MineListViewContentCommentAdapter(this,mQueuecomment);
        mListMineListViewContentComment=new ArrayList<>();
        mAdapterMineListViewContentLike = new ATYMineContent.MineListViewContentLikeAdapter(this,mQueuelike);
        mListMineListViewContentLike=new ArrayList<>();
        mAdapterMineListViewContentCollect = new ATYMineContent.MineListViewContentCollectAdapter(this,mQueuecollect);
        mListMineListViewContentCollect=new ArrayList<>();

        ((TextView)findViewById(R.id.tv_my_guding_comments)).setTextColor(Color.parseColor("#212121"));
        ((TextView)findViewById(R.id.tv_layout_mine_item_content_my_comments)).setTextColor(Color.parseColor("#212121"));
        ((View)findViewById(R.id.view_comment)).setBackgroundColor(Color.parseColor("#f9232b"));

        mListMineListViewContentComment.clear();
        mListView.setAdapter(mAdapterMineListViewContentComment);

        getMineListViewContentCommentList(userdiaryID);


        mListView.setOnItemClickListener(mMineListViewContentCommentAdapterListener);


        //添加评论
        findViewById(R.id.btn_layout_mine_item_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.layout_add_comment, (ViewGroup) findViewById(R.id.layout_add_comment));

                new AlertDialog.Builder(ATYMineContent.this).setTitle("Add comment").setView(layout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String et_comment=((EditText)layout.findViewById(R.id.et_layout_add_comment)).getText().toString();
                        if(et_comment==""||et_comment==null){
                            Toast.makeText(ATYMineContent.this,"Add comment failed.",Toast.LENGTH_SHORT).show();
                        }
                        else{

                            UserComment useraddComment=new UserComment();
                            useraddComment.setUsername(BmobUser.getCurrentUser( User.class));
                            useraddComment.setCommentContent(et_comment);
                            UserDiary userDiary=new UserDiary();
                            userDiary.setObjectId(userdiaryID);
                            useraddComment.setUserdiaryID(userDiary);

                            useraddComment.save(new SaveListener() {

                                @Override
                                public void done(Object o, Object o2) {
                                    mListMineListViewContentComment.clear();
                                    getMineListViewContentCommentList(userdiaryID);

                                }

                                @Override
                                public void done(Object o, BmobException e) {
                                    if(e!=null) {
                                        Toast.makeText(ATYMineContent.this, "Add comment failed." + e.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                }


                            });
                        }
                    }
                }).setNegativeButton("Cancel", null).show();
            }
        });

        //收藏日记
        collectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                BmobQuery<UserDiary> queryUserDiary = new BmobQuery<UserDiary>();
                queryUserDiary.getObject(userdiaryID, new QueryListener<UserDiary>() {
                    @Override
                    public void done(UserDiary object, BmobException e) {
                        if(e==null){
                            if (collectButton.isChecked()==true) {
                                UserDiaryCollection adduserDiaryCollection=new UserDiaryCollection();
                                adduserDiaryCollection.setUsername(BmobUser.getCurrentUser(User.class));
                                adduserDiaryCollection.setUserdiaryID(object);
                                adduserDiaryCollection.save(new SaveListener() {
                                    @Override
                                    public void done(Object o, Object o2) {
                                        mListMineListViewContentCollect.clear();
                                        getMineListViewContentCollectList(userdiaryID);
                                    }

                                    @Override
                                    public void done(Object o, BmobException e) {
                                        Toast.makeText(ATYMineContent.this, "Add collection failed." + e.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                });
                            }else{
                                final Handler handler=new Handler(){
                                    @Override
                                    public void handleMessage(Message msg) {
                                        switch (msg.what){
                                            case 0:
                                                mListMineListViewContentCollect.clear();
                                                getMineListViewContentCollectList(userdiaryID);
                                                break;
                                        }

                                    }
                                };
                                BmobQuery<UserDiaryCollection> userDiaryCollectionUser = new BmobQuery<UserDiaryCollection>();
                                userDiaryCollectionUser.addWhereEqualTo("username", BmobUser.getCurrentUser(User.class));
                                BmobQuery<UserDiaryCollection> userDiaryCollectionDiaryID = new BmobQuery<UserDiaryCollection>();
                                userDiaryCollectionDiaryID.addWhereEqualTo("userdiaryID", object);
                                List<BmobQuery<UserDiaryCollection>> andQuerysUserDiaryCollection = new ArrayList<BmobQuery<UserDiaryCollection>>();
                                andQuerysUserDiaryCollection.add(userDiaryCollectionUser);
                                andQuerysUserDiaryCollection.add(userDiaryCollectionDiaryID);
                                BmobQuery<UserDiaryCollection> userDiaryCollection = new BmobQuery<UserDiaryCollection>();
                                userDiaryCollection.and(andQuerysUserDiaryCollection);
                                userDiaryCollection.findObjects(new FindListener<UserDiaryCollection>() {
                                    @Override
                                    public void done(List<UserDiaryCollection> object, BmobException e) {
                                        for (UserDiaryCollection list : object) {
                                            list.delete(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e==null){
                                                        Message message = handler.obtainMessage();
                                                        message.what = 0;
                                                        //以消息为载体
                                                        message.obj = 1;//这里的list就是查询出list
                                                        //向handler发送消息
                                                        handler.sendMessage(message);

                                                    }else{
                                                        Toast.makeText(ATYMineContent.this, "Delete collection failed.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });


                            }
                        }
                    }

                });

            }
        });


        //点赞
        likeTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobQuery<UserDiary> queryUserDiary = new BmobQuery<UserDiary>();
                queryUserDiary.getObject(userdiaryID, new QueryListener<UserDiary>() {
                    @Override
                    public void done(UserDiary object, BmobException e) {
                        if(e==null){
                            if (likeTextview.isChecked()==true) {
                                UserLikeDiary addUserLikeDiary=new UserLikeDiary();
                                addUserLikeDiary.setUsername(BmobUser.getCurrentUser(User.class));
                                addUserLikeDiary.setUserdiaryID(object);
                                addUserLikeDiary.save(new SaveListener() {
                                    @Override
                                    public void done(Object o, Object o2) {
                                        mListMineListViewContentLike.clear();
                                        getMineListViewContentLikeList(userdiaryID);

                                        Intent intent = new Intent(ATYMineContent.this, FragmentMine.class);
                                        intent.putExtra("IsAddLike", true);
                                        setResult(RESULT_ADD_NEW_LIKE_TRUE, intent);
                                    }

                                    @Override
                                    public void done(Object o, BmobException e) {
                                        Toast.makeText(ATYMineContent.this, "Praise failed." + e.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                });
                            }else{
                                final Handler handler=new Handler(){
                                    @Override
                                    public void handleMessage(Message msg) {
                                        switch (msg.what){
                                            case 0:
                                                mListMineListViewContentLike.clear();
                                                getMineListViewContentLikeList(userdiaryID);

                                                Intent intent = new Intent(ATYMineContent.this, FragmentMine.class);
                                                intent.putExtra("IsAddLike", true);
                                                setResult(RESULT_ADD_NEW_LIKE_TRUE, intent);

                                                break;
                                        }

                                    }
                                };

                                BmobQuery<UserLikeDiary> userLikeDiaryUser = new BmobQuery<UserLikeDiary>();
                                userLikeDiaryUser.addWhereEqualTo("username", BmobUser.getCurrentUser(User.class));
                                BmobQuery<UserLikeDiary> UserLikeDiaryDiaryID = new BmobQuery<UserLikeDiary>();
                                UserLikeDiaryDiaryID.addWhereEqualTo("userdiaryID", object);
                                List<BmobQuery<UserLikeDiary>> andQuerysUserLikeDiary = new ArrayList<BmobQuery<UserLikeDiary>>();
                                andQuerysUserLikeDiary.add(userLikeDiaryUser);
                                andQuerysUserLikeDiary.add(UserLikeDiaryDiaryID);
                                BmobQuery<UserLikeDiary> userLikeDiary = new BmobQuery<UserLikeDiary>();
                                userLikeDiary.and(andQuerysUserLikeDiary);
                                userLikeDiary.findObjects(new FindListener<UserLikeDiary>() {
                                    @Override
                                    public void done(List<UserLikeDiary> object, BmobException e) {
                                        for (UserLikeDiary list : object) {
                                            list.delete(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e==null){
                                                        if (e == null) {
                                                            Message message = handler.obtainMessage();
                                                            message.what = 0;
                                                            //以消息为载体
                                                            message.obj = 1;//这里的list就是查询出list
                                                            //向handler发送消息
                                                            handler.sendMessage(message);
                                                        }

                                                    }else{
                                                        Toast.makeText(ATYMineContent.this, "Delete like failed.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });


                            }
                        }
                    }

                });

            }
        });




        //查看评论
        findViewById(R.id.relativelayout_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.tv_my_guding_comments)).setTextColor(Color.parseColor("#212121"));
                ((TextView)findViewById(R.id.tv_layout_mine_item_content_my_comments)).setTextColor(Color.parseColor("#212121"));
                ((View)findViewById(R.id.view_comment)).setBackgroundColor(Color.parseColor("#f9232b"));

                ((TextView)findViewById(R.id.tv_my_guding_likes)).setTextColor(Color.parseColor("#9b9ea2"));
                ((TextView)findViewById(R.id.tv_layout_mine_item_content_my_likes)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)findViewById(R.id.view_like)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                ((TextView)findViewById(R.id.tv_my_guding_collects)).setTextColor(Color.parseColor("#9b9ea2"));
                ((TextView)findViewById(R.id.tv_layout_mine_item_content_my_collects)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)findViewById(R.id.view_collect)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                mListMineListViewContentComment.clear();
                mListView.setAdapter(mAdapterMineListViewContentComment);

                getMineListViewContentCommentList(userdiaryID);
            }
        });

        //查看赞
        findViewById(R.id.relativelayout_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.tv_my_guding_likes)).setTextColor(Color.parseColor("#212121"));
                ((TextView)findViewById(R.id.tv_layout_mine_item_content_my_likes)).setTextColor(Color.parseColor("#212121"));
                ((View)findViewById(R.id.view_like)).setBackgroundColor(Color.parseColor("#f9232b"));

                ((TextView)findViewById(R.id.tv_my_guding_collects)).setTextColor(Color.parseColor("#9b9ea2"));
                ((TextView)findViewById(R.id.tv_layout_mine_item_content_my_collects)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)findViewById(R.id.view_collect)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                ((TextView)findViewById(R.id.tv_my_guding_comments)).setTextColor(Color.parseColor("#9b9ea2"));
                ((TextView)findViewById(R.id.tv_layout_mine_item_content_my_comments)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)findViewById(R.id.view_comment)).setBackgroundColor(Color.parseColor("#d7d7d7"));
                mListMineListViewContentLike.clear();
                mListView.setAdapter(mAdapterMineListViewContentLike);

                getMineListViewContentLikeList(userdiaryID);
            }
        });
        //查看收藏
        findViewById(R.id.relativelayout_collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.tv_my_guding_collects)).setTextColor(Color.parseColor("#212121"));
                ((TextView)findViewById(R.id.tv_layout_mine_item_content_my_collects)).setTextColor(Color.parseColor("#212121"));
                ((View)findViewById(R.id.view_collect)).setBackgroundColor(Color.parseColor("#f9232b"));



                ((TextView)findViewById(R.id.tv_my_guding_likes)).setTextColor(Color.parseColor("#9b9ea2"));
                ((TextView)findViewById(R.id.tv_layout_mine_item_content_my_likes)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)findViewById(R.id.view_like)).setBackgroundColor(Color.parseColor("#d7d7d7"));

                ((TextView)findViewById(R.id.tv_my_guding_comments)).setTextColor(Color.parseColor("#9b9ea2"));
                ((TextView)findViewById(R.id.tv_layout_mine_item_content_my_comments)).setTextColor(Color.parseColor("#9b9ea2"));
                ((View)findViewById(R.id.view_comment)).setBackgroundColor(Color.parseColor("#d7d7d7"));
                mListMineListViewContentCollect.clear();
                mListView.setAdapter(mAdapterMineListViewContentCollect);
                getMineListViewContentCollectList(userdiaryID);
            }
        });


    }





    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO 取消所有未执行完的网络请求
        mQueuelike.cancelAll(this);
        mQueuecollect.cancelAll(this);
        mQueuecomment.cancelAll(this);

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

    void LoadInit(String ID){
        //BmobUser中的特定属性
        User bmobUser = BmobUser.getCurrentUser( User.class);
        String username = bmobUser.getUsername();
        //MyUser中的扩展属性
        String nickName = bmobUser.getNickName();
        BmobFile userheadpic = bmobUser.getUserHeadPic();
        TextView tvNickname=(TextView)findViewById(R.id.tv_layout_mine_item_content_nickname);
        if(nickName==""||nickName==null){
            tvNickname.setText(username);
        }
        else{
            tvNickname.setText(nickName);
        }
        if(userheadpic!=null){
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
            ImageLoader.ImageListener listener = ImageLoader.getImageListener((CircleImageView)findViewById(R.id.iv_layout_mine_item_content_head_pic),R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
            imageLoader.get(userheadpic.getUrl(), listener);

        }

        BmobQuery<UserDiary> query = new BmobQuery<UserDiary>();
        query.getObject(ID, new QueryListener<UserDiary>() {

            @Override
            public void done(UserDiary object, BmobException e) {
                if(e==null){
                    ((TextView)findViewById(R.id.tv_layout_mine_item_content_Lunch)).setText(object.getDiaryLunch());
                    ((TextView)findViewById(R.id.tv_layout_mine_item_content_Exercise)).setText(object.getDiaryExercise());
                    ((TextView)findViewById(R.id.tv_layout_mine_item_content_Self_evaluation)).setText(object.getDiarySelfEvaluation());
                    ((TextView)findViewById(R.id.tv_layout_mine_item_content_Time)).setText(object.getCreatedAt().toString());
                    ((TextView)findViewById(R.id.tv_layout_mime_item_content_Private_thing)).setText(object.getDiaryPrivate());


                    BmobQuery<UserDiaryCollection> userDiaryCollectionUser = new BmobQuery<UserDiaryCollection>();
                    userDiaryCollectionUser.addWhereEqualTo("username", BmobUser.getCurrentUser(User.class));
                    BmobQuery<UserDiaryCollection> userDiaryCollectionDiaryID = new BmobQuery<UserDiaryCollection>();
                    userDiaryCollectionDiaryID.addWhereEqualTo("userdiaryID", object);
                    List<BmobQuery<UserDiaryCollection>> andQuerysUserDiaryCollection = new ArrayList<BmobQuery<UserDiaryCollection>>();
                    andQuerysUserDiaryCollection.add(userDiaryCollectionUser);
                    andQuerysUserDiaryCollection.add(userDiaryCollectionDiaryID);
                    BmobQuery<UserDiaryCollection> userDiaryCollection = new BmobQuery<UserDiaryCollection>();
                    userDiaryCollection.and(andQuerysUserDiaryCollection);
                    userDiaryCollection.findObjects(new FindListener<UserDiaryCollection>() {
                        @Override
                        public void done(List<UserDiaryCollection> object, BmobException e) {
                            if(object.size()!=0){
                                collectButton.setChecked(true);
                            }else{
                                collectButton.setChecked(false);
                            }
                        }
                    });


                    BmobQuery<UserLikeDiary> userLikeDiaryUser = new BmobQuery<UserLikeDiary>();
                    userLikeDiaryUser.addWhereEqualTo("username", BmobUser.getCurrentUser(User.class));
                    BmobQuery<UserLikeDiary> userLikeDiaryDiaryID = new BmobQuery<UserLikeDiary>();
                    userLikeDiaryDiaryID.addWhereEqualTo("userdiaryID", object);
                    List<BmobQuery<UserLikeDiary>> andQuerysUserLikeDiary = new ArrayList<BmobQuery<UserLikeDiary>>();
                    andQuerysUserLikeDiary.add(userLikeDiaryUser);
                    andQuerysUserLikeDiary.add(userLikeDiaryDiaryID);
                    BmobQuery<UserLikeDiary> userLikeDiary = new BmobQuery<UserLikeDiary>();
                    userLikeDiary.and(andQuerysUserLikeDiary);
                    userLikeDiary.findObjects(new FindListener<UserLikeDiary>() {
                        @Override
                        public void done(List<UserLikeDiary> object, BmobException e) {
                            if(object.size()!=0){
                                likeTextview.setChecked(true);
                            }else{
                                likeTextview.setChecked(false);
                            }
                        }
                    });


                    BmobQuery<UserDiaryCollection> userDiaryCollectionDiaryNum = new BmobQuery<UserDiaryCollection>();
                    userDiaryCollectionDiaryNum.addWhereEqualTo("userdiaryID", object);
                    userDiaryCollectionDiaryNum.findObjects(new FindListener<UserDiaryCollection>() {
                        @Override
                        public void done(List<UserDiaryCollection> object, BmobException e) {
                            if(object.size()!=0){
                                tv_mycollectNum.setText(String.valueOf(object.size()));
                            }
                        }
                    });

                    BmobQuery<UserLikeDiary> userLikeDiaryNum = new BmobQuery<UserLikeDiary>();
                    userLikeDiaryNum.addWhereEqualTo("userdiaryID", object);
                    userLikeDiaryNum.findObjects(new FindListener<UserLikeDiary>() {
                        @Override
                        public void done(List<UserLikeDiary> object, BmobException e) {
                            if(object.size()!=0){
                                tv_mylikeNum.setText(String.valueOf(object.size()));
                            }
                        }
                    });
                    BmobQuery<UserComment> userCommentNum = new BmobQuery<UserComment>();
                    userCommentNum.addWhereEqualTo("userdiaryID", object);
                    userCommentNum.findObjects(new FindListener<UserComment>() {
                        @Override
                        public void done(List<UserComment> object, BmobException e) {
                            if(object.size()!=0){
                                tv_mycommentNum.setText(String.valueOf(object.size()));
                            }
                        }
                    });


                }
            }

        });




    }


    AdapterView.OnItemClickListener mMineListViewContentCommentAdapterListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            FragmentMine.MineListViewContent MineListViewContent = (FragmentMine.MineListViewContent) mAdapterMineListViewContent.getItem(position);
//
//            Context context;
//            context = getActivity().getApplicationContext();
//            Intent intent = new Intent(context, ATYMineContent.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("ID", MineListViewContent.getID());
//            intent.putExtras(bundle);
//            startActivity(intent);

        }
    };

    public void getMineListViewContentCommentList(String diaryID) {
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        List<UserComment> list= (List<UserComment>) msg.obj;
                        tv_mycommentNum.setText(String.valueOf(list.size()));

                        for (UserComment usercommentlist : list) {
                            String item0=usercommentlist.getObjectId();
                            String item1 = usercommentlist.getUsername().getUsername();
                            String item2 = usercommentlist.getUsername().getNickName();
                            BmobFile item3=usercommentlist.getUsername().getUserHeadPic();
                            String item4 = usercommentlist.getCommentContent();
                            String item5 = usercommentlist.getCreatedAt().toString();
                            ATYMineContent.MineListViewContentComment data=new ATYMineContent.MineListViewContentComment(item0,item1,item2,item3,item4,item5);
                            mListMineListViewContentComment.add(data);
                            mAdapterMineListViewContentComment.notifyDataSetChanged();

                        }
                        break;
                }

            }
        };



        BmobQuery<UserComment> query = new BmobQuery<UserComment>();
        UserDiary userDiary=new UserDiary();
        userDiary.setObjectId(diaryID);
        query.addWhereEqualTo("userdiaryID",new BmobPointer(userDiary));
        query.include("username");
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

    class MineListViewContentComment {
        //缺少一个头像
        private String Nickname;
        private String username;
        private BmobFile HeadPic;
        private String ID;
        private String Comments;
        private String Time;
        public MineListViewContentComment(String ID,String username,String Nickname,BmobFile HeadPic,String Comments,String Time) {

            this.ID=ID;
            this.username=username;
            this.HeadPic=HeadPic;
            this.Nickname=Nickname;
            this.Time=Time;
            this.Comments=Comments;
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


        public String getNickname() {
            return Nickname;
        }

        public String getTime() {
            return Time;
        }


        public String getComments() {
            return Comments;
        }
    }

    class MineListViewContentCommentAdapter extends BaseAdapter {
        private RequestQueue mQueue;
        private ImageLoader mImageLoader;
        public MineListViewContentCommentAdapter(Context context, RequestQueue queue) {
            super();
            mQueue = queue;
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }
        @Override
        public int getCount() {
            return mListMineListViewContentComment.size();
        }

        @Override
        public Object getItem(int position) {
            return mListMineListViewContentComment.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ATYMineContent.MineListViewContentCommentAdapter.ViewHolder viewHolder;
            View view = convertView;

            final ATYMineContent.MineListViewContentComment MineListViewContentComment = (ATYMineContent.MineListViewContentComment) getItem(position);
            Boolean mineComment=true;
            if(MineListViewContentComment.getUsername().toString()==BmobUser.getCurrentUser(User.class).toString()){
                mineComment=false;
            }else{
                mineComment=true;
            }

            if(view == null||mineComment) {
                view = ATYMineContent.this.getLayoutInflater().inflate(R.layout.layout_comment_content, null);
                viewHolder = new ATYMineContent.MineListViewContentCommentAdapter.ViewHolder();
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_comment_content_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_layout_comment_content_time);
                viewHolder.mCommentsTextView = (TextView) view.findViewById(R.id.tv_layout_comment_content);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_layout_comment_content_head_pic);


                view.setTag(viewHolder);


            } else {
                viewHolder = (ATYMineContent.MineListViewContentCommentAdapter.ViewHolder) view.getTag();
            }


            if(MineListViewContentComment.getNickname()==""||MineListViewContentComment.getNickname()==null){
                viewHolder.mNicknameTextView.setText(MineListViewContentComment.getUsername());
            }else{
                viewHolder.mNicknameTextView.setText(MineListViewContentComment.getNickname());
            }

            viewHolder.mTimeTextView.setText(MineListViewContentComment.getTime());
            viewHolder.mCommentsTextView.setText(MineListViewContentComment.getComments());

            ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mHeadPicCircleImageView,R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
            mImageLoader.get(MineListViewContentComment.getHeadPic().getUrl(), listener);


            viewHolder.mHeadPicCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AtyTheirsList.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", MineListViewContentComment.getUsername());
                    bundle.putString("nickname", MineListViewContentComment.getNickname());
                    bundle.putString("headpic", MineListViewContentComment.getHeadPic().getUrl());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });



            return view;
        }

        class ViewHolder {
            protected TextView mNicknameTextView;
            protected CircleImageView mHeadPicCircleImageView;
            protected TextView mCommentsTextView;
            protected TextView mTimeTextView;
        }
    }











    public void getMineListViewContentLikeList(String diaryID) {
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        List<UserLikeDiary> list= (List<UserLikeDiary>) msg.obj;
                        tv_mylikeNum.setText(String.valueOf(list.size()));

                        for (UserLikeDiary userLikeDiarylist : list) {
                            String item0=userLikeDiarylist.getObjectId();
                            String item1 = userLikeDiarylist.getUsername().getUsername();
                            String item2 = userLikeDiarylist.getUsername().getNickName();
                            BmobFile item3=userLikeDiarylist.getUsername().getUserHeadPic();
                            String item4 = userLikeDiarylist.getCreatedAt().toString();
                            ATYMineContent.MineListViewContentLikeOrCollect data=new ATYMineContent.MineListViewContentLikeOrCollect(item0,item1,item2,item3,item4);
                            mListMineListViewContentLike.add(data);
                            mAdapterMineListViewContentLike.notifyDataSetChanged();

                        }
                        break;
                }

            }
        };



        BmobQuery<UserLikeDiary> query = new BmobQuery<UserLikeDiary>();
        UserDiary userDiary=new UserDiary();
        userDiary.setObjectId(diaryID);
        query.addWhereEqualTo("userdiaryID",new BmobPointer(userDiary));
        query.include("username");
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

    public void getMineListViewContentCollectList(String diaryID) {
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        List<UserDiaryCollection> list= (List<UserDiaryCollection>) msg.obj;
                        tv_mycollectNum.setText(String.valueOf(list.size()));

                        for (UserDiaryCollection diaryCollectionlist : list) {
                            String item0=diaryCollectionlist.getObjectId();
                            String item1 = diaryCollectionlist.getUsername().getUsername();
                            String item2 = diaryCollectionlist.getUsername().getNickName();
                            BmobFile item3=diaryCollectionlist.getUsername().getUserHeadPic();
                            String item4 = diaryCollectionlist.getCreatedAt().toString();
                            ATYMineContent.MineListViewContentLikeOrCollect data=new ATYMineContent.MineListViewContentLikeOrCollect(item0,item1,item2,item3,item4);
                            mListMineListViewContentCollect.add(data);
                            mAdapterMineListViewContentCollect.notifyDataSetChanged();

                        }
                        break;
                }

            }
        };



        BmobQuery<UserDiaryCollection> query = new BmobQuery<UserDiaryCollection>();
        UserDiary userDiary=new UserDiary();
        userDiary.setObjectId(diaryID);
        query.addWhereEqualTo("userdiaryID",new BmobPointer(userDiary));
        query.include("username");
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

    class MineListViewContentLikeOrCollect {
        //缺少一个头像
        private String Nickname;
        private String username;
        private BmobFile HeadPic;
        private String ID;
        private String Time;
        public MineListViewContentLikeOrCollect(String ID,String username,String Nickname,BmobFile HeadPic,String Time) {

            this.ID=ID;
            this.username=username;
            this.HeadPic=HeadPic;
            this.Nickname=Nickname;
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


        public String getNickname() {
            return Nickname;
        }

        public String getTime() {
            return Time;
        }

    }

    class MineListViewContentLikeAdapter extends BaseAdapter {
        private RequestQueue mQueue;
        private ImageLoader mImageLoader;
        public MineListViewContentLikeAdapter(Context context, RequestQueue queue) {
            super();
            mQueue = queue;
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }
        @Override
        public int getCount() {
            return mListMineListViewContentLike.size();
        }

        @Override
        public Object getItem(int position) {
            return mListMineListViewContentLike.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ATYMineContent.MineListViewContentLikeAdapter.ViewHolder viewHolder;
            View view = convertView;

            final ATYMineContent.MineListViewContentLikeOrCollect MineListViewContentLikeOrCollect = (ATYMineContent.MineListViewContentLikeOrCollect) getItem(position);
            Boolean mineComment=true;
            if(MineListViewContentLikeOrCollect.getUsername().toString()==BmobUser.getCurrentUser(User.class).toString()){
                mineComment=false;
            }else{
                mineComment=true;
            }

            if(view == null||mineComment) {
                view = ATYMineContent.this.getLayoutInflater().inflate(R.layout.layout_like_collect_content, null);
                viewHolder = new ATYMineContent.MineListViewContentLikeAdapter.ViewHolder();
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_comment_content_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_layout_comment_content_time);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_layout_comment_content_head_pic);


                view.setTag(viewHolder);


            } else {
                viewHolder = (ATYMineContent.MineListViewContentLikeAdapter.ViewHolder) view.getTag();
            }


            if(MineListViewContentLikeOrCollect.getNickname()==""||MineListViewContentLikeOrCollect.getNickname()==null){
                viewHolder.mNicknameTextView.setText(MineListViewContentLikeOrCollect.getUsername());
            }else{
                viewHolder.mNicknameTextView.setText(MineListViewContentLikeOrCollect.getNickname());
            }

            viewHolder.mTimeTextView.setText(MineListViewContentLikeOrCollect.getTime());

            ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mHeadPicCircleImageView,R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
            mImageLoader.get(MineListViewContentLikeOrCollect.getHeadPic().getUrl(), listener);

            viewHolder.mHeadPicCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AtyTheirsList.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", MineListViewContentLikeOrCollect.getUsername());
                    bundle.putString("nickname", MineListViewContentLikeOrCollect.getNickname());
                    bundle.putString("headpic", MineListViewContentLikeOrCollect.getHeadPic().getUrl());
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



    class MineListViewContentCollectAdapter extends BaseAdapter {
        private RequestQueue mQueue;
        private ImageLoader mImageLoader;
        public MineListViewContentCollectAdapter(Context context, RequestQueue queue) {
            super();
            mQueue = queue;
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }
        @Override
        public int getCount() {
            return mListMineListViewContentCollect.size();
        }

        @Override
        public Object getItem(int position) {
            return mListMineListViewContentCollect.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ATYMineContent.MineListViewContentCollectAdapter.ViewHolder viewHolder;
            View view = convertView;

            final ATYMineContent.MineListViewContentLikeOrCollect MineListViewContentLikeOrCollect = (ATYMineContent.MineListViewContentLikeOrCollect) getItem(position);
            Boolean mineComment=true;
            if(MineListViewContentLikeOrCollect.getUsername().toString()==BmobUser.getCurrentUser(User.class).toString()){
                mineComment=false;
            }else{
                mineComment=true;
            }

            if(view == null||mineComment) {
                view = ATYMineContent.this.getLayoutInflater().inflate(R.layout.layout_like_collect_content, null);
                viewHolder = new ATYMineContent.MineListViewContentCollectAdapter.ViewHolder();
                viewHolder.mNicknameTextView = (TextView) view.findViewById(R.id.tv_comment_content_nickname);
                viewHolder.mTimeTextView = (TextView) view.findViewById(R.id.tv_layout_comment_content_time);
                viewHolder.mHeadPicCircleImageView = (CircleImageView) view.findViewById(R.id.iv_layout_comment_content_head_pic);


                view.setTag(viewHolder);


            } else {
                viewHolder = (ATYMineContent.MineListViewContentCollectAdapter.ViewHolder) view.getTag();
            }


            if(MineListViewContentLikeOrCollect.getNickname()==""||MineListViewContentLikeOrCollect.getNickname()==null){
                viewHolder.mNicknameTextView.setText(MineListViewContentLikeOrCollect.getUsername());
            }else{
                viewHolder.mNicknameTextView.setText(MineListViewContentLikeOrCollect.getNickname());
            }

            viewHolder.mTimeTextView.setText(MineListViewContentLikeOrCollect.getTime());

            ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mHeadPicCircleImageView,R.mipmap.ic_account_circle_black_24dp, R.mipmap.ic_account_circle_black_24dp);
            mImageLoader.get(MineListViewContentLikeOrCollect.getHeadPic().getUrl(), listener);




            viewHolder.mHeadPicCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AtyTheirsList.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", MineListViewContentLikeOrCollect.getUsername());
                    bundle.putString("nickname", MineListViewContentLikeOrCollect.getNickname());
                    bundle.putString("headpic", MineListViewContentLikeOrCollect.getHeadPic().getUrl());
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
