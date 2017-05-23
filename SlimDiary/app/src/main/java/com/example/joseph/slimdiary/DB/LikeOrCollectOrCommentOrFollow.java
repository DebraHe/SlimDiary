package com.example.joseph.slimdiary.DB;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Joseph on 2017/3/24.
 */

public class LikeOrCollectOrCommentOrFollow {
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
