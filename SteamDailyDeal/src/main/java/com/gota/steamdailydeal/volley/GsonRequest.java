package com.gota.steamdailydeal.volley;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.JsonSyntaxException;
import com.gota.steamdailydeal.App;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

/**
 * Created by Gota on 2014/5/16.
 * Email: G.tianxiang@gmail.com
 */
public class GsonRequest<T> extends JsonRequest<T> {

    private Class<T> mClass;
    private Type mType;

    public GsonRequest(int method, Class<T> tClass, String url, String requestBody,
                       Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
        this.mClass = tClass;
    }

    public GsonRequest(int method, Type type, String url, String requestBody,
                       Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
        this.mType = type;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.d(App.TAG, "get json: " + jsonString);
            T result = null;
            if (mClass != null) {
                result = App.gson.fromJson(jsonString, mClass);
            } else if (mType != null) {
                result = App.gson.fromJson(jsonString, mType);
            } else {
                return Response.error(new ParseError(new Exception("Not spesific type or class!")));
            }
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

}
