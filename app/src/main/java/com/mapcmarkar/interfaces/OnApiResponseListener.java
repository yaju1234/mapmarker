package com.mapcmarkar.interfaces;

/**
 * Created by kamal on 11/14/2017.
 */

public interface OnApiResponseListener {
    public <E> void onSuccess(E t);
    public <E> void onError(E t);
    public  void onError();
}
