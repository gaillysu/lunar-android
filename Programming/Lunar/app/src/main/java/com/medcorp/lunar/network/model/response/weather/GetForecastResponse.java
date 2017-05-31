package com.medcorp.lunar.network.model.response.weather;

/**
 * Created by med on 17/4/21.
 */

public class GetForecastResponse {
    //pls refer to  http://openweathermap.org/forecast5
    private City city;
    private Coordinate coord;
    private String country;
    private String cod;
    private float message;
    private int cnt;
    private Forecast[] list;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public float getMessage() {
        return message;
    }

    public void setMessage(float message) {
        this.message = message;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public Forecast[] getList() {
        return list;
    }

    public void setList(Forecast[] list) {
        this.list = list;
    }
}
