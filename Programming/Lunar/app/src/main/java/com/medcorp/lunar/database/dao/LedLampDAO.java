package com.medcorp.lunar.database.dao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jason on 2016/12/12.
 *
 */
public class LedLampDAO extends RealmObject{
    @PrimaryKey
    private int id;
    private String name;
    private int color;


    public LedLampDAO() {
    }

    public int getId() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
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
