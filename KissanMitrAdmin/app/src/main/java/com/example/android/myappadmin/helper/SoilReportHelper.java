package com.example.android.myappadmin.helper;

public class SoilReportHelper {

    private String kissanId;
    private String landAvailable;
    private String typeOfLand;
    private String cropsGrown;
    private String estimatedIncome;
    private String ph;
    private String nitrogen;
    private String phosphorous;
    private String potassium;
    private String average;

    public SoilReportHelper(){}

    public SoilReportHelper(String kissanId, String landAvailable, String typeOfLand, String cropsGrown, String estimatedIncome, String ph, String nitrogen, String phosphorous, String potassium, String average) {
        this.kissanId = kissanId;
        this.landAvailable = landAvailable;
        this.typeOfLand = typeOfLand;
        this.cropsGrown = cropsGrown;
        this.estimatedIncome = estimatedIncome;
        this.ph = ph;
        this.nitrogen = nitrogen;
        this.phosphorous = phosphorous;
        this.potassium = potassium;
        this.average = average;
    }

    public String getKissanId() {
        return kissanId;
    }

    public void setKissanId(String kissanId) {
        this.kissanId = kissanId;
    }

    public String getLandAvailable() {
        return landAvailable;
    }

    public void setLandAvailable(String landAvailable) {
        this.landAvailable = landAvailable;
    }

    public String getTypeOfLand() {
        return typeOfLand;
    }

    public void setTypeOfLand(String typeOfLand) {
        this.typeOfLand = typeOfLand;
    }

    public String getCropsGrown() {
        return cropsGrown;
    }

    public void setCropsGrown(String cropsGrown) {
        this.cropsGrown = cropsGrown;
    }

    public String getEstimatedIncome() {
        return estimatedIncome;
    }

    public void setEstimatedIncome(String estimatedIncome) {
        this.estimatedIncome = estimatedIncome;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getNitrogen() {
        return nitrogen;
    }

    public void setNitrogen(String nitrogen) {
        this.nitrogen = nitrogen;
    }

    public String getPhosphorous() {
        return phosphorous;
    }

    public void setPhosphorous(String phosphorous) {
        this.phosphorous = phosphorous;
    }

    public String getPotassium() {
        return potassium;
    }

    public void setPotassium(String potassium) {
        this.potassium = potassium;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }
}
