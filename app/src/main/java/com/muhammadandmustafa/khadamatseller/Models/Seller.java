package com.muhammadandmustafa.khadamatseller.Models;

public class Seller {

    private String username, email, phoneNumber, storeName, city, area, specialty, profilePhotoURL;

    public Seller() {
    }

    public Seller(String username,
                  String email,
                  String phoneNumber,
                  String storeName,
                  String city,
                  String area,
                  String specialty,
                  String profilePhotoURL) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.storeName = storeName;
        this.city = city;
        this.area = area;
        this.specialty = specialty;
        this.profilePhotoURL = profilePhotoURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getProfilePhotoURL() {
        return profilePhotoURL;
    }

    public void setProfilePhotoURL(String profilePhotoURL) {
        this.profilePhotoURL = profilePhotoURL;
    }
}
