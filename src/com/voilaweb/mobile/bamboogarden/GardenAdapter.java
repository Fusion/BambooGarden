package com.voilaweb.mobile.bamboogarden;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final BambooViewHolder holder;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.bamboo_item, parent, false);
            holder = new BambooViewHolder();
            holder.nameView = (TextView) convertView.findViewById(R.id.bamboo_item_text);
            convertView.setTag(holder);
        }
        else {
            holder = (BambooViewHolder)convertView.getTag();
        }

        BambooInfo info = getItem(position);
        holder.nameView.setText(info.getName());
        holder.position = position;

        return convertView;
    }
}
