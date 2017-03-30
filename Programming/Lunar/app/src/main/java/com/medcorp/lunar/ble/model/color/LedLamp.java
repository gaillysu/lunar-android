package com.medcorp.lunar.ble.model.color;


import com.medcorp.lunar.ble.model.color.visitor.NevoLedVisitor;

/**
 * Created by Jason on 2016/12/8.
 * s
 */
public class LedLamp extends NevoLed {


    private int id = (int) (Math.floor(Math.random() * Integer.MAX_VALUE));
    private String name;
    private int color;
    private boolean isSelect;

    public LedLamp() {

    }


    public LedLamp(String name, int color) {
        this.name = name;
        this.color = color;
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

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public int getHexColor() {
        return getColor();
    }

    @Override
    public int getStringResource() {
        return 0;
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    @Override
    public String getTag() {
        return getName();
    }

    @Override
    public <T> T accept(NevoLedVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
