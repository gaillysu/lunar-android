package com.medcorp.lunar.network_new.listener;

import com.medcorp.lunar.network_new.modle.base.BaseResponse;

/**
 * Created by Jason on 2017/4/7.
 */

public interface RequestResponseListener<T extends BaseResponse> {
    void onFailed();

    void onSuccess(T response);
}
