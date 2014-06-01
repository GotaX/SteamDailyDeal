package com.gota.steamdailydeal.constants;

/**
 * Created by Gota on 2014/5/16.
 * Email: G.tianxiang@gmail.com
 */
public interface SteamAPI {
    String CC = "?cc=US";

    String FEATURED_CATEGORIES = "https://store.steampowered.com/api/featuredcategories/" + CC;

    String STORE_LINK = "https://store.steampowered.com/app/%s/" + CC;

    String CAPSULE_SMALL = "http://cdn.akamai.steamstatic.com/steam/apps/%s/capsule_sm_120.jpg";
    String CAPSULE_MEDIUM = "http://cdn.akamai.steamstatic.com/steam/apps/%s/capsule_184x69.jpg";
    String CAPSULE_LARGE = "http://cdn.akamai.steamstatic.com/steam/apps/%s/capsule_616x353.jpg";

    String WEEK_LONG_DEAL = "http://store.steampowered.com/sale/Weeklong_Deals/" + CC;

}
