package com.medcorp.lunar.event;

/**
 * Created by Jason on 2017/6/22.
 */

public class ViewPagerChildChange {
    private int position;

    public ViewPagerChildChange(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
