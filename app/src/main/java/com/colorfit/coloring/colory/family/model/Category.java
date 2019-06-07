package com.colorfit.coloring.colory.family.model;

public class Category {
    private String mName;
    private String mPhoto;

    public Category(String name, String photo) {
        mName = name;
        mPhoto = photo;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }
}
