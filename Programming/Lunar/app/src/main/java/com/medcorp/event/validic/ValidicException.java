package com.medcorp.event.validic;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by karl-john on 17/5/16.
 */
public class ValidicException {

    private SpiceException exception;

    public ValidicException(SpiceException exception) {
        this.exception = exception;
    }

    public SpiceException getException() {
        return exception;
    }

    public void setException(SpiceException exception) {
        this.exception = exception;
    }
}
