package com.medcorp.network.med.model;

/**
 * Created by Administrator on 2016/6/10.
 */
public class UserWithPasswordToken {
    private String password_token;
    private String email;
    private int id;

    public String getPassword_token() {
        return password_token;
    }

    public void setPassword_token(String password_token) {
        this.password_token = password_token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
