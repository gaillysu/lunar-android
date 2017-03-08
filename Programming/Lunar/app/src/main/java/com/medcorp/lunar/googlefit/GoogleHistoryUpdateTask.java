package com.medcorp.lunar.googlefit;

import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.medcorp.lunar.event.google.fit.GoogleFitUpdateEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

/**
 * Created by karl-john on 18/2/16.
 */
public class GoogleHistoryUpdateTask extends AsyncTask<DataSet,Void,Boolean>{

    private GoogleApiClient googleApiClient;

    public GoogleHistoryUpdateTask(GoogleFitManager googleFitManager) {
        this.googleApiClient = googleFitManager.getApiClient();
    }

    @Override
    protected Boolean doInBackground(DataSet... dataSets) {
        DataSet dataSet = dataSets[0];
        if (googleApiClient == null || dataSet == null) {
            return false;

        }

        com.google.android.gms.common.api.Status insertStatus = Fitness.HistoryApi.insertData(googleApiClient, dataSet).await(1, TimeUnit.MINUTES);
        if (!insertStatus.isSuccess()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            EventBus.getDefault().post(new GoogleFitUpdateEvent(true));
        }else{
            EventBus.getDefault().post(new GoogleFitUpdateEvent(false));
        }
    }
}
