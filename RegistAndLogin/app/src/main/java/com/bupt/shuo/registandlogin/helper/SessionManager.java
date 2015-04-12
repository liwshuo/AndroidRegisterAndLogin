package com.bupt.shuo.registandlogin.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 用于管理用户的登陆状态
 * Created by shuo on 2015/4/8.
 */
public class SessionManager {
    private static String TAG = SessionManager.class.getSimpleName();

    SharedPreferences pref;
    Editor editor;
    Context mContext;

    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "AndroidLogin";
    private static final String IS_LOGGEDIN = "isLoggedIn";

    public SessionManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //设置用户的登陆状态
    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(IS_LOGGEDIN, isLoggedIn);
        editor.commit();
    }
    //获取用户的登陆状态
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGEDIN, false);
    }
}
