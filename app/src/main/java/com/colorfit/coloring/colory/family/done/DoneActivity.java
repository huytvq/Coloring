package com.colorfit.coloring.colory.family.done;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.colorfit.coloring.colory.family.R;
import com.colorfit.coloring.colory.family.home.HomeActivity;
import com.colorfit.coloring.colory.family.utils.BitmapHelper;

import java.io.File;
import java.io.FileNotFoundException;

public class DoneActivity extends AppCompatActivity {

    private TextView mButtonHome;
    private ImageView mButtonBack;
    private ImageView mImageResult;
    private ImageView mButtonFB, mButtonInsta, mButtonTwitter, mButtonShare;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);
        initView();
        mUrl = getIntent().getStringExtra("url");
        mImageResult.setImageBitmap(BitmapHelper.getInstance().getBitmap());
        onClick();
    }

    private void onClick() {
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoneActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        mButtonFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInstalledOrNot("com.facebook.katana")) {
                    shareSocial("com.facebook.katana");
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.facebook.katana")));
                }
            }
        });
        mButtonInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInstalledOrNot("com.instagram.android")) {
                    shareSocial("com.instagram.android");
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.instagram.android")));
                }
            }
        });
        mButtonTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInstalledOrNot("com.twitter.android")) {
                    shareSocial("com.twitter.android");
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.twitter.android")));
                }
            }
        });
        mButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFile(mUrl);
            }
        });
    }

    private void shareFile(String file) {
        File imageFile = new File(file);
        Uri imageUri = FileProvider.getUriForFile(
                this,
                "com.colorfit.coloring.colory.family.fileprovider",
                imageFile);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(shareIntent, "Choose an app"));
    }

    private void initView() {
        mButtonHome = findViewById(R.id.button_home);
        mButtonBack = findViewById(R.id.button_back);
        mImageResult = findViewById(R.id.image_result);
        mButtonFB = findViewById(R.id.button_fb);
        mButtonInsta = findViewById(R.id.button_insta);
        mButtonTwitter = findViewById(R.id.button_twitter);
        mButtonShare = findViewById(R.id.button_share);
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    private void shareSocial(String pkName) {
        String typeInsta = "image/*";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType(typeInsta);

        try {
            if (mUrl != null) {
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), mUrl, "", ""));
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setPackage(pkName);
                startActivity(Intent.createChooser(share, getResources().getString(R.string.app_name)));
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
