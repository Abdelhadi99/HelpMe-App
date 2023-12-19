package com.example.helpme.UI.Model;

import java.util.HashMap;

public class Helper {
    String key,name,email,password,experienceType,experienceLevel,phoneNumber;
    HashMap<String, Double> locationMap;

    public Helper() {
    }

    public Helper(String key, String name, String email, String password, String experienceType, String experienceLevel, String phoneNumber, HashMap<String, Double> locationMap) {
        this.key = key;
        this.name = name;
        this.email = email;
        this.password = password;
        this.experienceType = experienceType;
        this.experienceLevel = experienceLevel;
        this.phoneNumber = phoneNumber;
        this.locationMap = locationMap;
    }


    public Helper(String key, String name, String email, String experienceType, String experienceLevel, String phoneNumber, HashMap<String, Double> locationMap) {
        this.key = key;
        this.name = name;
        this.email = email;
        this.experienceType = experienceType;
        this.experienceLevel = experienceLevel;
        this.phoneNumber = phoneNumber;
        this.locationMap = locationMap;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExperienceType() {
        return experienceType;
    }

    public void setExperienceType(String experienceType) {
        this.experienceType = experienceType;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public HashMap<String, Double> getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(HashMap<String, Double> locationMap) {
        this.locationMap = locationMap;
    }
}
