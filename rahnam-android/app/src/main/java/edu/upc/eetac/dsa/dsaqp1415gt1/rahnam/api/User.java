package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cristina on 29/05/2015.
 */
public class User {

    private String username;
    private String userpass;
    private String name;
    private int avatar;
    private String email;
    private Date birth;
    private String gender;
    private boolean loginSuccessful;
    private boolean RegisterSuccessful;
    private Map<String, Link> links = new HashMap<String, Link>();


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpass() {
        return userpass;
    }

    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public void setLoginSuccessful(boolean loginSuccessful) {
        this.loginSuccessful = loginSuccessful;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Link> links) {
        this.links = links;
    }

    public boolean isRegisterSuccessful() {
        return RegisterSuccessful;
    }

    public void setRegisterSuccessful(boolean registerSuccessful) {
        RegisterSuccessful = registerSuccessful;
    }
}
