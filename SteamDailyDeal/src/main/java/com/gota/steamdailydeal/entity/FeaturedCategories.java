package com.gota.steamdailydeal.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.TreeMap;

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
    
    public Map<String, CategoryInfo> map = new TreeMap<>();

    @Override
    public String toString() {
        return "FeaturedCategories [status=" + status + ", catSpecials=" + catSpecials
                + ", comingSoon=" + comingSoon + ", topSellers=" + topSellers + ", newReleases="
                + newReleases + ", genres=" + genres + ", trailerslideshow=" + trailerslideshow
                + ", map=" + map + "]";
    }
}
