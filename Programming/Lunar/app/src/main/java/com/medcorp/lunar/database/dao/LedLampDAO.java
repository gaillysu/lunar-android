package com.medcorp.lunar.database.dao;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Jason on 2016/12/12.
 *
 */

public class LedLampDAO {

    public static final String IDString = "ID";
    @DatabaseField(generatedId = true)
    private int ID;

    public static final String nameString = "name";
    @DatabaseField
    private String Name;

    public static final String colorString = "color";
    @DatabaseField
    private int Color;


    public LedLampDAO() {
    }

    public LedLampDAO(String name, int color) {
        this.Name = name;
        this.Color = color;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
