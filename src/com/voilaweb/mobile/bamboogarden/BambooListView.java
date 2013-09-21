package com.voilaweb.mobile.bamboogarden;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import swipelistview.BaseSwipeListViewListener;
import swipelistview.SwipeListView;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Chris
 * Date: 9/17/13
 * Time: 4:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class BambooListView extends SwipeListView implements BambooMessages {


    private Context mContext;

    public BambooListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setSwipeListViewListener(new BaseSwipeListViewListener() {


            @Override
            public void onOpened(int position, boolean toRight) {
            }


            @Override
            public void onClosed(int position, boolean fromRight) {
            }



            @Override
            public void onListChanged() {
            }


            @Override
            public void onMove(int position, float x) {
            }


            @Override
            public void onStartOpen(int position, int action, boolean right) {
            }


            @Override
            public void onStartClose(int position, boolean right) {
            }


            @Override
            public void onClickFrontView(int position) {
                Intent clickedIntent = new Intent(BROADCAST_ITEMCLICK);
                clickedIntent.putExtra("position", position);
                mContext.sendBroadcast(clickedIntent);
            }


            @Override
            public void onClickBackView(int position) {
            }


            @Override
            public void onDismiss(int[] reverseSortedPositions) {
            }
        });
    }




}
