package com.colorfit.coloring.colory.family.category;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colorfit.coloring.colory.family.MyApplication;
import com.colorfit.coloring.colory.family.R;
import com.colorfit.coloring.colory.family.model.PictureBean;
import com.colorfit.coloring.colory.family.paint.PaintActivity;
import com.nostra13.universalimageloader.utils.L;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DetailsCategoryActivity extends AppCompatActivity {

    private ImageView mButtonBack;
    private TextView mTextCategory;
    private RecyclerView mRecyclerCategory;
    private String mTitle;
    private List<PictureBean.Picture> mPictures;
    private DetailsCategoryAdapter mCategoryAdapter;
    private DetailsCategoryMyWorkAdapter mMyWorkAdapter;
    private String mPathData, mPathAsset;
    private ArrayList<String> mPhotos = new ArrayList<>();
    private boolean isCheckDownload, isCheckCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        initView();
        mTitle = getIntent().getStringExtra("title");
        if (mTitle.equalsIgnoreCase("MyWork")) {
            mTextCategory.setText(mTitle);
            getFromSdcard();
            initDataMyWork();
            onClickAdapter();
        } else {
            setData();
            loadLocalData();
        }
        onClick();
    }

    private void onClickAdapter() {
        mMyWorkAdapter.setOnRecycleViewItemClickListener(new DetailsCategoryMyWorkAdapter.OnRecycleViewItemClickListener() {
            @Override
            public void recycleViewItemClickListener(View view, String file) {
                Intent intent = new Intent(DetailsCategoryActivity.this, PaintActivity.class);
                intent.putExtra(MyApplication.MY_IMAGE, file);
                startActivityForResult(intent, 200);
            }
        });
    }

    public void getFromSdcard() {
        mPhotos = new ArrayList<>();
        File file = new File(android.os.Environment.getExternalStorageDirectory(), "/MyColoring");
        getFile(file);
    }

    public ArrayList<String> getFile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File file : listFile) {
//                if (file.isDirectory()) {
//                    getFile(file);
//                } else {
                if (file.getName().endsWith(".png")
                        || file.getName().endsWith(".jpg")
                        || file.getName().endsWith(".jpeg")) {
                    mPhotos.add(file.getAbsolutePath());
                }
            }
        }
        Collections.sort(mPhotos, new Comparator<String>() {
            @SuppressLint("NewApi")
            @Override
            public int compare(String o1, String o2) {
                File f1 = new File(o1);
                File f2 = new File(o2);

                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });
        return mPhotos;
    }

    private void initDataMyWork() {
        mMyWorkAdapter = new DetailsCategoryMyWorkAdapter(this, mPhotos);
        mRecyclerCategory.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerCategory.setAdapter(mMyWorkAdapter);
        mMyWorkAdapter.notifyDataSetChanged();
    }

    private void onClick() {
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setData() {
        if (mTitle != null) {
            mTextCategory.setText(mTitle);

            if (mTitle.equalsIgnoreCase("Animal")) {
                mPathAsset = "Data/animal";
                mPathData = MyApplication.DATALOCATION + MyApplication.ANIMAL;
            } else if (mTitle.equalsIgnoreCase("Animal Cute")) {
                mPathAsset = "Data/animals_cute";
                mPathData = MyApplication.DATALOCATION + MyApplication.ANIMAL_CUTE;
            } else if (mTitle.equalsIgnoreCase("Art Flower")) {
                mPathAsset = "Data/art_flowers";
                mPathData = MyApplication.DATALOCATION + MyApplication.ART_FLOWER;
            } else if (mTitle.equalsIgnoreCase("Flower")) {
                mPathAsset = "Data/flower";
                mPathData = MyApplication.DATALOCATION + MyApplication.FLOWER;
            } else if (mTitle.equalsIgnoreCase("Girl")) {
                mPathAsset = "Data/girl";
                mPathData = MyApplication.DATALOCATION + MyApplication.GIRL;
            } else if (mTitle.equalsIgnoreCase("Mandalas")) {
                mPathAsset = "Data/mandalas";
                mPathData = MyApplication.DATALOCATION + MyApplication.MANDALAS;
            } else if (mTitle.equalsIgnoreCase("Matrix")) {
                mPathAsset = "Data/matrix";
                mPathData = MyApplication.DATALOCATION + MyApplication.MATRIX;
            }
        }
        mRecyclerCategory.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initView() {
        mButtonBack = findViewById(R.id.button_back);
        mTextCategory = findViewById(R.id.text_category);
        mRecyclerCategory = findViewById(R.id.recycler_view_category);
    }

    private List<PictureBean.Picture> getDataBean(ArrayList<String> data) {
        List<PictureBean.Picture> pictureBeans = new ArrayList<>();
        for (String s : data) {
            pictureBeans.add(new PictureBean.Picture(s));
        }
        return pictureBeans;
    }

    public ArrayList<String> getMyWork(File dir) {
        ArrayList<String> mDataMyWork = new ArrayList<>();
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
        return mDataMyWork;
    }

    @SuppressLint("NewApi")
    private void loadLocalData() {
        try {
            mPictures = getDataBean(new ArrayList<>(Arrays.asList(this.getAssets().list(mPathAsset))));

            if (mPictures == null) {
                Toast.makeText(this, getString(R.string.loadfailed), Toast.LENGTH_SHORT).show();
            } else {
                showGrid();
            }
        } catch (IOException e) {
            L.e(e.toString());
            e.printStackTrace();
        }
    }

    private void showGrid() {
        mCategoryAdapter = new DetailsCategoryAdapter(this, mPictures, mTitle);
        mCategoryAdapter.setOnRecycleViewItemClickListener(new DetailsCategoryAdapter.OnRecycleViewItemClickListener() {
            @Override
            public void recycleViewItemClickListener(View view, int i) {
                gotoPaintActivity(mPictures.get(i).getUri());
//                Intent intent = new Intent(getContext(), PaintActivity.class);
//                intent.putExtra("data", (Serializable) pictureAll);
//                intent.putExtra("position", i);
//                intent.putExtra("fr0", "check fragment");
//                startActivity(intent);
            }
        });
        mRecyclerCategory.setAdapter(mCategoryAdapter);
    }

    private void gotoPaintActivity(String s) {
        Intent intent = new Intent(this, PaintActivity.class);
        intent.putExtra(MyApplication.BIGPIC, mPathData + s);
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                isCheckDownload = data.getBooleanExtra("check_download", false);
                isCheckCategory = data.getBooleanExtra("check_category_adapter", false);

                if (isCheckDownload && isCheckCategory) {
                    getFromSdcard();
                    initDataMyWork();
                    onClickAdapter();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("check_download", isCheckDownload);
        setResult(RESULT_OK, data);
        finish();
    }
}
