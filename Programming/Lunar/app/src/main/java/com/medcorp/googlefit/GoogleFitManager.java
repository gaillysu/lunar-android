package com.medcorp.googlefit;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.Tasks;
import com.medcorp.R;

import java.util.Arrays;


/**
 * Created by karl-john on 20/11/15.
 */
public class GoogleFitManager{

    private GoogleApiClient apiClient;

    private Context context;
    private Activity activity;

    public GoogleFitManager(Context context) {
        this.context = context;
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(Scopes.FITNESS_ACTIVITY_READ_WRITE, Scopes.FITNESS_BODY_READ_WRITE));
        Tasks task = new Tasks.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory.getDefaultInstance(), credential)
                .setApplicationName(context.getString(R.string.key_nevo))
                .build();
        build(new GoogleFitApiClientCallback(), new GoogleApiClientFailedListener());
    }

    private void build(GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener){
        apiClient = new GoogleApiClient.Builder(context)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .useDefaultAccount()
                .build();
    }

    public void connect(){
        apiClient.connect();
    }

    public void disconnect(){
        if(apiClient == null){
            build(null, null);
        }
        apiClient.disconnect();
    }

    public boolean isConnected(){
        return apiClient.isConnected();
    }

    public void setActivityForResults(AppCompatActivity activity){
        this.activity = activity;
    }

    protected GoogleApiClient getApiClient() {
        return apiClient;
    }

    public Activity getActivity() {
        return activity;
    }
}


