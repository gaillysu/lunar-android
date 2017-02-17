package com.medcorp.event.med;

import com.medcorp.network.med.model.MedReadMoreRoutineRecordsModel;

/**
 * Created by med on 16/8/23.
 */
public class MedReadMoreRoutineRecordsModelEvent {
    final private MedReadMoreRoutineRecordsModel medReadMoreRoutineRecordsModel;

    public MedReadMoreRoutineRecordsModelEvent(MedReadMoreRoutineRecordsModel medReadMoreRoutineRecordsModel) {
        this.medReadMoreRoutineRecordsModel = medReadMoreRoutineRecordsModel;
    }

    public MedReadMoreRoutineRecordsModel getMedReadMoreRoutineRecordsModel() {
        return medReadMoreRoutineRecordsModel;
    }
}
