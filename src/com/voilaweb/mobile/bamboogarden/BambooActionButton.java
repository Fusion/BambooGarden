package com.voilaweb.mobile.bamboogarden;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created with IntelliJ IDEA.
 * User: Chris
 * Date: 9/19/13
 * Time: 6:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class BambooActionButton extends Button implements BambooMessages {


    public BambooActionButton(final Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent clickedIntent = new Intent(BROADCAST_BUTTONCLICK);
                clickedIntent.putExtra("id", v.getId());
                // TODO this is quite brittle
                clickedIntent.putExtra("position",
                        ((BambooViewHolder)((ViewGroup)v.getParent().getParent()).getTag()).position);
                context.sendBroadcast(clickedIntent);
            }
        });

    }
}
