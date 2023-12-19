package com.example.helpme.UI.Model;

public class User {
    String key,name,email,password,carType,carColor,carModel,phoneNumber;

    public User() {
    }

    public User(String key,String name, String email, String password, String carType, String carColor, String carModel, String phoneNumber) {
        this.key = key;
        this.name = name;
        this.email = email;
        this.password = password;
        this.carType = carType;
        this.carColor = carColor;
        this.carModel = carModel;
        this.phoneNumber = phoneNumber;
    }

    public User(String key, String name, String email, String carType, String carColor, String carModel, String phoneNumber) {
        this.key = key;
        this.name = name;
        this.email = email;
        this.carType = carType;
        this.carColor = carColor;
        this.carModel = carModel;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", carType='" + carType + '\'' +
                ", carColor='" + carColor + '\'' +
                ", carModel='" + carModel + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
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

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
