package com.example.android.myappadmin.helper;

public class PesticideHelper {

    private String crop;
    private String companyProduct;
    private int quantity;
    private String landOccupied;

    public PesticideHelper(){}

    public PesticideHelper(String crop, String companyProduct, int quantity, String landOccupied) {
        this.crop = crop;
        this.companyProduct = companyProduct;
        this.quantity = quantity;
        this.landOccupied = landOccupied;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getCompanyProduct() {
        return companyProduct;
    }

    public void setCompanyProduct(String companyProduct) {
        this.companyProduct = companyProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLandOccupied() {
        return landOccupied;
    }

    public void setLandOccupied(String landOccupied) {
        this.landOccupied = landOccupied;
    }
}
