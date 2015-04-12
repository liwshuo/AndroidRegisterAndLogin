package com.bupt.shuo.registandlogin.helper;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * 用于提供全局变量,包括request对列
 */
public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue myRequestQueue;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    //获取requestqueue
    public RequestQueue getMyRequestQueue() {
        if (myRequestQueue == null) {
            myRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return myRequestQueue;
    }

    //向requestqueue中添加自定义tag的request
    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getMyRequestQueue().add(request);
    }

    //向requestqueue中添加带默认tag的request
    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        getMyRequestQueue().add(request);
    }

    //删除所有tag的request
    public void cancelPendingRequests(Object tag) {
        if (myRequestQueue != null) {
            myRequestQueue.cancelAll(tag);
        }
    }

}