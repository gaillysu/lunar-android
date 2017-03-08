package com.medcorp.lunar.event.google.api;

import android.os.Bundle;

/**
 * Created by karl-john on 17/3/16.
 */
public class GoogleApiClientConnectedEvent {
    private Bundle bundle;

    public GoogleApiClientConnectedEvent(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
