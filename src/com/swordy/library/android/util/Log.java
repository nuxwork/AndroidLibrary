package com.swordy.library.android.util;

import com.swordy.library.android.BuildConfig;

public final class Log
{
    public static final int ASSERT = android.util.Log.ASSERT;
    
    public static final int DEBUG = android.util.Log.DEBUG;
    
    public static final int ERROR = android.util.Log.ERROR;
    
    public static final int INFO = android.util.Log.INFO;
    
    public static final int VERBOSE = android.util.Log.VERBOSE;
    
    public static final int WARN = android.util.Log.WARN;
    
    private static long mPreTimeMillis = System.currentTimeMillis();
    
    public static void d(String tag, String msg)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.d(tag, msg);
    }
    
    public static void d(String tag, String msg, Throwable tr)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.d(tag, msg, tr);
    }
    
    public static void e(String tag, String msg)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.e(tag, msg);
    }
    
    public static void e(String tag, String msg, Throwable tr)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.e(tag, msg, tr);
    }
    
    public static void i(String tag, String msg)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.i(tag, msg);
    }
    
    public static void i(String tag, String msg, Throwable tr)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.i(tag, msg, tr);
    }
    
    public static void v(String tag, String msg)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.v(tag, msg);
    }
    
    public static void v(String tag, String msg, Throwable tr)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.v(tag, msg, tr);
    }
    
    public static void w(String tag, String msg)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.w(tag, msg);
    }
    
    public static void w(String tag, String msg, Throwable tr)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.w(tag, msg, tr);
    }
    
    public static void w(String tag, Throwable tr)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.w(tag, tr);
    }
    
    public static void wtf(String tag, String msg)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.wtf(tag, msg);
    }
    
    public static void wtf(String tag, String msg, Throwable tr)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.wtf(tag, msg, tr);
    }
    
    public static void wtf(String tag, Throwable tr)
    {
        if (BuildConfig.DEBUG)
            android.util.Log.wtf(tag, tr);
    }
    
    public static void currentThreadInfo(String tag)
    {
        if (BuildConfig.DEBUG)
        {
            Thread thr = Thread.currentThread();
            Log.v(tag, "Thread[id:" + thr.getId() + ",name:" + thr.getName() + "priority:" + thr.getPriority()
                + "group:" + thr.getThreadGroup().getName() + "]");
        }
    }
    
    public static void currentTimeMillis(String tag, String msg)
    {
        long current = System.currentTimeMillis();
        Log.v(tag, msg + "timeMillis[current: " + current + " , duration: " + (mPreTimeMillis - current) + "]");
        mPreTimeMillis = current;
    }
}
