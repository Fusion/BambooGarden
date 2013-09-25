package com.voilaweb.mobile.bamboogarden;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class GardenAdapter extends ArrayAdapter<BambooInfo> {


    private Context mContext;
    private GardenAdapterList mValues;



    public GardenAdapter(Context context) {
        super(context, R.layout.bamboo_item);
        mContext = context;
        mValues = new GardenAdapterList();
    }


    public GardenAdapter(Context context, GardenAdapterList values) {
        super(context, R.layout.bamboo_item);
        mContext = context;
        mValues = values;
    }


    public void setAllValues(GardenAdapterList  values) {
        mValues = values;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mValues.size();
    }


    @Override
    public BambooInfo getItem(int position) {
        return mValues.get(position);
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public int getItemViewType(int position) {
        int type = 0;
        BambooInfo info = getItem(position);
        if(info.isActive())
            type = 1;
        return type;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BambooViewHolder holder;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.bamboo_item, parent, false);
            holder = new BambooViewHolder();
            holder.nameView = (TextView) convertView.findViewById(R.id.bamboo_item_text);
            holder.colorView = (ImageView) convertView.findViewById(R.id.bamboo_item_icon);
            convertView.setTag(holder);
        }
        else {
            holder = (BambooViewHolder)convertView.getTag();
        }

        BambooInfo info = getItem(position);
        holder.nameView.setText(info.getName());
        holder.position = position;
        holder.colorView.setImageResource(R.drawable.btn_radio_off_holo);
        holder.colorView.setColorFilter(info.getColor(), PorterDuff.Mode.SRC_ATOP);

        if(getItemViewType(position) == 0) {
            holder.nameView.setTypeface(holder.nameView.getTypeface(), Typeface.NORMAL);
        }
        else {
            holder.nameView.setTypeface(holder.nameView.getTypeface(), Typeface.BOLD_ITALIC);
        }

        return convertView;
    }
}
