package com.gota.steamdailydeal.util;

import com.gota.steamdailydeal.data.Tables;
import com.gota.steamdailydeal.entity.AppInfo;
import com.gota.steamdailydeal.entity.Deal;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gota on 2014/6/1.
 * Email: G.tianxiang@gmail.com
 */
public class HTMLUtils {

    public static List<Deal> paresWeekLongDeals(Document document) {
        ArrayList<Deal> deals = new ArrayList<>();

        Elements elements = document.getElementsByClass("item");
        for (Element element : elements) {
            Deal deal = new Deal();
            deal.category = Tables.TDeals.CAT_WEEK_LONG_DEAL;
            deal.appInfo = new AppInfo();

            deal.appInfo.name = element.child(0).attr("title");
            deal.appInfo.url = element.child(0).attr("href");
            deal.appInfo.id = MyTextUtils.findAppId(deal.appInfo.url);
            deal.appInfo.type = MyTextUtils.findAppType(deal.appInfo.url);
            deal.appInfo.currency = "USD";
            String oldPrice = element.getElementsByClass("was").first().text().trim();
            deal.appInfo.originalPrice = MyTextUtils.convertPrice(oldPrice);
            String price = element.getElementsByClass("was").first().nextElementSibling().nextElementSibling().text().trim();
            deal.appInfo.finalPrice = MyTextUtils.convertPrice(price);
            String discountPercent = element.getElementsByClass("percent").first().text().trim();
            deal.appInfo.discountPercent = MyTextUtils.convertPercent(discountPercent);

            deals.add(deal);
        }
        return deals;
    }

}
