package com.medcorp.lunar.event;

import java.util.Date;

/***
 * Created by med on 16/8/10.
 */
public class DateSelectChangedEvent {
    private final Date date;

    public DateSelectChangedEvent(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}
