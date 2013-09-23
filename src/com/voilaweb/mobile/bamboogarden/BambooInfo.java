package com.voilaweb.mobile.bamboogarden;

import java.io.Serializable;


public class BambooInfo implements Serializable {


    private String mName;
    private boolean mActive;


    public BambooInfo() {
        this(null, false);
    }


    public BambooInfo(String name, boolean active) {
        super();
        setName(name);
        setActive(active);
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


    @Override
    public String toString() {
        return "Name: " + getName() + ", Active: " + (isActive() ? "yes" : "no");
    }
}
