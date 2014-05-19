package com.gota.steamdailydeal.constants;

/**
 * Created by Gota on 2014/5/18.
 * Email: G.tianxiang@gmail.com
 */
public class Steam {

    public static String getStoreLink(int id) {
       return String.format("https://store.steampowered.com/app/%s/", id);
    }

}
