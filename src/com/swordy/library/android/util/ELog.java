package com.swordy.library.android.util;

import com.swordy.library.android.BuildConfig;

public final class ELog
{
    
    public static final int VERBOSE = android.util.Log.VERBOSE;
    
    public static final int DEBUG = android.util.Log.DEBUG;
    
    public static final int INFO = android.util.Log.INFO;
    
    public static final int WARN = android.util.Log.WARN;
    
    public static final int ERROR = android.util.Log.ERROR;
    
    public static final int ASSERT = android.util.Log.ASSERT;
    
    private static long previousTimeMillis = System.currentTimeMillis();
    
    private static boolean enabled;
    
    private static boolean allEnabled;
    
    public static void setEnabled(boolean enable)
    {
        enabled = enable;
    }
    
    public static void seAllEnabled(boolean enable)
    {
        allEnabled = enable;
    }
    
    public static boolean enable()
    {
        return enabled && allEnabled;
    }
    
    public static void d(String tag, String msg)
    {
        if (enable())
        {
            android.util.Log.d(tag, msg);
        }
    }
    
    public static void d(String tag, String msg, Throwable tr)
    {
        if (enable())
        {
            android.util.Log.d(tag, msg, tr);
        }
    }
    
    public static void e(String tag, String msg)
    {
        if (enable())
        {
            android.util.Log.e(tag, msg);
        }
    }
    
    public static void e(String tag, String msg, Throwable tr)
    {
        if (enable())
        {
            android.util.Log.e(tag, msg, tr);
        }
    }
    
    public static void i(String tag, String msg)
    {
        if (enable())
        {
            android.util.Log.i(tag, msg);
        }
    }
    
    public static void i(String tag, String msg, Throwable tr)
    {
        if (enable())
        {
            android.util.Log.i(tag, msg, tr);
        }
    }
    
    public static void v(String tag, String msg)
    {
        if (enable())
        {
            android.util.Log.v(tag, msg);
        }
    }
    
    public static void v(String tag, String msg, Throwable tr)
    {
        if (enable())
        {
            android.util.Log.v(tag, msg, tr);
        }
    }
    
    public static void w(String tag, String msg)
    {
        if (enable())
        {
            android.util.Log.w(tag, msg);
        }
    }
    
    public static void w(String tag, String msg, Throwable tr)
    {
        if (enable())
        {
            android.util.Log.w(tag, msg, tr);
        }
    }
    
    public static void w(String tag, Throwable tr)
    {
        if (enable())
        {
            android.util.Log.w(tag, tr);
        }
    }
    
    public static void wtf(String tag, String msg)
    {
        if (enable())
        {
            android.util.Log.wtf(tag, msg);
        }
    }
    
    public static void wtf(String tag, String msg, Throwable tr)
    {
        if (enable())
        {
            android.util.Log.wtf(tag, msg, tr);
        }
    }
    
    public static void wtf(String tag, Throwable tr)
    {
        if (enable())
        {
            android.util.Log.wtf(tag, tr);
        }
    }
    
    public static void currentThreadInfo(String tag)
    {
        if (enable())
        {
            Thread thr = Thread.currentThread();
            ELog.v(tag, "Thread[id:" + thr.getId() + ",name:" + thr.getName() + "priority:" + thr.getPriority()
                + "group:" + thr.getThreadGroup().getName() + "]");
        }
    }
    
    public static void currentTimeMillis(String tag, String msg)
    {
        if (enable())
        {
            long current = System.currentTimeMillis();
            ELog.v(tag, msg + "timeMillis[current: " + current + " , duration: " + (previousTimeMillis - current) + "]");
            previousTimeMillis = current;
        }
    }
}
