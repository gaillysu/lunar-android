package com.medcorp.event.med;

import com.medcorp.network.med.model.MedReadMoreRoutineRecordsModel;
import com.medcorp.network.med.model.MedReadMoreSleepRecordsModel;

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
