package com.medcorp.lunar.network.listener;

import com.medcorp.lunar.network.modle.base.BaseResponse;

/**
 * Created by Jason on 2017/4/7.
 */

public interface RequestResponseListener<T extends BaseResponse> {
    void onFailed();

    void onSuccess(T response);
}
