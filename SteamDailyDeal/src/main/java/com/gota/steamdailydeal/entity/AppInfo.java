package com.gota.steamdailydeal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gota on 2014/5/16.
 * Email: G.tianxiang@gmail.com
 */
    public class AppInfo {
    public String id;
    public int type;
    public String name;
    public boolean discounted;
    @SerializedName("discount_percent")
    public int discountPercent;
    @SerializedName("original_price")
    public int originalPrice;
    @SerializedName("final_price")
    public int finalPrice;
    public String currency;
    @SerializedName("large_capsule_image")
    public String largeCapsuleImage;
    @SerializedName("small_capsule_image")
    public String smallCapsuleImage;
    @SerializedName("discount_expiration")
    public long discountExpiration;
    public String headline;
    /**
     * Whether the instance has controller support. Known values:
     * <ul>
     * <li>partial</li>
     * <li>full</li>
     * </ul>
     */
    @SerializedName("controller_support")
    public String controllerSupport;
    @SerializedName("purchase_package")
    public String purchasePackage;


    /**
     * Only for soptlight
     */
    @SerializedName("header_image")
    public String headerImage;
    public String body;
    public String url;

    @Override
    public String toString() {
        return "AppInfo{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", discounted=" + discounted +
                ", discountPercent=" + discountPercent +
                ", originalPrice=" + originalPrice +
                ", finalPrice=" + finalPrice +
                ", currency='" + currency + '\'' +
                ", largeCapsuleImage='" + largeCapsuleImage + '\'' +
                ", smallCapsuleImage='" + smallCapsuleImage + '\'' +
                ", discountExpiration=" + discountExpiration +
                ", headline='" + headline + '\'' +
                ", controllerSupport='" + controllerSupport + '\'' +
                ", purchasePackage='" + purchasePackage + '\'' +
                ", headerImage='" + headerImage + '\'' +
                ", body='" + body + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
