package com.example.joseph.slimdiary.DB;

import cn.bmob.v3.BmobObject;

/**
 * Created by Joseph on 2017/3/19.
 */

public class UserComment extends BmobObject {
    User username;
    UserDiary userdiaryID;
    String commentContent;

    public String getCommentContent() {
        return commentContent;
    }

    public User getUsername() {
        return username;
    }

    public UserDiary getUserdiaryID() {
        return userdiaryID;
    }

    public void setUsername(User username) {
        this.username = username;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public void setUserdiaryID(UserDiary userdiaryID) {
        this.userdiaryID = userdiaryID;
    }


}
