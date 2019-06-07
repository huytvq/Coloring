package com.colorfit.coloring.colory.family.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colorfit.coloring.colory.family.R;
import com.colorfit.coloring.colory.family.category.DetailsCategoryActivity;
import com.colorfit.coloring.colory.family.model.Category;
import com.colorfit.coloring.colory.family.model.PictureBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private CategoryAdapter categoryAdapter;
    private ArrayList<String> mDataMyWork;
    private RecyclerView mRecyclerView;
    private List<PictureBean.Picture> mPictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        runtime_permission();
    }

    private List<PictureBean.Picture> getDataBean(ArrayList<String> data) {
        List<PictureBean.Picture> pictureBeans = new ArrayList<>();
        for (String s : data) {
            pictureBeans.add(new PictureBean.Picture(s));
        }
        return pictureBeans;
    }

    private void onClickAdapter() {
        categoryAdapter.setOnClickListener(new CategoryAdapter.OnClickListener() {
            @Override
            public void onClick(String title) {
                Intent intent = new Intent(HomeActivity.this, DetailsCategoryActivity.class);
                intent.putExtra("title", title);
                startActivityForResult(intent, 111);
            }
        });
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view_home);
    }

    private void loadData() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/MyColoring");

        if (dir.exists() && dir.isDirectory()) {
            getMyWork(dir);
            if (mDataMyWork.size() > 0) {
                categoryAdapter = new CategoryAdapter(this, getCategory(true));
            }
        } else {
            categoryAdapter = new CategoryAdapter(this, getCategory(false));
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.smoothScrollBy(0, 0);
        mRecyclerView.setAdapter(categoryAdapter);
    }

    private void runtime_permission() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 123);
        } else {
            loadImageAsset();
            initView();
            loadData();
            onClickAdapter();
        }
    }

    private void loadImageAsset() {
        try {
            mPictures = getDataBean(new ArrayList<>(Arrays.asList(this.getAssets().list("Image"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                loadImageAsset();
                initView();
                loadData();
                onClickAdapter();
            } else {
                runtime_permission();
            }
        }
    }

    public ArrayList<String> getMyWork(File dir) {
        mDataMyWork = new ArrayList<>();
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File file : listFile) {
//                if (file.isDirectory()) {
//                    getFile(file);
//                } else {
                if (file.getName().endsWith(".png")
                        || file.getName().endsWith(".jpg")
                        || file.getName().endsWith(".jpeg")) {
                    mDataMyWork.add(file.getAbsolutePath());
                }
            }
//            }
        }
        Collections.sort(mDataMyWork, new Comparator<String>() {
            @SuppressLint("NewApi")
            @Override
            public int compare(String o1, String o2) {
                File f1 = new File(o1);
                File f2 = new File(o2);

                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });
        return mDataMyWork;
    }

    private List<Category> getCategory(boolean isCheckFolder) {
        List<Category> categories = new ArrayList<>();

        if (isCheckFolder) {
            categories.add(new Category("MyWork", mDataMyWork.get(0)));
        }
        categories.add(new Category("Animal", mPictures.get(0).getUri()));
        categories.add(new Category("Animal Cute", mPictures.get(1).getUri()));
        categories.add(new Category("Art Flower", mPictures.get(2).getUri()));
        categories.add(new Category("Flower", mPictures.get(3).getUri()));
        categories.add(new Category("Girl", mPictures.get(4).getUri()));
        categories.add(new Category("Mandalas", mPictures.get(5).getUri()));
        categories.add(new Category("Matrix", mPictures.get(6).getUri()));

        return categories;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                boolean isCheckDownload = data.getBooleanExtra("check_download", false);
                if (isCheckDownload) {
                    loadData();
                    onClickAdapter();
                }
            }
        }
    }
}
