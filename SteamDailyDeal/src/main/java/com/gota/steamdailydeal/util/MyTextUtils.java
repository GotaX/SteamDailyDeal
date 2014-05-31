package com.gota.steamdailydeal.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;

import com.gota.steamdailydeal.App;
import com.gota.steamdailydeal.data.Tables;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by Gota on 2014/5/17.
 * Email: G.tianxiang@gmail.com
 */
public class MyTextUtils {

    public static String getStoreLink(String id) {
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
