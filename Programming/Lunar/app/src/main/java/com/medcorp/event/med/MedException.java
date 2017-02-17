package com.medcorp.event.med;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by karl-john on 17/5/16.
 */
public class MedException {

    private SpiceException exception;

    public MedException(SpiceException exception) {
        this.exception = exception;
    }

    public SpiceException getException() {
        return exception;
    }

    public void setException(SpiceException exception) {
        this.exception = exception;
    }
}
