package com.gota.steamdailydeal.gson;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.gota.steamdailydeal.entity.CategoryInfo;
import com.gota.steamdailydeal.entity.FeaturedCategories;

import java.lang.reflect.Type;

public class FeaturedCategoriesDeserializer 
    implements JsonDeserializer<FeaturedCategories> {

    private Gson mGson = new Gson();
    
    public FeaturedCategories deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        
        FeaturedCategories fc = mGson.fromJson(json, FeaturedCategories.class);

        JsonObject obj = json.getAsJsonObject();
        int i = 0;
        while (true) {
            String strNum = String.valueOf(i++);
            JsonElement element = obj.getAsJsonObject(strNum);
            if (element == null) break;

            CategoryInfo ci = mGson.fromJson(element, CategoryInfo.class);
            fc.map.put(strNum, ci);
        }
        
        return fc;
    }

}
