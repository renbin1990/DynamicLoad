package com.renbin.dynamicload;

import android.os.Bundle;
import android.view.LayoutInflater;

import java.lang.reflect.Field;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;

/**
 * data:2021-07-26
 * Author:renbin
 */
class BaseActivity extends AppCompatActivity {

    private SkinFactory mSkinFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //解决继承AppCompatActivity SkinFactory创建报错
            setLayoutInflaterFactory(getLayoutInflater());
            mSkinFactory = new SkinFactory();
            LayoutInflaterCompat.setFactory2(getLayoutInflater(), mSkinFactory);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void apply() {
        mSkinFactory.apply();
    }

    /**
     * 改变mFactorySet的值，使继承AppCompatActivity不报错
     * 需要设置targetSdkVersion 28才有效，29 30会报错，需要额外适配
     *
     * @param origonal
     */
    public void setLayoutInflaterFactory(LayoutInflater origonal) {
        LayoutInflater layoutInflater = origonal;
        try {
            Field mFactorySet = LayoutInflater.class.getDeclaredField("mFactorySet");
            mFactorySet.setAccessible(true);
            mFactorySet.set(layoutInflater, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //其他隐藏的activiry进行资源替换
        mSkinFactory.apply();
    }
}
