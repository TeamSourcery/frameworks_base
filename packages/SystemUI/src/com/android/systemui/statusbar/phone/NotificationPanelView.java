/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar.phone;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Slog;
import android.view.MotionEvent;
import android.view.View;

import com.android.systemui.R;
import com.android.systemui.statusbar.GestureRecorder;

public class NotificationPanelView extends PanelView {

    private final String NOTIF_WALLPAPER_IMAGE_PATH = "/data/data/com.teamsourcery.sourcerytools/files/notification_wallpaper.jpg";
    private final String NOTIF_WALLPAPER_IMAGE_PATH_LAND = "/data/data/com.teamsourcery.sourcerytools/files/notification_wallpaper_land.jpg";
    
    private int mScreenOrientation;

    float wallpaperAlpha = Settings.System.getFloat(getContext()
            .getContentResolver(), Settings.System.NOTIF_WALLPAPER_ALPHA, 1.0f);

    Drawable mHandleBar;
    float mHandleBarHeight;
    View mHandleView;
    int mFingers;
    PhoneStatusBar mStatusBar;
    boolean mOkToFlip;

    public NotificationPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    	setNotificationWallpaper();
    }

    public void setNotificationWallpaper() {
	File portrait = new File(NOTIF_WALLPAPER_IMAGE_PATH);
        File landscape = new File(NOTIF_WALLPAPER_IMAGE_PATH_LAND);
	mScreenOrientation = getContext().getResources().getConfiguration().orientation;
        boolean isPortrait =  mScreenOrientation == Configuration.ORIENTATION_PORTRAIT;

        if (isPortrait) {
            if (portrait.exists()) {
                Drawable d = Drawable.createFromPath(NOTIF_WALLPAPER_IMAGE_PATH);
                d.setAlpha((int) (wallpaperAlpha * 255));
                this.setBackground(d);
            } else {
		this.setBackground(this.getResources().getDrawable(R.drawable.notification_panel_bg));
	    }
        } else {
            if (landscape.exists()) {
                Drawable d = Drawable.createFromPath(NOTIF_WALLPAPER_IMAGE_PATH_LAND);
                d.setAlpha((int) (wallpaperAlpha * 255));
	        this.setBackground(d);
            } else {
		this.setBackground(this.getResources().getDrawable(R.drawable.notification_panel_bg));
	    }
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
	//super.onConfigurationChanged(newConfig);
	if (newConfig.orientation != mScreenOrientation) {
		setNotificationWallpaper();
	}
    }

    public void setStatusBar(PhoneStatusBar bar) {
        mStatusBar = bar;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Resources resources = getContext().getResources();
        mHandleBar = resources.getDrawable(R.drawable.status_bar_close);
        mHandleBarHeight = resources.getDimension(R.dimen.close_handle_height);
        mHandleView = findViewById(R.id.handle);

        setContentDescription(resources.getString(R.string.accessibility_desc_notification_shade));

    }

    @Override
    public void fling(float vel, boolean always) {
        GestureRecorder gr = ((PhoneStatusBarView) mBar).mBar.getGestureRecorder();
        if (gr != null) {
            gr.tag(
                "fling " + ((vel > 0) ? "open" : "closed"),
                "notifications,v=" + vel);
        }
        super.fling(vel, always);
    }

    // We draw the handle ourselves so that it's always glued to the bottom of the window.
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            final int pl = getPaddingLeft();
            final int pr = getPaddingRight();
            mHandleBar.setBounds(pl, 0, getWidth() - pr, (int) mHandleBarHeight);

        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        final int off = (int) (getHeight() - mHandleBarHeight - getPaddingBottom());
        canvas.translate(0, off);
        mHandleBar.setState(mHandleView.getDrawableState());
        mHandleBar.draw(canvas);
        canvas.translate(0, -off);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (PhoneStatusBar.SETTINGS_DRAG_SHORTCUT && mStatusBar.mHasFlipSettings) {
            boolean shouldFlip = false;

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mOkToFlip = getExpandedHeight() == 0;
                    if(mStatusBar.skipToSettingsPanel()) {
                        shouldFlip = true;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (mOkToFlip) {
                        float miny = event.getY(0);
                        float maxy = miny;
                        for (int i=1; i<event.getPointerCount(); i++) {
                            final float y = event.getY(i);
                            if (y < miny) miny = y;
                            if (y > maxy) maxy = y;
                        }
                        if (maxy - miny < mHandleBarHeight) {
                            shouldFlip = true;
                        }
                    }
                    break;
            }
            if(mOkToFlip && shouldFlip) {
                if (getMeasuredHeight() < mHandleBarHeight) {
                    mStatusBar.switchToSettings();
                } else {
                    mStatusBar.flipToSettings();
                }
                mOkToFlip = false;
            }
        }
        return mHandleView.dispatchTouchEvent(event);
    }
}
