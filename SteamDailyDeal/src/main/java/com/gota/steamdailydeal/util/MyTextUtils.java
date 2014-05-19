package com.gota.steamdailydeal.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;

/**
 * Created by Gota on 2014/5/17.
 * Email: G.tianxiang@gmail.com
 */
public class MyTextUtils {

    public static SpannableString strikethrough(String text) {
        SpannableString sp = new SpannableString(text);
        sp.setSpan(new StrikethroughSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }
}
