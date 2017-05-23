package com.example.joseph.slimdiary.DB;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Joseph on 2017/3/16.
 */

public class User extends BmobUser {

    //这个BmobFile是特有的，我们可以用来上传我们的图片(头像资源)
    private BmobFile userHeadPic;

    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public BmobFile getUserHeadPic() {
        return userHeadPic;
    }

    public void setUserHeadPic(BmobFile userHeadPic) {
        this.userHeadPic = userHeadPic;
    }
}