package com.muhammadandmustafa.khadamatseller.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String name, price, description, imgURL, sellerID;

    public Product() {
    }

    public Product(String name,
                   String price,
                   String description,
                   String imgURL,
                   String sellerID) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imgURL = imgURL;
        this.sellerID = sellerID;
    }

    protected Product(Parcel in) {
        name = in.readString();
        price = in.readString();
        description = in.readString();
        imgURL = in.readString();
        sellerID = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeString(imgURL);
        dest.writeString(sellerID);
    }
}
