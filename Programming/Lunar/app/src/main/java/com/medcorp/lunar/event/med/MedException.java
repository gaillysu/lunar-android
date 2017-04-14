package com.medcorp.lunar.event.med;

/**
 * Created by karl-john on 17/5/16.
 */
public class MedException {

    private Throwable exception;

    public MedException(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
