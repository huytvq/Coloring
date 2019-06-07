package com.colorfit.coloring.colory.family.utils;

import android.graphics.Bitmap;

import java.io.Serializable;

public class BitmapHelper implements Serializable {
    private Bitmap mBitmap = null;
    private static final BitmapHelper instance = new BitmapHelper();

    public BitmapHelper() {
    }

    public static BitmapHelper getInstance() {
        return instance;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}
