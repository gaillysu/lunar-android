package com.medcorp.network.validic.model;

/**
 * Created by Jason on 2016/10/12.
 */

public class ForgetPasswordModel {

    private int id;
    private String email;
    private String password_token;
    private String password;

    public ForgetPasswordModel(int id, String email, String password_token, String password) {
        this.id = id;
        this.email = email;
        this.password_token = password_token;
        this.password = password;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword_token(String password_token){
        this.password_token = password_token;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public int getId(){
        return this.id;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPassword_token(){
        return this.password_token;
    }

    public String getPassword(){
        return this.password;
    }
}
