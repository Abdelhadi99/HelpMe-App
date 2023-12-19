package com.example.helpme.UI.Model;

import java.util.HashMap;

public class Case {
    private String caseKey;
    private String userKey,userName,carType,carColor;
    private HashMap<String,Double> userLocation;

    private String helperKey,helperName,helperExpType,helperPhoneNumber;
    private boolean isAccept,isCompleted;

    public Case() {
    }

    public Case(String caseKey, String userKey, String userName, String carType, String carColor, HashMap<String, Double> userLocation, boolean isAccept, boolean isCompleted) {
        this.caseKey = caseKey;
        this.userKey = userKey;
        this.userName = userName;
        this.carType = carType;
        this.carColor = carColor;
        this.userLocation = userLocation;
        this.isAccept = isAccept;
        this.isCompleted = isCompleted;
    }


    public Case(String caseKey,String userKey,String helperKey, String helperName, String helperExpType, String helperPhoneNumber, boolean isAccept) {
        this.caseKey = caseKey;
        this.userKey = userKey;
        this.helperKey = helperKey;
        this.helperName = helperName;
        this.helperExpType = helperExpType;
        this.helperPhoneNumber = helperPhoneNumber;
        this.isAccept = isAccept;
    }


    public Case(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }




    public String getCaseKey() {
        return caseKey;
    }

    public void setCaseKey(String caseKey) {
        this.caseKey = caseKey;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public HashMap<String, Double> getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(HashMap<String, Double> userLocation) {
        this.userLocation = userLocation;
    }

    public String getHelperKey() {
        return helperKey;
    }

    public void setHelperKey(String helperKey) {
        this.helperKey = helperKey;
    }

    public String getHelperName() {
        return helperName;
    }

    public void setHelperName(String helperName) {
        this.helperName = helperName;
    }

    public String getHelperExpType() {
        return helperExpType;
    }

    public void setHelperExpType(String helperExpType) {
        this.helperExpType = helperExpType;
    }

    public String getHelperPhoneNumber() {
        return helperPhoneNumber;
    }

    public void setHelperPhoneNumber(String helperPhoneNumber) {
        this.helperPhoneNumber = helperPhoneNumber;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean accept) {
        isAccept = accept;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

}
