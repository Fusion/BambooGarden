package com.voilaweb.mobile.bamboogarden;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class QuickHandle extends LinearLayout implements View.OnTouchListener {


    static enum DIRECTION { DOWN, UP };

    private View mFirstPane;
    private View mSecondPane;
    private int mStartingLoc;
    private int mRefPaneHeight;


    public QuickHandle(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        setVisuals(DIRECTION.DOWN);
    }


    public void setPanes(View firstPane, View secondPane) {
        mFirstPane  = firstPane;
        mSecondPane = secondPane;
    }


    public void showFirstPane() {
        moveToBottom();
    }


    private void setVisuals(DIRECTION direction) {
        if(direction == DIRECTION.DOWN)
            setBackgroundResource(R.drawable.arrow_ss_down);
        else
            setBackgroundResource(R.drawable.arrow_ss_up);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean consume = false;

        switch(event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                mStartingLoc =  (int)event.getRawY();
                mRefPaneHeight = mFirstPane.getLayoutParams().height;
                if(mRefPaneHeight < 0)
                    mRefPaneHeight = ((IFirstViewInfo)mFirstPane).getMaxMeasuredHeight();
                consume = true;
                break;
            case MotionEvent.ACTION_MOVE:
                int newRawY = (int)event.getRawY();
                moveChildren(newRawY);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(event.getRawY() < mStartingLoc)
                    moveToTop();
                else
                    moveToBottom();
                break;
        }

        return consume;
    }


    private void moveChildren(int y) {
        int offset = y - mStartingLoc;
        ViewGroup.LayoutParams lp = mFirstPane.getLayoutParams();
        int newHeight = mRefPaneHeight + offset;

        int max = ((IFirstViewInfo)mFirstPane).getMaxMeasuredHeight();
        if(newHeight> max)
            newHeight = max;
        else if(newHeight < 1)
            newHeight = 1;
        lp.height = newHeight;
        mFirstPane.requestLayout();
    }


    private void moveToBottom() {
        setVisuals(DIRECTION.UP);
        int max = ((IFirstViewInfo)mFirstPane).getMaxMeasuredHeight();
        ViewGroup.LayoutParams lp = mFirstPane.getLayoutParams();
        if(max == 0)
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        else
            lp.height = max;
        mFirstPane.requestLayout();
    }


    private void moveToTop() {
        setVisuals(DIRECTION.DOWN);
        ViewGroup.LayoutParams lp = mFirstPane.getLayoutParams();
        lp.height = 1;
        mFirstPane.requestLayout();
    }
}
