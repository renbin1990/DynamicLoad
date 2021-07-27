package com.renbin.dynamicload;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.renbin.skin_library.SkinManager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * data:2021-07-26
 * Author:renbin
 */
class SkinFactory implements LayoutInflater.Factory2{
    //安卓所有控件都是在这三种下
    private static  final  String[] prxfixList = {
      "android.widget.",
      "android.view.",
      "android.webkit"
    };
    //收集需要换肤的控件容器
    private List<SkinView> mViewList = new ArrayList<>();

    //批量换肤
    public void apply(){
        for (SkinView skinView :mViewList){
            skinView.apply();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        Log.e("------>","11111111111111111111  "+name);
        //收集需要换肤的控件

        View view = null;
        if (name.contains(".")){
            //带包名控件 类似于androidx.constraintlayout.widget.ConstraintLayout
            view = onCreateView(name, context, attrs);
        }else {
            //不带包名控件，类似于TextView LinearLayout
            for (String s : prxfixList){
                String viewName = s+name;
                view = onCreateView(viewName, context, attrs);
                if (view!=null){
                    break;
                }
            }
        }

        //收集需要换肤的控件
        if (view!= null){
            paserView(view,name,attrs);
        }
        return view;
    }

    /**
     * 收集需要换肤的控件
     * @param view
     * @param name
     * @param attrs
     */
    private void paserView(View view, String name, AttributeSet attrs) {
        List<SkinItem> skinItems = new ArrayList<>();
        //遍历的是这个当前进来的控件的所有属性
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            //得到属性名字
            String attributeName = attrs.getAttributeName(i);
            if (attributeName.contains("background") || attributeName.contains("textColor")|| attributeName.contains("src")){
                //认为是要收集的换肤的控件
                String attributeValue = attrs.getAttributeValue(i);
                //获取资源文件id
                int resId = Integer.parseInt(attributeValue.substring(1));
                //获取资源id的类型
                String resourceTypeName = view.getResources().getResourceTypeName(resId);
                //获取资源id的名字
                String resourceEntryName = view.getResources().getResourceEntryName(resId);
                SkinItem skinItem = new SkinItem(attributeName,resourceTypeName,resourceEntryName,resId);
                skinItems.add(skinItem);
            }
        }

        /**
         * 如果一个控件集合大小大于0 说明需要换肤
         */
        if (skinItems.size() >0){
            //需要更换，则添加到集合中
            SkinView skinView = new SkinView(view,skinItems);
            mViewList.add(skinView);
        }
    }

    /**
     * 控件实例化
     * @param name
     * @param context
     * @param attrs
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = null;
        try {
            Class<?> aClass = context.getClassLoader().loadClass(name);
            //获取到第二个构造方法
            Constructor<? extends View> constructor = (Constructor<? extends View>) aClass.getConstructor(Context.class, AttributeSet.class);
            view = constructor.newInstance(context, attrs);
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    /**
     * 需要换肤的控件封装对象
     */
    class SkinView{
        View view;
        List<SkinItem> skinItems;

        public SkinView(View view, List<SkinItem> skinItems) {
            this.view = view;
            this.skinItems = skinItems;
        }

        public void apply(){
            for (SkinItem skinItem : skinItems){
                //判断这条属性是background吗？
                if (skinItem.getName().equals("background")){
                    //如果是backound，设置背景有两种方式一种是color设置颜色，一种是drawable和mipmap设置背景
                    if (skinItem.getTypeName().equals("color")){
                        //将资源id 传给SkinManager 去进行资源匹配，如果匹配到了，就直接设置给控件
                        //如果没有匹配到，就把之前的资源id设置给控件
                        if (SkinManager.getInstance().resourceIsNull()){
                            view.setBackgroundResource(SkinManager.getInstance().getColor(skinItem.getResId()));
                        }else {
                            view.setBackgroundColor(SkinManager.getInstance().getColor(skinItem.getResId()));
                        }
                    }else if (skinItem.getTypeName().equals("drawable")||skinItem.getTypeName().equals("mipmap")){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                            view.setBackground(SkinManager.getInstance().getDrawable(skinItem.getResId()));
                        }else {
                            view.setBackgroundDrawable(SkinManager.getInstance().getDrawable(skinItem.getResId()));
                        }
                    }
                }else if (skinItem.getName().equals("src")){
                    if (skinItem.getTypeName().equals("drawable")||skinItem.getTypeName().equals("mipmap")){
                        ((ImageView)view).setImageResource(SkinManager.getInstance().getDrawableID(skinItem.getResId()));
                    }else if (skinItem.getTypeName().equals("color")){
                        ((ImageView)view).setImageResource(SkinManager.getInstance().getColor(skinItem.getResId()));
                    }
                }else if (skinItem.getName().equals("textColor")){
                    ((TextView)view).setTextColor(SkinManager.getInstance().getColor(skinItem.getResId()));
                }
            }
        }
    }

    class SkinItem{
        //属性名字 textcocor text background
        String name;
        //属性的值类型 color mipmap
        String typeName;
        //属性的值的名字
        String entryName;
        //属性的资源id
        int resId;

        public SkinItem(String name, String typeName, String entryName, int resId) {
            this.name = name;
            this.typeName = typeName;
            this.entryName = entryName;
            this.resId = resId;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getEntryName() {
            return entryName;
        }

        public void setEntryName(String entryName) {
            this.entryName = entryName;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }
    }
}
