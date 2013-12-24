package com.swordy.library.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class DrawableUtil
{
    public static final String TAG = "RemoteClient.DrawableUtil";
    
    private static HashMap<Integer, ArrayList<Bitmap>> mBitmapTracker = new HashMap<Integer, ArrayList<Bitmap>>();
    
    private static int mCurrentTrackerId;
    
    private static Options mOpts;
    
    public static Options getOptimizeOptions(Resources res)
    {
        if (mOpts != null)
            return mOpts;
        
        DisplayMetrics dm = res.getDisplayMetrics();
        mOpts = new Options();
        mOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        mOpts.inDensity = dm.densityDpi;
        mOpts.inPurgeable = true;
        mOpts.inInputShareable = true;
        return mOpts;
    }
    
    public static Drawable getDrawable(Resources res, int resId)
    {
        return getDrawable(res, resId, getOptimizeOptions(res));
    }
    
    private static Drawable getDrawable(Resources res, int resId, Options opts)
    {
        if (resId != View.NO_ID)
        {
            Bitmap bitmap = BitmapFactory.decodeResource(res, resId, opts);
            ArrayList<Bitmap> tracker = getCurrentTracker();
            if (tracker == null)
            {
                throw new RuntimeException("tracker is null, make sure set tracker first");
            }
            
            getCurrentTracker().add(bitmap);
            return new BitmapDrawable(res, bitmap);
        }
        else
        {
            return null;
        }
    }
    
    public static Drawable getDrawable(Resources res, int normalId, int pressedId)
    {
        Options opts = getOptimizeOptions(res);
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[] {android.R.attr.state_pressed}, getDrawable(res, pressedId, opts));
        drawable.addState(new int[] {}, getDrawable(res, normalId, opts));
        return drawable;
    }
    
    public static Drawable getDrawable(Resources res, int normalId, int pressedId, int checkedId)
    {
        Options opts = getOptimizeOptions(res);
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[] {android.R.attr.state_pressed}, getDrawable(res, pressedId, opts));
        drawable.addState(new int[] {android.R.attr.state_checked}, getDrawable(res, checkedId, opts));
        drawable.addState(new int[] {}, getDrawable(res, normalId, opts));
        return drawable;
    }
    
    public static AnimationDrawable getAnimationDrawable(Resources res, int[] resIds, int[] durations, boolean oneShot)
    {
        if (resIds == null || durations == null || resIds.length == 0 || durations.length == 0)
            return null;
        
        if (resIds.length != durations.length)
        {
            throw new IllegalArgumentException("resources not mapping with durations");
        }
        
        Options opts = getOptimizeOptions(res);
        AnimationDrawable drawable = new AnimationDrawable();
        drawable.setOneShot(oneShot);
        
        for (int i = 0; i != resIds.length; i++)
        {
            drawable.addFrame(getDrawable(res, resIds[i], opts), durations[i]);
        }
        return drawable;
    }
    
    private static ArrayList<Bitmap> getCurrentTracker()
    {
        return mBitmapTracker.get(mCurrentTrackerId);
    }
    
    /**
     * @see DrawableUtil.{@link #recycle(int)}
     * @see DrawableUtil.{@link #recycleAll()}
     */
    public static void addDrawableTracker(int trackerId)
    {
        if (mBitmapTracker.containsKey(trackerId))
        {
            throw new IllegalArgumentException("the tracker id has been used.");
        }
        
        ArrayList<Bitmap> tracker = new ArrayList<Bitmap>();
        mBitmapTracker.put(trackerId, tracker);
    }
    
    public static void setCurrentTrackerId(int trackerId)
    {
        mCurrentTrackerId = trackerId;
    }
    
    /**
     * 释放已记录的所有Bitmap
     * @param trackerId 用于记录使用DrawableUtil.getDrawable
     * @see DrawableUtil.{@link #addDrawableTracker(int)}.
     * @see DrawableUtil.{@link #recycleAll()}.
     */
    public static void recycle(int trackerId)
    {
        ArrayList<Bitmap> tracker = mBitmapTracker.get(trackerId);
        for (Bitmap bmp : tracker)
        {
            if (bmp != null && !bmp.isRecycled())
            {
                Log.v(TAG, "recyle bitmap...");
                bmp.recycle();
            }
        }
        mBitmapTracker.remove(trackerId);
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
}
