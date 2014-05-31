package com.gota.steamdailydeal.entity;

/**
 * Created by Gota on 2014/5/31.
 * Email: G.tianxiang@gmail.com
 */
public class Deal {

    public int type;
    public AppInfo appInfo;

    @Override
    public String toString() {
        return "Deal{" +
                "type=" + type +
                ", appInfo=" + appInfo +
                '}';
    }
}
