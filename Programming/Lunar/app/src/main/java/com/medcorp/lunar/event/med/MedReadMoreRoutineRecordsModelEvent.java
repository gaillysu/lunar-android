package com.medcorp.lunar.event.med;


import com.medcorp.lunar.network_new.modle.response.ObtainMoreStepsResponse;

/**
 * Created by med on 16/8/23.
 */
public class MedReadMoreRoutineRecordsModelEvent {
    final private ObtainMoreStepsResponse medReadMoreRoutineRecordsModel;

    public MedReadMoreRoutineRecordsModelEvent(ObtainMoreStepsResponse medReadMoreRoutineRecordsModel) {
        this.medReadMoreRoutineRecordsModel = medReadMoreRoutineRecordsModel;
    }

    public ObtainMoreStepsResponse getMedReadMoreRoutineRecordsModel() {
        return medReadMoreRoutineRecordsModel;
    }
}
