package com.medcorp.network.med.manager;

import android.content.Context;

import com.medcorp.R;
import com.medcorp.network.med.service.BaseMedRetroService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by gaillysu on 16/3/8.
 *
 */
public class MedManager {
    private Context context;
    private SpiceManager spiceManager;

    public MedManager(Context context)
    {
        this.context = context;
        spiceManager = new SpiceManager(BaseMedRetroService.class);
        startSpiceManager();
    }

    public void startSpiceManager()
    {
        if(!spiceManager.isStarted()) {
            spiceManager.start(context);
        }
    }
    public void stopSpiceManager()
    {
        if(spiceManager!=null) {
            spiceManager.shouldStop();
        }
    }

    public void execute(SpiceRequest request, RequestListener listener){
        spiceManager.execute(request, listener);
    }

    public String getAccessToken(){
        return context.getString(R.string.token);
    }

    public String getOrganizationID(){
        return "med-corp";
    }

}
