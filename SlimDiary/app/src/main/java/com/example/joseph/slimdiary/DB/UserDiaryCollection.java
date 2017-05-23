package com.example.joseph.slimdiary.DB;

import cn.bmob.v3.BmobObject;

/**
 * Created by Joseph on 2017/3/21.
 */

public class UserDiaryCollection extends BmobObject {
    public User username;
    public UserDiary userdiaryID;

    public UserDiary getUserdiaryID() {
        return userdiaryID;
    }

    public void setUserdiaryID(UserDiary userdiaryID) {
        this.userdiaryID = userdiaryID;
    }

    public User getUsername() {
        return username;
    }

    public void setUsername(User username) {
        this.username = username;
    }

}
