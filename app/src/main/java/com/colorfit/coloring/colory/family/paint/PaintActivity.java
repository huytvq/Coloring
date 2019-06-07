package com.colorfit.coloring.colory.family.paint;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.colorfit.coloring.colory.family.MyApplication;
import com.colorfit.coloring.colory.family.R;
import com.colorfit.coloring.colory.family.done.DoneActivity;
import com.colorfit.coloring.colory.family.loader.AsynImageLoader;
import com.colorfit.coloring.colory.family.utils.BitmapHelper;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.snatik.storage.Storage;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.senab.photoview.ColourImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PaintActivity extends AppCompatActivity {
    private ColourImageView colourImageView;
    private PhotoViewAttacher mAttacher;
    private String URL;
    boolean isCheckColor1, isCheckColor2, isCheckColor3, isCheckColor4, isCheckColor5,
            isCheckColor6, isCheckColor7;
    private ImageView undo, redo, clear, done, delete;
    private ImageView color1, color2, color3, color4, color5, color6, color7;
    private int currentColorPaint;
    private int colorDefault = Color.parseColor("#ffffff");
    private ImageView fillColor;
    private ProgressDialog mProgressDialog;
    private ImageView mImageProgress;
    private boolean isCheckDownload;
    private String mPathMyWork;
    private boolean isCheckCatagory;
    private File fileSave;
    private Bitmap cachedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        initView();
        if (getIntent().hasExtra(MyApplication.BIGPIC)) {
            isCheckCatagory = false;
            URL = getIntent().getStringExtra(MyApplication.BIGPIC);
            loadLargeImage();
        } else if (getIntent().hasExtra(MyApplication.MY_IMAGE)) {
            isCheckCatagory = true;
            URL = "file://" + getIntent().getStringExtra(MyApplication.MY_IMAGE);
            mPathMyWork = getIntent().getStringExtra(MyApplication.MY_IMAGE);
            loadImageMyWork();
        }
        addEvent();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadImageMyWork() {
        showProgressDialog();
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                AsynImageLoader.showLagreImageAsynWithNoCacheOpen(colourImageView, URL, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        mProgressDialog.dismiss();
                        mImageProgress.clearAnimation();
                        Toast.makeText(PaintActivity.this, getString(R.string.loadpicturefailed), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        mAttacher = new PhotoViewAttacher(colourImageView, bitmap, PaintActivity.this);

                        isCheckColor1 = true;
                        currentColorPaint = getResources().getColor(R.color.red_500);
                        colourImageView.setColor(currentColorPaint);
                        color1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_up));

                        mProgressDialog.dismiss();
                        mImageProgress.clearAnimation();
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                        mProgressDialog.dismiss();
                        mImageProgress.clearAnimation();
                        Toast.makeText(PaintActivity.this, getString(R.string.loadpicturefailed), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }.execute();
    }

    private void addEvent() {
        onUndoRedo();
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colourImageView.undo();
            }
        });
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colourImageView.redo();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repaint();
            }
        });
        findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        onButtonColor1();
        onButtonColor2();
        onButtonColor3();
        onButtonColor4();
        onButtonColor5();
        onButtonColor6();
        onButtonColor7();

        onColorPicker();
        onFillColor();
        onClearColor();
        onSave();
    }

    private void onSave() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cachedBitmap = colourImageView.getBitmap();
                BitmapHelper.getInstance().setBitmap(cachedBitmap);

                if (BitmapHelper.getInstance().getBitmap() == null) {
                    Toast.makeText(PaintActivity.this, "Image Empty. Please wait...", Toast.LENGTH_SHORT).show();
                } else {
                    isCheckDownload = true;
                    saveToLocal(colourImageView.getBitmap());
                    if (isCheckCatagory) {
                        Storage storage = new Storage(getApplicationContext());
                        storage.deleteFile(mPathMyWork);
                    }
                    Intent intent = new Intent(PaintActivity.this, DoneActivity.class);
                    intent.putExtra("url", String.valueOf(fileSave));
                    startActivity(intent);
                }


            }
        });
    }

    private void onClearColor() {
        delete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (isCheckColor1) {
                    isCheckColor1 = false;
                    color1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }
                if (isCheckColor3) {
                    isCheckColor3 = false;
                    color3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }
                if (isCheckColor4) {
                    isCheckColor4 = false;
                    color4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }
                if (isCheckColor2) {
                    isCheckColor2 = false;
                    color2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }
                if (isCheckColor5) {
                    isCheckColor5 = false;
                    color5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }
                if (isCheckColor6) {
                    isCheckColor6 = false;
                    color6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }
                if (isCheckColor7) {
                    isCheckColor7 = false;
                    color7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }

                colourImageView.setEnabled(true);
                fillColor.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.brown_500)));
                delete.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                colourImageView.setColor(getResources().getColor(R.color.white));
            }
        });
    }

    private void onFillColor() {
        fillColor.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                colourImageView.setModel(ColourImageView.Model.FILLCOLOR);
                fillColor.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                colourImageView.setEnabled(true);
                delete.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.brown_500)));
                colourImageView.setColor(currentColorPaint);
            }
        });
    }

    private void onColorPicker() {
        findViewById(R.id.colorPicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckColor1) {
                    isCheckColor1 = false;
                    color1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }
                if (isCheckColor3) {
                    isCheckColor3 = false;
                    color3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }
                if (isCheckColor4) {
                    isCheckColor4 = false;
                    color4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }
                if (isCheckColor2) {
                    isCheckColor2 = false;
                    color2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }
                if (isCheckColor5) {
                    isCheckColor5 = false;
                    color5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }
                if (isCheckColor6) {
                    isCheckColor6 = false;
                    color6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }
                if (isCheckColor7) {
                    isCheckColor7 = false;
                    color7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                }
                new ColorPickerDialog.Builder(PaintActivity.this)
                        .setPreferenceName("MyColorPickerDialog")
                        .setPositiveButton(getString(R.string.ok),
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        colourImageView.setColor(envelope.getColor());
                                    }
                                }).setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .attachAlphaSlideBar(true) // default is true. If false, do not show the AlphaSlideBar.
                        .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                        .show();
            }
        });
    }

    private void onUndoRedo() {
        colourImageView.setOnRedoUndoListener(new ColourImageView.OnRedoUndoListener() {
            @SuppressLint("NewApi")
            @Override
            public void onRedoUndo(int undoSize, int redoSize) {
                if (undoSize != 0) {
                    undo.setEnabled(true);
                    undo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                    done.setEnabled(true);
                    done.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                    clear.setEnabled(true);
                    clear.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                } else {
                    undo.setEnabled(false);
                    undo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.brown_500)));
                    done.setEnabled(false);
                    done.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.brown_500)));
                    clear.setEnabled(false);
                    clear.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.brown_500)));
                }
                if (redoSize != 0) {
                    redo.setEnabled(true);
                    redo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                } else {
                    redo.setEnabled(false);
                    redo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.brown_500)));
                }
            }
        });
    }

    private void onButtonColor2() {
        color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckColor2) {
                    color2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                    isCheckColor2 = false;
                    colourImageView.setColor(colorDefault);
                } else {
                    isCheckColor2 = true;
                    currentColorPaint = getResources().getColor(R.color.orange_500);
                    colourImageView.setColor(currentColorPaint);
                    color2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up));
                    if (isCheckColor1) {
                        isCheckColor1 = false;
                        color1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor3) {
                        isCheckColor3 = false;
                        color3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor4) {
                        isCheckColor4 = false;
                        color4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor5) {
                        isCheckColor5 = false;
                        color5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor6) {
                        isCheckColor6 = false;
                        color6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor7) {
                        isCheckColor7 = false;
                        color7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                }
            }
        });
    }

    private void onButtonColor3() {
        color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckColor3) {
                    color3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                    isCheckColor3 = false;
                    colourImageView.setColor(colorDefault);
                } else {
                    isCheckColor3 = true;
                    currentColorPaint = getResources().getColor(R.color.yellow_500);
                    colourImageView.setColor(currentColorPaint);
                    color3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up));
                    if (isCheckColor1) {
                        isCheckColor1 = false;
                        color1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor2) {
                        isCheckColor2 = false;
                        color2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor4) {
                        isCheckColor4 = false;
                        color4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor5) {
                        isCheckColor5 = false;
                        color5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor6) {
                        isCheckColor6 = false;
                        color6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor7) {
                        isCheckColor7 = false;
                        color7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                }
            }
        });
    }

    private void onButtonColor4() {
        color4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckColor4) {
                    color4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                    isCheckColor4 = false;
                    colourImageView.setColor(colorDefault);
                } else {
                    isCheckColor4 = true;
                    currentColorPaint = getResources().getColor(R.color.green_500);
                    colourImageView.setColor(currentColorPaint);
                    color4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up));
                    if (isCheckColor1) {
                        isCheckColor1 = false;
                        color1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor3) {
                        isCheckColor3 = false;
                        color3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor2) {
                        isCheckColor2 = false;
                        color2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor5) {
                        isCheckColor5 = false;
                        color5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor6) {
                        isCheckColor6 = false;
                        color6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor7) {
                        isCheckColor7 = false;
                        color7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                }
            }
        });
    }

    private void onButtonColor5() {
        color5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckColor5) {
                    color5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                    isCheckColor5 = false;
                    colourImageView.setColor(colorDefault);
                } else {
                    isCheckColor5 = true;
                    currentColorPaint = getResources().getColor(R.color.blue_900);
                    colourImageView.setColor(currentColorPaint);
                    color5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up));
                    if (isCheckColor1) {
                        isCheckColor1 = false;
                        color1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor3) {
                        isCheckColor3 = false;
                        color3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor4) {
                        isCheckColor4 = false;
                        color4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor2) {
                        isCheckColor2 = false;
                        color2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor6) {
                        isCheckColor6 = false;
                        color6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor7) {
                        isCheckColor7 = false;
                        color7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                }
            }
        });
    }

    private void onButtonColor6() {
        color6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckColor6) {
                    color6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                    isCheckColor6 = false;
                    colourImageView.setColor(colorDefault);
                } else {
                    isCheckColor6 = true;
                    currentColorPaint = getResources().getColor(R.color.blue_300);
                    colourImageView.setColor(currentColorPaint);
                    color6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up));
                    if (isCheckColor1) {
                        isCheckColor1 = false;
                        color1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor3) {
                        isCheckColor3 = false;
                        color3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor4) {
                        isCheckColor4 = false;
                        color4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor2) {
                        isCheckColor2 = false;
                        color2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor5) {
                        isCheckColor5 = false;
                        color5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor7) {
                        isCheckColor7 = false;
                        color7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                }
            }
        });
    }

    private void onButtonColor7() {
        color7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckColor7) {
                    color7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                    isCheckColor7 = false;
                    colourImageView.setColor(colorDefault);
                } else {
                    isCheckColor7 = true;
                    currentColorPaint = getResources().getColor(R.color.pink_500);
                    colourImageView.setColor(currentColorPaint);
                    color7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up));
                    if (isCheckColor1) {
                        isCheckColor1 = false;
                        color1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor3) {
                        isCheckColor3 = false;
                        color3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor4) {
                        isCheckColor4 = false;
                        color4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor2) {
                        isCheckColor2 = false;
                        color2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor6) {
                        isCheckColor6 = false;
                        color6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor5) {
                        isCheckColor5 = false;
                        color5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                }
            }
        });
    }

    private void onButtonColor1() {
        color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckColor1) {
                    color1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down));
                    isCheckColor1 = false;
                    colourImageView.setColor(colorDefault);
                } else {
                    isCheckColor1 = true;
                    currentColorPaint = getResources().getColor(R.color.red_500);
                    colourImageView.setColor(currentColorPaint);
                    color1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up));
                    if (isCheckColor2) {
                        isCheckColor2 = false;
                        color2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor3) {
                        isCheckColor3 = false;
                        color3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor4) {
                        isCheckColor4 = false;
                        color4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor5) {
                        isCheckColor5 = false;
                        color5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor6) {
                        isCheckColor6 = false;
                        color6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                    if (isCheckColor7) {
                        isCheckColor7 = false;
                        color7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down));
                    }
                }
            }
        });
    }

    private void repaint() {
//        MyProgressDialog.show(this, null, getString(R.string.loadpicture));
        colourImageView.clearStack();
        AsynImageLoader.showLagreImageAsynWithAllCacheOpen(colourImageView, URL, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
//                MyProgressDialog.DismissDialog();
                Toast.makeText(PaintActivity.this, getString(R.string.loadpicturefailed), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                mAttacher = new PhotoViewAttacher(colourImageView, bitmap, PaintActivity.this);
//                MyProgressDialog.DismissDialog();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
//                MyProgressDialog.DismissDialog();
                Toast.makeText(PaintActivity.this, getString(R.string.loadpicturefailed), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void initView() {
        colourImageView = findViewById(R.id.fillImageview);
        undo = findViewById(R.id.undo);
        redo = findViewById(R.id.redo);
        clear = findViewById(R.id.clear);
        done = findViewById(R.id.done);
//        brush = findViewById(R.id.brush);
        fillColor = findViewById(R.id.fillColor);
        delete = findViewById(R.id.delete);
        color1 = findViewById(R.id.color1);
        color2 = findViewById(R.id.color2);
        color3 = findViewById(R.id.color3);
        color4 = findViewById(R.id.color4);
        color5 = findViewById(R.id.color5);
        color6 = findViewById(R.id.color6);
        color7 = findViewById(R.id.color7);
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("check_download", isCheckDownload);
        data.putExtra("check_category_adapter", isCheckCatagory);
        setResult(RESULT_OK, data);
        finish();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadLargeImage() {
        showProgressDialog();
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                AsynImageLoader.showLagreImageAsynWithAllCacheOpen(colourImageView, URL, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        mProgressDialog.dismiss();
                        mImageProgress.clearAnimation();
                        Toast.makeText(PaintActivity.this, getString(R.string.loadpicturefailed), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        mAttacher = new PhotoViewAttacher(colourImageView, bitmap, PaintActivity.this);
                        //rotate screen load bitmap saved in the savestates
//                        if (cachedBitmap != null) {
//                            Glide.with(view).load(cachedBitmap).into(colourImageView);
//                        } else {
////                            openSaveImage(ImageSaveUtil.convertImageLageUrl(URL).hashCode());
//                            showHintDialog();
//                        }
                        isCheckColor1 = true;
                        currentColorPaint = getResources().getColor(R.color.red_500);
                        colourImageView.setColor(currentColorPaint);
                        color1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_up));

                        mProgressDialog.dismiss();
                        mImageProgress.clearAnimation();
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                        mProgressDialog.dismiss();
                        mImageProgress.clearAnimation();
                        Toast.makeText(PaintActivity.this, getString(R.string.loadpicturefailed), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }.execute();
    }

    public void saveToLocal(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dir = new File(root + "/MyColoring");
        if (!dir.exists()) {
            dir.mkdir();
        }
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timeStamp = simpleDateFormat.format(new Date());
        String fileName = timeStamp + ".png";
        fileSave = new File(dir, fileName);
        if (fileSave.exists()) fileSave.delete();
        try {
            FileOutputStream out = new FileOutputStream(fileSave);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog() {
        mProgressDialog = ProgressDialog.show(this, null, null);
        mProgressDialog.setContentView(R.layout.progress_dialog);
        mProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mImageProgress = mProgressDialog.findViewById(R.id.image_progress);
        mImageProgress.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_360));
    }

    public class TransparentProgressDialog extends Dialog {

        public TransparentProgressDialog(Context context) {
            super(context);

            WindowManager.LayoutParams wlmp = getWindow().getAttributes();

            wlmp.gravity = Gravity.CENTER_HORIZONTAL;
            getWindow().setAttributes(wlmp);
            setTitle(null);
            setCancelable(false);
            setOnCancelListener(null);
            View view = LayoutInflater.from(context).inflate(
                    R.layout.progress_dialog, null);
            setContentView(view);
        }
    }
}
