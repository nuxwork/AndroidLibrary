package com.swordy.library.android.widget;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * @author  yWX191142
 */
public class RegionButton extends View
{
    private static final String TAG = RegionButton.class.getSimpleName();
    
    private static final int NO_REGION_ID = 0xffffffff;
    
    private static final int MSG_CHECK_LONG_PRESSED = 0;
    
    private static final int FLAG_REGION_PRESSED = 0x00000001;
    
    private static final int FLAG_REGION_LONG_PRESSED = 0x00000002;
    
    private int mFlags;
    
    private Context mContext;
    
    private Rect mDrawingRect;
    
    private Options mOptions;
    
    private int[] mRegionDrawableIds;
    
    private int[] mRegionIds;
    
    private int mPressedRegionId;
    
    private LevelListDrawable mRegionDrawable;
    
    private WeakReference<Bitmap>[] mRefRegionDrawableBitmaps;
    
    private Bitmap mRegionMapBitmap;
    
    private OnRegionClickListener mOnRegionClickListener;
    
    private OnRegionLongClickListener mOnRegionLongClickListener;
    
    private OnRegionTouchListener mOnRegionTouchListener;
    
    private long mPressedTime = System.currentTimeMillis();
    
    public RegionButton(Context context)
    {
        super(context);
        onCreate(context, null, 0);
    }
    
    public RegionButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        onCreate(context, attrs, 0);
    }
    
    public RegionButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        onCreate(context, attrs, defStyle);
    }
    
    protected void onCreate(Context context, AttributeSet attrs, int defStyle)
    {
        mContext = context;
        mDrawingRect = new Rect();
        setClickable(true);
        
        if (attrs == null)
        {
            return;
        }
    }
    
    public void setOnRegionTouchListener(OnRegionTouchListener listener)
    {
        if (mOnRegionTouchListener == listener)
            return;
        
        mOnRegionTouchListener = listener;
        setClickable(true);
    }
    
    public void setOnRegionClickListener(OnRegionClickListener listener)
    {
        if (mOnRegionClickListener == listener)
            return;
        
        mOnRegionClickListener = listener;
        setClickable(true);
    }
    
    public void setOnRegionLongClickListener(OnRegionLongClickListener listener)
    {
        if (mOnRegionLongClickListener == listener)
            return;
        
        mOnRegionLongClickListener = listener;
        setLongClickable(true);
    }
    
    public void setRegionMap(int regionMapId, int[] regionIds, int[] regionDrawableIds)
    {
        Options opts = new Options();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        opts = new Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inDensity = dm.densityDpi;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        
        setRegionMap(regionMapId, regionIds, regionDrawableIds, opts);
    }
    
    public void setRegionMap(int regionMapId, int[] regionIds, int[] regionDrawableIds, Options opts)
    {
        Resources res = getResources();
        mRegionDrawableIds = regionDrawableIds;
        mRegionIds = regionIds;
        mOptions = opts;
        
        mRegionMapBitmap = BitmapFactory.decodeResource(res, regionMapId, opts);
        
        if (regionIds == null || regionIds.length == 0 || regionDrawableIds == null || regionDrawableIds.length == 0)
        {
            requestLayout(); // refresh for mRegion but mRegionMap
            return;
        }
        else if (regionDrawableIds.length != regionIds.length)
        {
            throw new IllegalArgumentException("colors are not mapping with drawables");
        }
        
        mRegionDrawable = new LevelListDrawable();
        mRefRegionDrawableBitmaps = new WeakReference[regionIds.length];
        int stateId = NO_ID;
        for (int i = 0; i != regionIds.length; i++)
        {
            stateId = regionDrawableIds[i];
            Bitmap bmpState = BitmapFactory.decodeResource(res, stateId, opts);
            if (bmpState == null)
                continue;
            
            mRefRegionDrawableBitmaps[i] = new WeakReference<Bitmap>(bmpState);
            BitmapDrawable drawable = new BitmapDrawable(res, getWeakBitmap(mRefRegionDrawableBitmaps[i], stateId));//.get());
            mRegionDrawable.addLevel(regionIds[i], regionIds[i], drawable);
        }
        
        mRegionDrawable.setLevel(NO_REGION_ID);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = 0;
        int height = 0;
        
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        Drawable background = getBackground();
        
        // Record our dimensions if they are known;
        if (widthMode != MeasureSpec.UNSPECIFIED)
        {
            if (background != null)
                width = background.getIntrinsicWidth();
        }
        
        if (heightMode != MeasureSpec.UNSPECIFIED)
        {
            if (background != null)
                height = background.getIntrinsicHeight();
        }
        
        if (widthMode == MeasureSpec.EXACTLY)
        {
            width = widthSize;
        }
        
        if (heightMode == MeasureSpec.EXACTLY)
        {
            height = heightSize;
        }
        
        setMeasuredDimension(width, height);
        
        getDrawingRect(mDrawingRect);
    }
    
    /**
     * @param tag true is pointer to width, and false is pointer to height
     */
    private int getMaxSize(Drawable[] drawables, boolean tag)
    {
        int max = 0;
        int cur = 0;
        for (Drawable d : drawables)
        {
            if (d != null)
            {
                if (tag)
                    cur = d.getIntrinsicWidth();
                else
                    cur = d.getIntrinsicHeight();
                
                if (cur > max)
                    max = cur;
            }
        }
        return max;
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        getDrawingRect(mDrawingRect);
        
        Bitmap bmp = mRegionMapBitmap;
        if (bmp != null)
        {
            if (bmp != null && (getWidth() != bmp.getWidth() || getHeight() != bmp.getHeight()))
            {
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, mDrawingRect.width(), mDrawingRect.height(), false);
                bmp.recycle();
                mRegionMapBitmap = scaledBmp;
            }
        }
    }
    
    private Bitmap getWeakBitmap(WeakReference<Bitmap> ref, int resId)
    {
        Bitmap bmp = ref.get();
        if (bmp == null)
        {
            
            Bitmap b = BitmapFactory.decodeResource(getResources(), resId, mOptions);
            ref = new WeakReference<Bitmap>(b);
            bmp = ref.get();
        }
        return bmp;
    }
    
    private boolean isRegionPressed()
    {
        return (mFlags & FLAG_REGION_PRESSED) == FLAG_REGION_PRESSED;
    }
    
    private boolean isRegionLongPressed()
    {
        return (mFlags & FLAG_REGION_LONG_PRESSED) == FLAG_REGION_LONG_PRESSED;
    }
    
    private boolean hasRegion(int regionColor)
    {
        for (int i = 0; i != mRegionIds.length; i++)
        {
            if (regionColor == mRegionIds[i])
                return true;
        }
        return false;
    }
    
    private void checkForRegionLongClick(long delayMillis)
    {
        mFlags &= ~FLAG_REGION_LONG_PRESSED;
        mHandler.sendEmptyMessageDelayed(MSG_CHECK_LONG_PRESSED, delayMillis);
    }
    
    private void removeRegionLongPressCallback()
    {
        mHandler.removeMessages(MSG_CHECK_LONG_PRESSED);
        mFlags &= ~FLAG_REGION_LONG_PRESSED;
    }
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_CHECK_LONG_PRESSED:
                    mFlags |= FLAG_REGION_LONG_PRESSED;
                    if (mPressedRegionId != NO_REGION_ID)
                        perfermRegionLongClick(mPressedRegionId);
                    break;
            }
        }
    };
    
    private int getColorFromBitmap(Bitmap bmp, int x, int y)
    {
        if (x < 0 || x >= bmp.getWidth() || y < 0 || y >= bmp.getHeight())
            return NO_REGION_ID;
        
        return bmp.getPixel(x, y);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        
        if (mRegionMapBitmap == null || mRegionIds == null || mRegionDrawable == null)
        {
            return super.onTouchEvent(event);
        }
        
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                final int x = (int)event.getX();
                final int y = (int)event.getY();
                
                int id = getColorFromBitmap(mRegionMapBitmap, x, y);
                if (id == NO_REGION_ID)
                {
                    break;
                }
                
                mPressedRegionId = id;
                setPressed(true);
                invalidate();
                mPressedTime = System.currentTimeMillis();
                
                if (isEnabled())
                {
                    if (hasRegion(id))
                    {
                        mFlags |= FLAG_REGION_PRESSED;
                        if (!perfermRegionTouch(id, event) && isLongClickable())
                        {
                            checkForRegionLongClick(ViewConfiguration.getLongPressTimeout());
                        }
                    }
                }
                return true;
            }
            case MotionEvent.ACTION_UP:
            {
                if (mPressedRegionId == NO_REGION_ID)
                    break;
                
                int id = mPressedRegionId;
                mPressedRegionId = NO_REGION_ID;
                setPressed(false);
                long duration = System.currentTimeMillis() - mPressedTime;
                postInvalidateDelayed(ViewConfiguration.getPressedStateDuration() - duration);
                
                removeRegionLongPressCallback();
                if (isEnabled())
                {
                    if (!perfermRegionTouch(id, event) && isRegionPressed())
                    {
                        perfermRegionClick(id);
                    }
                }
                mFlags &= ~FLAG_REGION_PRESSED;
                return true;
            }
            case MotionEvent.ACTION_CANCEL:
            {
                if (mPressedRegionId == NO_REGION_ID)
                    break;
                
                int id = mPressedRegionId;
                mPressedRegionId = NO_REGION_ID;
                setPressed(false);
                invalidate();
                mFlags &= ~FLAG_REGION_PRESSED;
                
                removeRegionLongPressCallback();
                if (isEnabled())
                {
                    return perfermRegionTouch(id, event);
                }
                return true;
            }
            case MotionEvent.ACTION_MOVE:
            {
                if (mPressedRegionId == NO_REGION_ID)
                    break;
                
                final int x = (int)event.getX();
                final int y = (int)event.getY();
                
                int id = getColorFromBitmap(mRegionMapBitmap, x, y);
                if (id != mPressedRegionId)
                {
                    removeRegionLongPressCallback();
                    mPressedRegionId = NO_REGION_ID;
                    mFlags &= ~FLAG_REGION_PRESSED;
                    setPressed(false);
                    invalidate();
                }
                else if (perfermRegionTouch(mPressedRegionId, event))
                    ;
                return true;
            }
            default:
            {
                if (mPressedRegionId == NO_REGION_ID)
                    break;
                
                final int x = (int)event.getX();
                final int y = (int)event.getY();
                
                int id = getColorFromBitmap(mRegionMapBitmap, x, y);
                if (id != NO_REGION_ID)
                {
                    if (perfermRegionTouch(id, event))
                        return true;
                    else
                        return super.onTouchEvent(event);
                }
            }
        }
        return super.onTouchEvent(event);
    }
    
    private void perfermRegionClick(int regionid)
    {
        OnRegionClickListener l = mOnRegionClickListener;
        if (l != null)
        {
            l.onRegionClick(this, regionid);
        }
    }
    
    private void perfermRegionLongClick(int regionId)
    {
        OnRegionLongClickListener l = mOnRegionLongClickListener;
        if (l != null)
        {
            l.onRegionLongClick(this, regionId);
        }
    }
    
    private boolean perfermRegionTouch(int regionId, MotionEvent event)
    {
        OnRegionTouchListener l = mOnRegionTouchListener;
        if (l != null)
        {
            return l.onRegionTouch(this, regionId, event);
        }
        return false;
    }
    
    @Override
    protected void drawableStateChanged()
    {
        if (mRegionDrawable != null)
            mRegionDrawable.setLevel(mPressedRegionId);
        super.drawableStateChanged();
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        
        super.onDraw(canvas);
        
        if (mRegionDrawable != null)
        {
            Drawable current = mRegionDrawable.getCurrent();
            if (current != null)
            {
                if (current instanceof BitmapDrawable)
                {
                    Bitmap bmp = ((BitmapDrawable)current).getBitmap();
                    if (bmp == null)
                    {
                        Log.e(TAG, "bitmap has been released!");
                    }
                }
                current.setBounds(mDrawingRect);
                current.draw(canvas);
            }
        }
    }
    
    public static interface OnRegionTouchListener
    {
        boolean onRegionTouch(RegionButton v, int id, MotionEvent event);
    }
    
    public static interface OnRegionClickListener
    {
        void onRegionClick(RegionButton v, int id);
    }
    
    public static interface OnRegionLongClickListener
    {
        void onRegionLongClick(RegionButton v, int id);
    }
}
