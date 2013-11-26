package com.swordy.library.android.util;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class PreferenceHelper
{
    private static final String TAG = "AndroidLibrary.PreferenceHelper";
    
    private static final String PREFERENCE_NAME = "default_preference";
    
    private static final String KEY_ENABLE_VIBRATE = "enable_vibrate";
    
    private static Application mApp;
    
    private static boolean mIsVibrateEnabled;
    
    private static SharedPreferences mPreference;
    
    private static final PreferenceHelper mInstance = new PreferenceHelper();
    
    private static PreferenceHelperListener mListener;
    
    private PreferenceHelper()
    {
    }
    
    public static PreferenceHelper getInstance(Application app)
    {
        mApp = app;
        mInstance.init();
        return mInstance;
    }
    
    private void init()
    {
        mPreference = mApp.getSharedPreferences(PREFERENCE_NAME, Application.MODE_WORLD_WRITEABLE);
        mIsVibrateEnabled = mPreference.getBoolean(KEY_ENABLE_VIBRATE, false);
        mListener = new PreferenceHelperListener();
        mPreference.registerOnSharedPreferenceChangeListener(mListener);
    }
    
    /**
     * clear all preference
     */
    public void clear()
    {
        Editor e = mPreference.edit();
        e.clear();
        e.commit();
    }
    
    public void release()
    {
        mPreference.unregisterOnSharedPreferenceChangeListener(mListener);
        mListener = null;
        mPreference = null;
        mApp = null;
    }
    
    public void setVibrateEnabled(boolean enbaled)
    {
        Editor e = mPreference.edit();
        e.putBoolean(KEY_ENABLE_VIBRATE, enbaled);
        e.commit();
    }
    
    public boolean isVibrateEnabled()
    {
        return mIsVibrateEnabled;
    }
    
    private static class PreferenceHelperListener implements OnSharedPreferenceChangeListener
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sp, String key)
        {
            if (KEY_ENABLE_VIBRATE.equals(key))
            {
                mIsVibrateEnabled = sp.getBoolean(key, false);
            }
        }
    };
}
