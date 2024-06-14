package com.example.android_mas.products;

public class Product {
    public String uid; // This is the userId
    public String productId;
    public String name;
    public String description;
    public String pricePerKg;
    public String productImage;
    public String producеtProfile;
    public String nameprofile;



    public Product(String uid, String productId, String name, String description, String pricePerKg, String productImage, String producеtProfile, String nameprofile) {
        this.uid = uid;
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.pricePerKg = pricePerKg;
        this.productImage = productImage;
        this.producеtProfile = producеtProfile;
        this.nameprofile = nameprofile;
    }

    public String getUid() {
        return uid;
    }

    public String getProductId() {
        return productId;
    }
}
