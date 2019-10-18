package org.leaffun.alpha;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookUtil {

    private static String SP_NAME = "my";
    private static String REGEX = "x,1x";
    private static String CATEGORY_KEY = "category";


    /**
     * 添加记录详情
     * @param context
     * @param item
     * @param itemDetail
     * @return
     */
    public static boolean addItemDetail(Context context, String item, String itemDetail) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("item-"+item, code(itemDetail));
        return editor.commit();
    }


    /**
     * 获取记录详情
     * @param context
     * @param item
     * @return
     */
    public static String getItemDetail(Context context, String item) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        String itemDetail = sp.getString("item-"+item, "");
        return itemDetail;
    }

    /**
     * 追加一条记录
     * @param context
     * @param one
     * @return
     */
    public static  boolean addItem(Context context,String category,String one) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        String categoryList = sp.getString(category, "");
        if(TextUtils.isEmpty(categoryList)){
            categoryList = one+REGEX;
        }else{
            categoryList= uncode(categoryList);
            categoryList += one+REGEX;
        }


        SharedPreferences.Editor editor = sp.edit();
        editor.putString(category, code(categoryList));
        return editor.commit();
    }

    /**
     * 获取某分类下的所有记录
     * @param context
     * @param category
     * @return
     */
    public static List<String> getItems(Context context,String category) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        String categoryList = sp.getString(category, "");
        if(!TextUtils.isEmpty(categoryList)){
            categoryList= uncode(categoryList);

            return new ArrayList<>(Arrays.asList(categoryList.split(REGEX)));
        }else{
            return null;
        }
    }

    /**
     * 删除某个分类下的所有记录
     * @param context
     * @param category
     * @return
     */
    public static  boolean removeItems(Context context,String category) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.remove(category);
        return editor.commit();
    }


    /**
     * 追加一个分类
     * @param context
     * @param one
     * @return
     */
    public static  boolean addCategory(Context context,String one) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);


        String categoryList = sp.getString(CATEGORY_KEY, "");
        if(TextUtils.isEmpty(categoryList)){
            categoryList = one+REGEX;
        }else{
            categoryList= uncode(categoryList);
            categoryList += one+REGEX;
        }


        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CATEGORY_KEY, code(categoryList));
        return editor.commit();
    }

    /**
     * 获取所有分类
     * @param context
     * @return
     */
    public static  List<String> getCategory(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        String categoryList = sp.getString(CATEGORY_KEY, "");
        if(!TextUtils.isEmpty(categoryList)){
            categoryList= uncode(categoryList);
            return new ArrayList<>(Arrays.asList(categoryList.split(REGEX)));
        }else{
            return null;
        }
    }

    /**
     * 覆盖分类
     * @param context
     * @param list
     * @return
     */
    public static  boolean putCategory(Context context,List<String> list) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        StringBuilder categoryList = new StringBuilder();
        if(list!= null && list.size()>0){
            for(String s : list){
                categoryList.append(s).append(REGEX);
            }
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CATEGORY_KEY, code(categoryList.toString()));
        return editor.commit();
    }


    /**
     * 解密
     * @param str
     * @return
     */
    public static  String uncode(String str){

        return str;
    }

    /**
     * 加密
     * @param str
     * @return
     */
    public static  String code(String str){

        return str;
    }

    public static void toast(Context context, String str){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
    }
}
