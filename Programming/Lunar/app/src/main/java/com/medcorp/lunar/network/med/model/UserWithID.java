package com.medcorp.lunar.network.med.model;

/**
 * Created by med on 16/4/29.
 */
public class UserWithID {
    private int id;
    private String first_name;
    private String last_name;
    private String email;
    private float length;
    private float weight;
    private int   sex;
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

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public DateWithTimeZone getBirthday() {
        return birthday;
    }

    public void setBirthday(DateWithTimeZone birthday) {
        this.birthday = birthday;
    }
}
