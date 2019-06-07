package com.colorfit.coloring.colory.family;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.WindowManager;

import com.colorfit.coloring.colory.family.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

public class MyApplication extends Application {
    public static final String DATALOCATION = "assets://Data/";
    public static final String GIRL = "girl/";
    public static final String ANIMAL = "animal/";
    public static final String ANIMAL_CUTE = "animals_cute/";
    public static final String FLOWER = "flower/";
    public static final String ART_FLOWER = "art_flowers/";
    public static final String MANDALAS = "mandalas/";
    public static final String MATRIX = "matrix/";
    public static final String BIGPIC = "bigpic";
    public static final String MY_IMAGE = "MY_IMAGE";
    public static int screenWidth;

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
        screenWidth = getScreenWidth(this);
    }

    public void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(100 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoaderUtil.getInstance().init(config);
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public static String getVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            L.e("VersionE", e.getMessage());
            e.printStackTrace();
            return "0";
        }
    }
}
