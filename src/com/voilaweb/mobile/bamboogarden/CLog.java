package com.voilaweb.mobile.bamboogarden;

import android.os.SystemClock;
import com.stericson.RootTools.containers.RootClass;

@RootClass.Candidate
public class CLog {
    static public String TAG = "Bamboo Garden";
    static public final int MASK_TRACE = 1 << 0;
    
    static final protected long mInitTime = SystemClock.uptimeMillis();
    static int mMask = 0;
    static String mTag = null;
    
    
    static public void setTag(String tag) {
    	mTag = tag;
    }
    
    
    static public String getTag() {
    	return mTag != null ? mTag : TAG;
    }
    
    
    static public void setOn(int maskBit) {
    	mMask |= maskBit;
    }
    
    
    static public boolean isOn(int maskBit) {
    	return ((mMask & maskBit) != 0);
    }
    
    
    static protected String getMeta() {
    	return "T(th:" + android.os.Process.myTid() + " el:" + (SystemClock.uptimeMillis() - mInitTime) + ") ";
    }
    
    
    static public void log(String txt) {
        android.util.Log.d(getTag(), getTag() + ": " + getMeta() + txt);
    }

    
    static public void log(String txt, Exception ex) {
        android.util.Log.d(getTag(), getTag() + ": " + getMeta() + txt, ex);
    }

    
    static public void error(String txt) {
        android.util.Log.e(getTag(), getTag() + ": " + getMeta() + txt);
    }

    
    static public void error(String txt, Exception ex) {
        android.util.Log.e(getTag(), getTag() + ": " + getMeta() + txt, ex);
    }
}
