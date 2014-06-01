package com.gota.steamdailydeal.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;

import com.gota.steamdailydeal.App;
import com.gota.steamdailydeal.constants.Steam;
import com.gota.steamdailydeal.data.Tables;

import java.util.Currency;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gota on 2014/5/17.
 * Email: G.tianxiang@gmail.com
 */
public class MyTextUtils {

    private static final Pattern PATTERN_ID =
            Pattern.compile("(?<=((app/)||(sub/)))(\\d+)");

    public static int findAppType(String storeLink) {
        if (storeLink.contains("/app/")) {
            return Steam.TYPE_APP;
        } else if (storeLink.contains("/sub/")) {
            return Steam.TYPE_SUB;
        } else {
            return -1;
        }
    }

    public static String findAppId(String storeLink) {
        Matcher matcher = PATTERN_ID.matcher(storeLink);
        return matcher.find() ? matcher.group() : "";
    }

    public static int convertPrice(String price) {
        return Integer.parseInt(price.replaceAll("[^\\d]", ""));
    }

    public static int convertPercent(String percent) {
        return Integer.parseInt(percent.replaceAll("[^\\d]", ""));
    }

    public static String getStoreLink(int id) {
        return String.format("https://store.steampowered.com/app/%s/", id);
    }

    public static SpannableString strikethrough(CharSequence text) {
        SpannableString sp = new SpannableString(text);
        sp.setSpan(new StrikethroughSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    public static String getCurrency(int price, String currencyCode) {
        float realPrice = price / 100f;
        currencyCode = currencyCode.toUpperCase();
        Currency currency = Currency.getInstance(currencyCode);
        Locale locale = App.instance.getResources().getConfiguration().locale;
        String symbol = currency.getSymbol(locale);
        return String.format("%s%s", symbol, realPrice);
    }

    public static String getDiscount(String text) {
        return String.format("-%s%s", text, "%");
    }

    public static String getCategory(int category) {
        switch (category) {
            case Tables.TDeals.CAT_DAILY_DEAL:
                return "每日特惠";
            case Tables.TDeals.CAT_SPOTLIGHT:
                return "今日焦点";
            case Tables.TDeals.CAT_WEEK_LONG_DEAL:
                return "周期特惠";
            default:
                return "未知类型";
        }
    }
}
