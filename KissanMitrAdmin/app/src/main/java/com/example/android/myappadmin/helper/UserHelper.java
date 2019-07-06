package com.example.android.myappadmin.helper;

public class UserHelper {

    private String name;
    private String fName;
    private String gender;
    private String phone;

    private String state;
    private String district;
    private String tehsil;
    private String village;
    private String place ;

    private String imageUrl;
    private String aadharNo;
    private String birthYear;
    private String registrationType;

    public UserHelper(){}

    public UserHelper(String name, String fName, String gender, String phone, String state, String district, String tehsil, String village, String place, String imageUrl, String aadharNo, String birthYear, String registrationType) {
        this.name = name;
        this.fName = fName;
        this.gender = gender;
        this.phone = phone;
        this.state = state;
        this.district = district;
        this.tehsil = tehsil;
        this.village = village;
        this.place = place;
        this.imageUrl = imageUrl;
        this.aadharNo = aadharNo;
        this.birthYear = birthYear;
        this.registrationType = registrationType;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTehsil() {
        return tehsil;
    }

    public void setTehsil(String tehsil) {
        this.tehsil = tehsil;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }

}
