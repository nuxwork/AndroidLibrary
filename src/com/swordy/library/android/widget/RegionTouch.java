package com.swordy.library.android.widget;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 区域化的View：用于解决一些不规则形状的或非独立图形的点击事件。
 * 
 * 首先将每个区域用唯一的颜色值标识起来，不能使用{@link #NO_COLOR}，将各区域对应的颜色和点击
 * 后的状态图保存在一个区域-状态映射表里{@link #setRegionMap(Drawable, Map)}通过触摸时获得的坐标点x
 * 和y，调用getPixelColor(x, y)来检索区域-状态映射表中的颜色，最后映射到指定的区域，设置该区域的状态图。
 * 
 * @author  yWX191142
 */
public class RegionTouch extends View
{
    private static final String TAG = RegionTouch.class.getSimpleName();
    
    private static final int MASK_PRESSED = 0x00000001;
    
    /**
     * 区域-状态映射表是否更新
     */
    private static final int MASK_UPDATE_REGION_MAP = 0x00000002;
    
    /**
     * 状态图是动态加载还是静态加载，动态加载可以防止图片过大导致内存消耗过大
     */
    private static final int MASK_IS_DYNAMIC_LOAD = 0x00000004;
    
    private int mPrivateFlags = 0;
    
    private long mEventTime = System.currentTimeMillis();
    
    /**
     * 无效的区域颜色为0x00000000，即透明色。
     */
    public static final int NO_COLOR = 0x00000000;
    
    /**
     * 区域划分图：将每个区域用唯一的颜色标示出来，通过触摸时获得的坐标点x和y，
     * 调用getPixelColor(x, y)来检索区域-状态映射表中的颜色，最后映射到指定的区域。
     * 需要注意的是，各区域不可重叠，重叠部分不能正确映射。
     */
    private Bitmap mRegion;
    
    /**
     * 区域-状态图映射表，key为标识区域的唯一颜色，value为该区域对应的状态图
     */
    private Map<Integer, Object> mRegionMap;
    
    private int mPressedRegionColor = 0;
    
    private Drawable mPressedRegionDrawable = null;
    
    private Context mContext;
    
    private Rect mDrawingRect;
    
    private OnRegionClickedListener mOnRegionClickedListener;
    
    private OnRegionLongClickedListener mOnRegionLongClickedListener;
    
    private OnRegionTouchListener mOnRegionTouchListener;
    
    public static interface OnRegionTouchListener
    {
        boolean onRegionTouch(View v, int regionColor, MotionEvent event);
    }
    
    public static interface OnRegionClickedListener
    {
        void onRegionClicked(View v, int regionColor);
    }
    
    public static interface OnRegionLongClickedListener
    {
        void onRegionLongClicked(View v, int regionColor);
    }
    
    public RegionTouch(Context context)
    {
        super(context);
        onCreate(context, null, 0);
    }
    
    public RegionTouch(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        onCreate(context, attrs, 0);
    }
    
    public RegionTouch(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        onCreate(context, attrs, defStyle);
    }
    
    protected void onCreate(Context context, AttributeSet attrs, int defStyle)
    {
        mContext = context;
        mRegionMap = new HashMap<Integer, Object>();
        mDrawingRect = new Rect();
        setClickable(true);
    }
    
    public void setOnRegionTouchListener(OnRegionTouchListener listener)
    {
        if (mOnRegionTouchListener == listener)
            return;
        
        mOnRegionTouchListener = listener;
        setClickable(true);
    }
    
    public void setOnRegionClickedListener(OnRegionClickedListener listener)
    {
        if (mOnRegionClickedListener == listener)
            return;
        
        mOnRegionClickedListener = listener;
        setClickable(true);
    }
    
    public void setOnRegionLongClickedListener(OnRegionLongClickedListener listener)
    {
        if (mOnRegionLongClickedListener == listener)
            return;
        
        mOnRegionLongClickedListener = listener;
        setClickable(true);
    }
    
    /**
     * 设置区域划分图和区域-状态图映射表
     * @param regionResid 区域划分图的资源id
     * @param colors 标识区域的唯一颜色集，即区域-状态图映射表的key。颜色值不能为{@link #NO_COLOR}
     * @param drawableIds 区域对应的状态图资源id集，即区域-状态图映射表的value。
     * colors 和 stateResIds 必须一一对应
     * @see #setRegionMap(Drawable, Map)
     * @see #setRegionMap(int, int[], int[], boolean)
     * @throws IllegalArgumentException 颜色值与状态图数量不一致
     */
    public void setRegionMap(int regionResid, int[] colors, int[] stateResIds)
    {
        setRegionMap(regionResid, colors, stateResIds, false);
    }
    
    /**
     * 设置区域划分图和区域-状态图映射表
     * @param regionResid 区域划分图的资源id
     * @param colors 标识区域的唯一颜色集，即区域-状态图映射表的key。颜色值不能为{@link #NO_COLOR}
     * @param drawableIds 区域对应的状态图资源id集，即区域-状态图映射表的value。
     * colors 和 stateResIds 必须一一对应
     * @param isDynamic 状态图是动态加载还是静态加载，动态加载可以防止图片过大导致内存消耗过大
     * @see #setRegionMap(Drawable, Map)
     * @throws IllegalArgumentException 颜色值与状态图数量不一致
     */
    public void setRegionMap(int regionResid, int[] colors, int[] stateResIds, boolean isDynamicLoad)
    {
        Resources res = mContext.getResources();
        mRegion = BitmapFactory.decodeResource(res, regionResid);
        
        if (colors == null || colors.length == 0 || stateResIds == null || stateResIds.length == 0)
        {
            requestLayout(); // refresh for mRegion but mRegionMap
            return;
        }
        else if (colors.length != stateResIds.length)
            throw new IllegalArgumentException(
                "colors are not mapping with drawableIds, colors length must keep with drawableIds");
        
        mRegionMap.clear();
        
        for (int i = 0; i != colors.length; i++)
        {
            if (colors[i] == NO_COLOR)
            {
                throw new IllegalArgumentException("a region color can not be NO_COLOR(0)");
            }
            
            if (isDynamicLoad)
            {
                mRegionMap.put(colors[i], stateResIds[i]);
                mPrivateFlags |= MASK_IS_DYNAMIC_LOAD;
            }
            else
            {
                mPrivateFlags &= ~MASK_IS_DYNAMIC_LOAD;
                Drawable d = res.getDrawable(stateResIds[i]);
                mRegionMap.put(colors[i], d);
            }
        }
        mPrivateFlags |= MASK_UPDATE_REGION_MAP;
        requestLayout();
    }
    
    /**
     * 设置区域划分图和区域-状态图映射表
     * 
     * @param region 区域划分图 将每个区域用唯一的颜色标示出来，通过触摸时获得的坐标点x和y，
     * 调用Bitmap.getPixel(x, y)来检索区域-状态映射表中的颜色，最后映射到指定的区域。
     * 需要注意的是，各区域不可重叠，重叠部分不能正确映射。
     * 
     * @param regionMap 区域-状态图映射表，key为标识区域的唯一颜色，value为该区域对应的状态图
     * @throws IllegalArgumentException 区域的颜色不能为{@link #NO_COLOR}
     */
    public void setRegionMap(Bitmap region, Map<Integer, Drawable> regionMap)
    {
        
        Set<Entry<Integer, Drawable>> entrySet = regionMap.entrySet();
        for (Entry<Integer, Drawable> entry : entrySet)
        {
            if (entry.getKey() == NO_COLOR)
            {
                throw new IllegalArgumentException("a region color can not be NO_COLOR(0)");
            }
            
            mRegionMap.put(entry.getKey(), entry.getValue());
        }
        
        mRegion = region;
        mPrivateFlags |= MASK_UPDATE_REGION_MAP;
        mPrivateFlags &= ~MASK_IS_DYNAMIC_LOAD;
        requestLayout();
    }
    
    /**
    * 设置区域划分图和区域-状态图映射表
    * 
    * @param region 区域划分图 将每个区域用唯一的颜色标示出来，通过触摸时获得的坐标点x和y，
    * 调用Bitmap.getPixel(x, y)来检索区域-状态映射表中的颜色，最后映射到指定的区域。
    * 需要注意的是，各区域不可重叠，重叠部分不能正确映射。
    * 
    * @param regionMap 区域-状态图映射表，key为标识区域的唯一颜色，value为该区域对应的状态图
    * @param isDynamicLoad 状态图是动态加载还是静态加载，动态加载可以防止图片过大导致内存消耗过大
    * @throws IllegalArgumentException 区域的颜色不能为{@link #NO_COLOR}
    */
    public void setRegionMap(Bitmap region, Map<Integer, Integer> regionMap, boolean isDynamicLoad)
    {
        Resources res = mContext.getResources();
        Set<Entry<Integer, Integer>> entrySet = regionMap.entrySet();
        for (Entry<Integer, Integer> entry : entrySet)
        {
            if (entry.getKey() == NO_COLOR)
            {
                throw new IllegalArgumentException("a region color can not be NO_COLOR(0)");
            }
            
            if (isDynamicLoad)
            {
                mPrivateFlags |= MASK_IS_DYNAMIC_LOAD;
                mRegionMap.put(entry.getKey(), entry.getValue());
            }
            else
            {
                mPrivateFlags &= ~MASK_IS_DYNAMIC_LOAD;
                Drawable d = res.getDrawable(entry.getValue());
                mRegionMap.put(entry.getKey(), d);
            }
        }
        
        mRegion = region;
        mPrivateFlags |= MASK_UPDATE_REGION_MAP;
        requestLayout();
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
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        
        //---- keep same size ----
        if (mRegion != null && (getWidth() != mRegion.getWidth() || getHeight() != mRegion.getHeight()))
        {
            Bitmap bmp = Bitmap.createScaledBitmap(mRegion, getWidth(), getHeight(), false);
            mRegion.recycle();
            mRegion = bmp;
        }
        
    }
    
    @Override
    public boolean onTouchEvent(final MotionEvent event)
    {
        if (mRegion == null || mRegionMap == null || mRegionMap.size() == 0)
            return super.onTouchEvent(event);
        
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mEventTime = System.currentTimeMillis();
                if (!isEnabled())
                    return super.onTouchEvent(event);
                
                int x = (int)event.getX();
                int y = (int)event.getY();
                
                if (x < 0 || x > mRegion.getWidth() || y < 0 || y > mRegion.getHeight())
                    break;
                
                int regionColor = mRegion.getPixel(x, y);
                if (!mRegionMap.keySet().contains(regionColor))
                {
                    mPressedRegionColor = NO_COLOR;
                }
                else
                {
                    if ((mPrivateFlags & MASK_IS_DYNAMIC_LOAD) == MASK_IS_DYNAMIC_LOAD
                        && mPressedRegionColor != regionColor)
                    {
                        mPressedRegionDrawable = getResources().getDrawable((Integer)mRegionMap.get(regionColor));
                    }
                    mPressedRegionColor = regionColor;
                }
                
                invalidate();
                
                if (mPressedRegionColor != NO_COLOR)
                {
                    if (mOnRegionTouchListener != null)
                    {
                        mOnRegionTouchListener.onRegionTouch(this, mPressedRegionColor, event);
                    }
                    else if (mOnRegionClickedListener != null)
                    {
                        performClick();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                long duration = System.currentTimeMillis() - mEventTime;
                postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        final int color = mPressedRegionColor;
                        boolean regionPressed = mPressedRegionColor != NO_COLOR;
                        mPressedRegionColor = NO_COLOR;
                        invalidate();
                        if (regionPressed && mOnRegionTouchListener != null)
                        {
                            mOnRegionTouchListener.onRegionTouch(RegionTouch.this, color, event);
                        }
                    }
                }, ViewConfiguration.getPressedStateDuration() - duration);
                return true;
        }
        return super.onTouchEvent(event);
    }
    
    @Override
    public boolean performClick()
    {
        if (mOnRegionClickedListener != null)
        {
            mOnRegionClickedListener.onRegionClicked(this, mPressedRegionColor);
        }
        return true;
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        
        if (mPressedRegionColor != NO_COLOR)
        {
            Drawable d = null;
            if ((mPrivateFlags & MASK_IS_DYNAMIC_LOAD) == MASK_IS_DYNAMIC_LOAD)
            {
                d = mPressedRegionDrawable;
            }
            else
            {
                d = (Drawable)mRegionMap.get(mPressedRegionColor);
            }
            
            if (d != null)
            {
                d.setBounds(0, 0, getWidth(), getHeight());
                d.draw(canvas);
            }
        }
    }
}
