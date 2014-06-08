package com.gota.steamdailydeal.constants;

/**
 * Created by Gota on 2014/5/16.
 * Email: G.tianxiang@gmail.com
 */
public interface SteamAPI {
    int TYPE_APP = 0;
    int TYPE_SUB = 1;

    String CC = "?cc=US";

    // JSON API
    String FEATURED_CATEGORIES = "https://store.steampowered.com/api/featuredcategories/" + CC;

    // HTML
    String STORE_LINK = "https://store.steampowered.com/%s/%s/" + CC;
    String WEEK_LONG_DEAL = "http://store.steampowered.com/sale/Weeklong_Deals/" + CC;

    String CAPSULE_SMALL = "http://cdn.akamai.steamstatic.com/steam/%s/%s/capsule_sm_120.jpg";
    String CAPSULE_MEDIUM = "http://cdn.akamai.steamstatic.com/steam/%s/%s/capsule_184x69.jpg";
    String CAPSULE_LARGE = "http://cdn.akamai.steamstatic.com/steam/%s/%s/capsule_616x353.jpg";

    // Enhanced Steam
    String LOWEST_PRICE = "http://api.enhancedsteam.com/pricev2/?search=%s/%s&stores=steam,amazonus,impulse,gamersgate,greenmangaming,gamefly,origin,uplay,indiegalastore,gametap,gamesplanet,getgames,desura,gog,dotemu,gameolith,adventureshop,nuuvem,shinyloot,dlgamer,humblestore,squenix,bundlestars,fireflower,humblewidgets&cc=US&coupon=true";

}
