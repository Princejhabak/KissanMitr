package com.example.android.myapp.helper;

public class CameraUploadHelper {

    private String imageName;
    private String imageDate;
    private String imageDownloadUrl;

    public CameraUploadHelper(){}

    public CameraUploadHelper(String imageName, String imageDate, String imageDownloadUrl) {
        this.imageName = imageName;
        this.imageDate = imageDate;
        this.imageDownloadUrl = imageDownloadUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageDate() {
        return imageDate;
    }

    public void setImageDate(String imageDate) {
        this.imageDate = imageDate;
    }

    public String getImageDownloadUrl() {
        return imageDownloadUrl;
    }

    public void setImageDownloadUrl(String imageDownloadUrl) {
        this.imageDownloadUrl = imageDownloadUrl;
    }
}
