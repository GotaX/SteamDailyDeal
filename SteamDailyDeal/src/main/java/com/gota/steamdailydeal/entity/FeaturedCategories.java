package com.gota.steamdailydeal.entity;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class FeaturedCategories {
    
    public int status;
    
    @SerializedName("specials")
    public CategoryInfo catSpecials;
    
    @SerializedName("coming_soon")
    public CategoryInfo comingSoon;
    
    @SerializedName("top_sellers")
    public CategoryInfo topSellers;
    
    @SerializedName("new_releases")
    public CategoryInfo newReleases;
    
    @SerializedName("genres")
    public CategoryInfo genres;
    
    @SerializedName("trailerslideshow")
    public CategoryInfo trailerslideshow; 
    
    public Map<String, CategoryInfo> catSpotlight = new HashMap<String, CategoryInfo>();

    @Override
    public String toString() {
        return "FeaturedCategories [status=" + status + ", catSpecials=" + catSpecials
                + ", comingSoon=" + comingSoon + ", topSellers=" + topSellers + ", newReleases="
                + newReleases + ", genres=" + genres + ", trailerslideshow=" + trailerslideshow
                + ", catSpotlight=" + catSpotlight + "]";
    }

    public CategoryInfo getDailyDeal() {
        for (Map.Entry<String, CategoryInfo> entry : catSpotlight.entrySet()) {
            CategoryInfo ci = entry.getValue();
            if (ci.id.equals("cat_dailydeal")) return ci;
        }
        return null;
    }
}
