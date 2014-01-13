package com.swordy.library.android.util;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class DrawableUtil
{
    public static final String TAG = "AndroidLibrary.DrawableUtil";
    
    /**
     * HashMap<Context id instead of hashCode, HashMap<image resource id, decoded bitmap>>
     */
    private static HashMap<Integer, HashMap<Integer, Bitmap>> mBitmapTracker =
        new HashMap<Integer, HashMap<Integer, Bitmap>>();
    
    private static Options mOpts;
    
    public static synchronized Options getOptimizeOptions(Resources res)
    {
        if (mOpts != null)
        {
            return mOpts;
        }
        
        DisplayMetrics dm = res.getDisplayMetrics();
        mOpts = new Options();
        mOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        mOpts.inDensity = dm.densityDpi;
        mOpts.inPurgeable = true;
        mOpts.inInputShareable = true;
        return mOpts;
    }
    
    /**
     * 释放已记录的所有Bitmap
     * @param trackerId 用于记录使用DrawableUtil.getDrawable
     * @see DrawableUtil.{@link #addDrawableTracker(int)}.
     * @see DrawableUtil.{@link #recycleAll()}.
     */
    public static void recycle(Context context)
    {
        recycle(context.hashCode());
    }
    
    private static void recycle(int contextId)
    {
        Log.v(TAG, "recycle context : " + contextId);
        HashMap<Integer, Bitmap> tracker = mBitmapTracker.get(contextId);
        if (tracker == null)
        {
            return;
        }
        
        Set<Entry<Integer, Bitmap>> entries = tracker.entrySet();
        for (Entry<Integer, Bitmap> entry : entries)
        {
            int resId = entry.getKey();
            if (containInOtherContext(contextId, resId))
            {
                Log.v(TAG, "containInOtherContext : " + resId);
                continue;
            }
            
            Bitmap bmp = entry.getValue();
            if (bmp != null && !bmp.isRecycled())
            {
                Log.v(TAG, "recyle bitmap > context : " + contextId + " res: " + entry.getKey());
                bmp.recycle();
            }
        }
        tracker.clear();
        
        mBitmapTracker.remove(contextId);
        Log.v(TAG, "mBitmapTracker size: " + mBitmapTracker.size());
        
        if (mBitmapTracker.size() == 0)
        {
            mOpts = null;
        }
    }
    
    /**
     * 释放所有记录的Bitmap
     * @see DrawableUtil.{@link #addDrawableTracker(int)}.
     * @see DrawableUtil.{@link #recycle(int)}.
     */
    public static void recycleAll()
    {
        Set<Integer> keys = mBitmapTracker.keySet();
        for (Integer trackerId : keys)
        {
            recycle(trackerId);
        }
    }
    
    /**
     * 判断资源是否在其它地方还在使用，如果是，则不进行释放
     * @param contextId 当前需要释放该资源的Context id
     * @param resId 需要释放的resource id
     * @return true 表示该资源仍在其他地方使用中
     */
    private static boolean containInOtherContext(int contextId, int resId)
    {
        // TODO 该轮询费劲
        Set<Entry<Integer, HashMap<Integer, Bitmap>>> entries = mBitmapTracker.entrySet();
        for (Entry<Integer, HashMap<Integer, Bitmap>> entry : entries)
        {
            if (contextId == entry.getKey())
            {
                continue;
            }
            
            HashMap<Integer, Bitmap> value = entry.getValue();
            if (value.containsKey(resId))
            {
                return true;
            }
        }
        return false;
    }
    
    public static Drawable getDrawable(Context context, int resId)
    {
        return getDrawable(context, resId, getOptimizeOptions(context.getResources()));
    }
    
    public static Drawable getDrawable(Context context, int resId, Options opts)
    {
        if (resId == View.NO_ID)
        {
            return null;
        }
        
        Bitmap bitmap = null;
        Resources res = context.getResources();
        
        int contextId = context.hashCode();
        HashMap<Integer, Bitmap> tracker = mBitmapTracker.get(contextId);
        
        // 创建新的追踪器
        if (tracker == null)
        {
            tracker = new HashMap<Integer, Bitmap>();
            mBitmapTracker.put(contextId, tracker);
            Log.v(TAG, "add new tracker > context : " + contextId);
        }
        
        // 如果该资源已经解码，则直接返回解码过的Bitmap
        Set<Entry<Integer, HashMap<Integer, Bitmap>>> entries = mBitmapTracker.entrySet();
        for (Entry<Integer, HashMap<Integer, Bitmap>> entry : entries)
        {
            HashMap<Integer, Bitmap> value = entry.getValue();
            if (value.containsKey(resId))
            {
                Log.v(TAG, "repeat resource : " + resId);
                bitmap = value.get(resId);
                tracker.put(resId, bitmap);
                break;
            }
        }
        
        if (bitmap == null)
        {
            bitmap = BitmapFactory.decodeResource(res, resId, opts);
            tracker.put(resId, bitmap);
            Log.v(TAG, "track bitmap > context : " + contextId + ", res : " + resId + ", size: " + tracker.size());
        }
        
        return new BitmapDrawable(res, bitmap);
    }
    
    public static Drawable getDrawable(Context context, int normalId, int pressedId)
    {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[] {android.R.attr.state_pressed}, getDrawable(context, pressedId));
        drawable.addState(new int[] {}, getDrawable(context, normalId));
        return drawable;
    }
    
    public static Drawable getDrawable(Context context, int normalId, int pressedId, int checkedId)
    {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[] {android.R.attr.state_pressed}, getDrawable(context, pressedId));
        drawable.addState(new int[] {android.R.attr.state_checked}, getDrawable(context, checkedId));
        drawable.addState(new int[] {}, getDrawable(context, normalId));
        return drawable;
    }
    
    public static LevelListDrawable getLevelListDrawable(Context context, int... resId)
    {
        if (resId == null)
        {
            return null;
        }
        
        LevelListDrawable d = new LevelListDrawable();
        for (int id : resId)
        {
            d.addLevel(id, id, getDrawable(context, id));
        }
        return d;
    }
    
    public static AnimationDrawable getAnimationDrawable(Context context, int[] resIds, int duration, boolean oneShot)
    {
        int length = resIds.length;
        int dur = duration / length;
        int[] durations = new int[length];
        for (int i = 0; i != length; i++)
        {
            durations[i] = dur;
        }
        
        return getAnimationDrawable(context, resIds, durations, oneShot);
    }
    
    public static AnimationDrawable getAnimationDrawable(Context context, int[] resIds, int[] durations, boolean oneShot)
    {
        if (resIds == null || durations == null || resIds.length == 0 || durations.length == 0)
        {
            return null;
        }
        
        if (resIds.length != durations.length)
        {
            throw new IllegalArgumentException("resources not mapping with durations");
        }
        
        AnimationDrawable drawable = new AnimationDrawable();
        drawable.setOneShot(oneShot);
        
        for (int i = 0; i != resIds.length; i++)
        {
            drawable.addFrame(getDrawable(context, resIds[i]), durations[i]);
        }
        return drawable;
    }
    
}
