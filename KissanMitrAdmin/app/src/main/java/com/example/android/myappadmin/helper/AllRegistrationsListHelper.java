package com.example.android.myappadmin.helper;

public class AllRegistrationsListHelper {

    private String dp;
    private String mobileNo;
    private String name;

    public AllRegistrationsListHelper(){}

    public AllRegistrationsListHelper(String dp, String mobileNo, String name) {
        this.dp = dp;
        this.mobileNo = mobileNo;
        this.name = name;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
