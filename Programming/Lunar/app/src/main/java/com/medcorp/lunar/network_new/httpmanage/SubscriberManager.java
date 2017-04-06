package com.medcorp.lunar.network_new.httpmanage;


import android.support.v4.util.ArrayMap;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


public class SubscriberManager {
    private final ArrayMap<Object, CompositeSubscription> SubscriptionMap = new ArrayMap<>();
    private static SubscriberManager instance = new SubscriberManager();

    private SubscriberManager() {
    }

    public static SubscriberManager getInstance() {
        return instance;
    }

    public void addSubscription(Object object, Subscription s) {
        CompositeSubscription subscriptionList = SubscriptionMap.get(object);
        if (subscriptionList == null) {
            subscriptionList = new CompositeSubscription();
            SubscriptionMap.put(object, subscriptionList);
        }
        subscriptionList.add(s);
    }

    public void removeSubscription(Object object) {
        CompositeSubscription subscriptionList = SubscriptionMap.get(object);
        if (subscriptionList != null) {
            subscriptionList.clear();
        }
        SubscriptionMap.remove(object);
    }

    public void AppExit() {
        try {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
