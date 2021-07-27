package com.renbin.skin_library;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.lang.reflect.Method;

import androidx.core.content.ContextCompat;

/**
 * data:2021-07-26
 * Author:renbin
 * 加载皮肤资源对象
 */
public class SkinManager {

    private static  SkinManager sSkinManager;
    //shangxiawen
    private Context mContext;
    //资源包包名
    private String packageName;
    //资源包里面的资源对象
    private Resources mResources;

    public static  SkinManager getInstance(){
        if (sSkinManager ==null){
            sSkinManager = new SkinManager();
        }
        return sSkinManager;
    }

    public void setContext(Context context){
        this.mContext = context;
    }

    /**
     * 根据路径加载皮肤包
     * @param path
     */
    public void loadSkinApk(String path){
        //获取到包管理器
        PackageManager packageManager = mContext.getPackageManager();
        //获取资源包包信息类
        PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        //获取资源对象
        packageName = packageArchiveInfo.packageName;
        try{
            //通过反射获取assetManager对象
            AssetManager assetManager = AssetManager.class.newInstance();
            //通过反射获取addAssetPath方向
            Method addAssetPath = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager,path);
            //得到了资源包里面的资源对象
            mResources = new Resources(assetManager,mContext.getResources().getDisplayMetrics(),mContext.getResources().getConfiguration());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取颜色资源
     * @param resid     当前app更换资源的资源id
     * @return 匹配到的资源对象的资源id
     */
    public int getColor(int resid){
        if (resourceIsNull()){
            return resid;
        }
        //获取资源id的类型
        String resourceTypeName = mContext.getResources().getResourceTypeName(resid);
        //获取资源id的名字
        String resourceEntryName = mContext.getResources().getResourceEntryName(resid);
        //去匹配，获取外部pak资源的id去匹配
        int identifier = mResources.getIdentifier(resourceEntryName, resourceTypeName, packageName);
        if (identifier == 0){
            return  resid;
        }
        return mResources.getColor(identifier);
    }

    /**
     * 从资源包中获取资源id对应的资源
     * @param id
     * @return
     */
    public Drawable getDrawable(int id){
        if (resourceIsNull()){
            return ContextCompat.getDrawable(mContext,id);
        }
        //获取资源id的类型
        String resourceTypeName = mContext.getResources().getResourceTypeName(id);
        //获取资源id的名字
        String resourceEntryName = mContext.getResources().getResourceEntryName(id);
        //去匹配，获取外部pak资源的id去匹配
        int identifier = mResources.getIdentifier(resourceEntryName, resourceTypeName, packageName);
        if (identifier ==0){
            return ContextCompat.getDrawable(mContext,id);
        }
        return mResources.getDrawable(identifier);
    }

    /**
     * 从资源包中获取资源id对应的资源
     * @param id
     * @return
     */
    public int getDrawableID(int id){
        if (resourceIsNull()){
            return id;
        }
        //获取资源id的类型
        String resourceTypeName = mContext.getResources().getResourceTypeName(id);
        //获取资源id的名字
        String resourceEntryName = mContext.getResources().getResourceEntryName(id);
        //去匹配，获取外部pak资源的id去匹配
        int identifier = mResources.getIdentifier(resourceEntryName, resourceTypeName, packageName);
        if (identifier ==0){
            return id;
        }
        return identifier;
    }

    /**
     * 判断资源包的资源对象是否为空
     * @return
     */
    public boolean resourceIsNull(){
        if (mResources == null){
            return  true;
        }
        return false;
    }
}
