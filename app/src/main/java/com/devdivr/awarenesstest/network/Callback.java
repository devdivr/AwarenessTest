package com.devdivr.awarenesstest.network;

public interface Callback<T> {

    void onSuccess(T t);

    void onFailure();

    void onComplete();
}