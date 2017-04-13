package com.medcorp.lunar.network.httpmanager;

import com.medcorp.lunar.network.model.base.BaseResponse;

public interface RequestResponse<T extends BaseResponse> {
    void onFailure(Throwable e);

    void onSuccess(T o);
}
