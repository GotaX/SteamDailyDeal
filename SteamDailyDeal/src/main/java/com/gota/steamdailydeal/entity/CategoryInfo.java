package com.gota.steamdailydeal.entity;

import java.util.List;

/**
 * Created by Gota on 2014/5/16.
 * Email: G.tianxiang@gmail.com
 */
public class CategoryInfo {
    public String id;
    public String name;
    public List<AppInfo> items;
 
    @Override
    public String toString() {
        return "CategoryInfo [id=" + id + ", name=" + name + ", items=" + items + "]";
    }
}
