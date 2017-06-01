package com.medcorp.lunar.network.model.response.weather;

/**
 * Created by med on 17/4/24.
 */

public class City {
    int id;
    String name;
    private Coordinate coord;
    private String country;
    public Coordinate getCoord() {
        return coord;
    }
    public String getCountry() {
        return country;
    }
    public String getName() {
        return name;
    }
}
