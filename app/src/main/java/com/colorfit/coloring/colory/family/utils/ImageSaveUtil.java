package com.colorfit.coloring.colory.family.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import com.nostra13.universalimageloader.utils.L;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageSaveUtil {
    public static final String OldMainUrl = "http://23.99.106.254";
    public static final String ThemeThumbUrl = OldMainUrl + "/%d/category.png"; //get add categoryid + /category.png
    public static final String ImageThumbUrl = OldMainUrl + "/%d/t_%d.jpg"; //get add categoryid and imageid
    public static final String ImageLageUrl = OldMainUrl + "/%d/f_%d.png"; //get add categoryid and imageid

    private static ImageSaveUtil ourInstance = new ImageSaveUtil();
    public static File mDir;
    public static String mFileName;

    public static ImageSaveUtil getInstance() {
        return ourInstance;
    }

    public static String convertImageLageUrl(String url) {
        try {
            int[] ids = parseIds(url);
            return String.format(ImageLageUrl, ids[0], ids[1]);
        } catch (Exception e) {
            L.e(e.toString());
            return url;
        }
    }

    private static int[] parseIds(String url) throws Exception {
        int[] ids = new int[2];
        ids[0] = getNum(url.split(new String("&image="))[0]);
        ids[1] = getNum(url.split(new String("&image="))[1]);
        return ids;
    }

    private static int getNum(String str) throws Exception {
        String dest = "";
        if (str != null) {
            dest = str.replaceAll("[^0-9]", "");

        }
        return Integer.valueOf(dest);
    }

    public static void saveToLocal(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        mDir = new File(root + "/MyColoring");
        mDir.mkdirs();
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timeStamp = simpleDateFormat.format(new Date());
        mFileName = timeStamp + ".png";
        File file = new File(mDir, mFileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
