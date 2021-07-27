package com.renbin.dynamicload;

import android.app.Application;

import com.renbin.skin_library.SkinManager;

/**
 * data:2021-07-27
 * Author:renbin
 */
public class MyApp  extends Application {
    public static MyApp appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        //在application初始化context
        SkinManager.getInstance().setContext(this);
    }
}
