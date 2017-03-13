package com.medcorp.lunar.database.dao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jason on 2016/12/12.
 *
 */

public class LedLampDAO extends RealmObject{
    @PrimaryKey
    private int ID;
    private String name;
    private int color;


    public LedLampDAO() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
