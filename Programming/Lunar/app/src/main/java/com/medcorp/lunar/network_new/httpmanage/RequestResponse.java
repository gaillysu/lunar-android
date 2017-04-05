package com.medcorp.lunar.network_new.httpmanage;

public interface RequestResponse<T> {
    void onFailure(Throwable e);

    void onSuccess(T o);
}
