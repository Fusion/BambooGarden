package com.voilaweb.mobile.bamboogarden;

import java.io.Serializable;


public class BambooInfo implements Serializable {


    private String mName;
    private boolean mActive;
    private int mColor;


    public BambooInfo() {
        this(null, false, -14895114);
    }


    public BambooInfo(String name, boolean active, int color) {
        super();
        setName(name);
        setActive(active);
        setColor(color);
    }


    public void setName(String name) {
        mName = name;
    }


    public String getName() {
        return mName;
    }


    public void setActive(boolean active) {
        mActive = active;
    }


    public boolean isActive() {
        return mActive;
    }


    public void setColor(int color) {
        mColor = color;
    }


    public int getColor() {
        return mColor;
    }


    @Override
    public String toString() {
        return "Name: " + getName() + ", Active: " + (isActive() ? "yes" : "no") + ", Color: " + getColor();
    }
}
