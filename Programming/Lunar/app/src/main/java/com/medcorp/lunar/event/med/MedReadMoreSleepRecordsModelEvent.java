package com.medcorp.lunar.event.med;


import com.medcorp.lunar.network.med.model.MedReadMoreSleepRecordsModel;

/**
 * Created by med on 16/8/23.
 */
public class MedReadMoreSleepRecordsModelEvent {
    final private MedReadMoreSleepRecordsModel medReadMoreSleepRecordsModel;

    public MedReadMoreSleepRecordsModelEvent(MedReadMoreSleepRecordsModel medReadMoreSleepRecordsModel) {
        this.medReadMoreSleepRecordsModel = medReadMoreSleepRecordsModel;
    }

    public MedReadMoreSleepRecordsModel getMedReadMoreSleepRecordsModel() {
        return medReadMoreSleepRecordsModel;
    }
}
