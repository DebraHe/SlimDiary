package com.example.joseph.slimdiary.DB;

import cn.bmob.v3.BmobObject;

/**
 * Created by Joseph on 2017/3/21.
 */

public class UserLikeUser extends BmobObject {
    public User usernameMe;
    public User usernameFriend;

    public User getUsernameFriend() {
        return usernameFriend;
    }

    public void setUsernameFriend(User usernameFriend) {
        this.usernameFriend = usernameFriend;
    }

    public User getUsernameMe() {
        return usernameMe;
    }

    public void setUsernameMe(User usernameMe) {
        this.usernameMe = usernameMe;
    }
}
