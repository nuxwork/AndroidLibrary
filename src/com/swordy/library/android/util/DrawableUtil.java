package com.swordy.library.android.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.view.View;

public class DrawableUtil
{
    public static final String TAG = "AndroidLibrary.DrawableUtil";
    
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
}
