package com.medcorp.lunar.event.med;


import com.medcorp.lunar.network.model.response.ObtainMoreSleepResponse;

/**
 * Created by med on 16/8/23.
 */
public class MedReadMoreSleepRecordsModelEvent {
    final private ObtainMoreSleepResponse medReadMoreSleepRecordsModel;

    public MedReadMoreSleepRecordsModelEvent(ObtainMoreSleepResponse medReadMoreSleepRecordsModel) {
        this.medReadMoreSleepRecordsModel = medReadMoreSleepRecordsModel;
    }

    public ObtainMoreSleepResponse getMedReadMoreSleepRecordsModel() {
        return medReadMoreSleepRecordsModel;
    }
}
