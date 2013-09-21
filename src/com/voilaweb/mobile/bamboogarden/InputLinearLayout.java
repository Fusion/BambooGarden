package com.voilaweb.mobile.bamboogarden;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


public class InputLinearLayout extends LinearLayout implements IFirstViewInfo  {
    private int mMaxMeasuredHeight = 0;

    public InputLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int h = getMeasuredHeight();
        if(h > mMaxMeasuredHeight)
            mMaxMeasuredHeight = h;
    }


    @Override
    public int getMaxMeasuredHeight() {
        return mMaxMeasuredHeight;
    }
}
