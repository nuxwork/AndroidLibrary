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
        Bitmap bitmap = BitmapFactory.decodeResource(res, resId, getOptimizeOptions(res));
        return new BitmapDrawable(res, bitmap);
    }
    
    public static Drawable getDrawable(Resources res, int normalId, int pressedId)
    {
        Options opts = getOptimizeOptions(res);
        Bitmap bmpNormal = BitmapFactory.decodeResource(res, normalId, opts);
        Bitmap bmpPressed = BitmapFactory.decodeResource(res, pressedId, opts);
        
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[] {android.R.attr.state_pressed}, new BitmapDrawable(res, bmpPressed));
        drawable.addState(new int[] {}, new BitmapDrawable(res, bmpNormal));
        return drawable;
    }
    public static Drawable getDrawable(Resources res, int normalId, int pressedId, int checkedId)
    {
        Options opts = getOptimizeOptions(res);
        Bitmap bmpNormal = BitmapFactory.decodeResource(res, normalId, opts);
        Bitmap bmpPressed = BitmapFactory.decodeResource(res, pressedId, opts);
        Bitmap bmpChecked = BitmapFactory.decodeResource(res, checkedId, opts);
        
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[] {android.R.attr.state_pressed}, new BitmapDrawable(res, bmpPressed));
        drawable.addState(new int[] {android.R.attr.state_checked}, new BitmapDrawable(res, bmpChecked));
        drawable.addState(new int[] {}, new BitmapDrawable(res, bmpNormal));
        return drawable;
    }
    
    public static AnimationDrawable getAnimationDrawable(Resources res, int[] resIds, int[] durations)
    {
        if (resIds == null || durations == null || resIds.length == 0 || durations.length == 0)
            return null;
        
        if (resIds.length != durations.length)
        {
            throw new IllegalArgumentException("resources not mapping with durations");
        }
        
        Options opts = getOptimizeOptions(res);
        AnimationDrawable drawable = new AnimationDrawable();
        
        for (int i = 0; i != resIds.length; i++)
        {
            Bitmap bitmap = BitmapFactory.decodeResource(res, resIds[i], opts);
            drawable.addFrame(new BitmapDrawable(res, bitmap), durations[i]);
        }
        return drawable;
    }
}
