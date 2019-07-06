package com.example.android.myapp.helper;

public class LocationUploadHelper {

    private String state ;
    private String district;
    private String tehsil ;
    private String village ;

    public LocationUploadHelper(){}

    public LocationUploadHelper(String state, String district, String tehsil, String village) {
        this.state = state;
        this.district = district;
        this.tehsil = tehsil;
        this.village = village;
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
}
