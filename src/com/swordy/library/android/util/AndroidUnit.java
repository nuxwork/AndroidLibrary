package com.swordy.library.android.util;

import android.content.Context;


public class AndroidUnit
{
    private static final String TAG = "AndroidLibrary.AndroidUnit";
    
    public static float density = -1f;
    
    public static void init(Context context)
    {
        density = context.getApplicationContext().getResources().getDisplayMetrics().density;
    }
    
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue)
    {
        if (density == -1f)
            throw new RuntimeException("error density, call AndroidUnit.init(Application) first.");
        return (int)(dpValue * density + 0.5f);
    }
    
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue)
    {
        if (density == -1f)
            throw new RuntimeException("error density, call AndroidUnit.init(Application) first.");
        return (int)(pxValue / density + 0.5f);
    }
}
