package com.medcorp.event.google.api;

import com.google.android.gms.common.ConnectionResult;

/**
 * Created by karl-john on 17/3/16.
 */
public class GoogleApiClientConnectionFailedEvent {
    private ConnectionResult connectionResult;

    public GoogleApiClientConnectionFailedEvent(ConnectionResult connectionResult) {

        this.connectionResult = connectionResult;
    }

    public ConnectionResult getConnectionResult() {
        return connectionResult;
    }

    public void setConnectionResult(ConnectionResult connectionResult) {
        this.connectionResult = connectionResult;
    }
}
