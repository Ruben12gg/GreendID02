package com.example.greenid_esmad;

import android.app.Application;

public class GLOBALS extends Application {

    private String userIdGlobal;
    private String userName;

    public String getUserPfp() {
        return userPfp;
    }

    public void setUserPfp(String userPfp) {
        this.userPfp = userPfp;
    }

    private String userPfp;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIdGlobal() {
        return userIdGlobal;
    }

    public void setUserIdGlobal(String userIdGlobal) {
        this.userIdGlobal = userIdGlobal;
    }

    public void setUserIdG(String userId) {
    }
}
