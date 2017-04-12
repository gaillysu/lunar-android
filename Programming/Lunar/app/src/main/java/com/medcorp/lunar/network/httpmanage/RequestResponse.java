package com.medcorp.lunar.network.httpmanage;

import com.medcorp.lunar.network.modle.base.BaseResponse;

public interface RequestResponse<T extends BaseResponse> {
    void onFailure(Throwable e);

    void onSuccess(T o);
}
