package com.medcorp.network.validic.manager;

import android.content.Context;

import com.medcorp.R;
import com.medcorp.network.validic.service.BaseValidicRetroService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by gaillysu on 16/3/8.
 */
public class ValidicManager {
    private Context context;
    private SpiceManager spiceManager;

    public ValidicManager(Context context)
    {
        this.context = context;
        spiceManager = new SpiceManager(BaseValidicRetroService.class);
        startSpiceManager();
    }

    public String getOrganizationID(){
        return context.getString(R.string.key_validic_organization_id);
    }

    public String getOrganizationToken(){
        return context.getString(R.string.key_validic_organization_token);
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
}