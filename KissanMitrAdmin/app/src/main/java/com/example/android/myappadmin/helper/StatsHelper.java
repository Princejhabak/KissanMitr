package com.example.android.myappadmin.helper;

public class StatsHelper {

    private String location;
    private String address;
    private String audioCount;
    private String imageCount;

    public StatsHelper(){}

    public StatsHelper(String location, String address,String audioCount, String imageCount ) {
        this.location = location;
        this.address = address;
        this.audioCount = audioCount;
        this.imageCount = imageCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAudioCount() {
        return audioCount;
    }

    public void setAudioCount(String audioCount) {
        this.audioCount = audioCount;
    }

    public String getImageCount() {
        return imageCount;
    }

    public void setImageCount(String imageCount) {
        this.imageCount = imageCount;
    }
}
