package com.medcorp.lunar.network.model.response;

/**
 * Created by Jason on 2017/4/13.
 */

public class FacebookUserInfoResponse {

    /**
     * id : 285515735194335
     * name : Deng Jason
     * email : jason_ema@163.com
     * gender : male
     * birthday : 11/17/1989
     */

    private String id;
    private String name;
    private String email;
    private String gender;
    private String birthday;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
