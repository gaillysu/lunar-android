package com.medcorp.lunar.network.med.model;

/**
 * Created by Jason on 2016/8/9.
 */
public class UserWithLocation {

    private int id;
    private String first_name;
    private String last_name;
    private String email;
    private int length;
    private String last_longitude;
    private String last_latitude;
    private int weight;
    private DateWithTimeZone birthday;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getLast_longitude() {
        return last_longitude;
    }

    public void setLast_longitude(String last_longitude) {
        this.last_longitude = last_longitude;
    }

    public String getLast_latitude() {
        return last_latitude;
    }

    public void setLast_latitude(String last_latitude) {
        this.last_latitude = last_latitude;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public DateWithTimeZone getBirthday() {
        return birthday;
    }

    public void setBirthday(DateWithTimeZone birthday) {
        this.birthday = birthday;
    }
}
