package com.medcorp.lunar.network_new.httpmanage;

import rx.Subscriber;

public class SubscriberExtends {

    private static SubscriberExtends mSubscriberExtends;

    private SubscriberExtends() {
    }

    public static SubscriberExtends getInstance() {
        if (null == mSubscriberExtends) {
            synchronized (SubscriberExtends.class) {
                if (null == mSubscriberExtends) {
                    mSubscriberExtends = new SubscriberExtends();
                }
            }
        }
        return mSubscriberExtends;
    }


    public <T> Subscriber getSubscriber(final RequestResponse<T> requestResponse) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(final Throwable e) {
                if (null != requestResponse) {
                    requestResponse.onFailure(e);
                }
            }

            @Override
            public void onNext(final T o) {

                if (null != requestResponse) {
                    requestResponse.onSuccess(o);
                }
            }
        };
    }
}
