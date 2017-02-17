package com.medcorp.googlefit;

import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.medcorp.event.google.api.GoogleApiClientConnectedEvent;
import com.medcorp.event.google.api.GoogleApiClientConnectionSuspendedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by karl-john on 17/3/16.
 */
public class GoogleFitApiClientCallback implements GoogleApiClient.ConnectionCallbacks {
    @Override
    public void onConnected(Bundle bundle) {
        EventBus.getDefault().post(new GoogleApiClientConnectedEvent(bundle));
    }

    @Override
    public void onConnectionSuspended(int i) {
        EventBus.getDefault().post(new GoogleApiClientConnectionSuspendedEvent(i));
    }
}
