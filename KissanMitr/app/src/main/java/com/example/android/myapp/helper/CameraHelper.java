package com.example.android.myapp.helper;

import java.util.Date;

public class CameraHelper {

    private String path ;
    private String name;
    private Date date ;


    public CameraHelper(){}

    public CameraHelper(String path, String name, Date date){
        this.path = path ;
        this.name = name ;
        this.date = date ;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
