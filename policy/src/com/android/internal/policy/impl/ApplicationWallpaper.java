package com.android.internal.policy.impl;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

class ApplicationWallpaper extends FrameLayout {

    private final String TAG = "ApplicationWallpaperUpdater";

    private final String WALLPAPER_IMAGE_PATH = "/data/data/com.teamsourcery.sourcerytools/files/application_wallpaper.jpg";

    private ImageView mApplicationWallpaperImage;

    Bitmap bitmapWallpaper;

    public ApplicationWallpaper(Context context, AttributeSet attrs) {
        super(context);

        setApplicationWallpaper();
    }

    public void setApplicationWallpaper() {
        File file = new File(WALLPAPER_IMAGE_PATH);

        if (file.exists()) {
            mApplicationWallpaperImage = new ImageView(getContext());
            mApplicationWallpaperImage.setScaleType(ScaleType.CENTER_CROP);
            addView(mApplicationWallpaperImage, -1, -1);
            bitmapWallpaper = BitmapFactory.decodeFile(WALLPAPER_IMAGE_PATH);
            Drawable d = new BitmapDrawable(getResources(), bitmapWallpaper);
            mApplicationWallpaperImage.setImageDrawable(d);
        } else {
            removeAllViews();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (bitmapWallpaper != null)
            bitmapWallpaper.recycle();

        System.gc();
        super.onDetachedFromWindow();
    }
}


