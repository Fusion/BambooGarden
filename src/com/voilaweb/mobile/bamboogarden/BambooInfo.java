package com.voilaweb.mobile.bamboogarden;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Chris
 * Date: 9/17/13
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class BambooInfo implements Serializable {


    private String mName;


    public BambooInfo() {
        this(null);
    }


    public BambooInfo(String name) {
        super();
        setName(name);
    }


    public void setName(String name) {
        mName = name;
    }


    public String getName() {
        return mName;
    }
}
