package com.gota.steamdailydeal.exception;

/**
 * Created by Gota on 2014/5/18.
 * Email: G.tianxiang@gmail.com
 */
public class NoNetworkException extends Exception {
    public NoNetworkException() {
        super();
    }

    public NoNetworkException(String detailMessage) {
        super(detailMessage);
    }

    public NoNetworkException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoNetworkException(Throwable throwable) {
        super(throwable);
    }
}
