
package com.android.systemui.statusbar.toggles;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;

import com.android.systemui.R;

public class PWToggle extends StatefulToggle {

    SettingsObserver mObserver = null;

    @Override
    public void init(Context c, int style) {
        super.init(c, style);
        mObserver = new SettingsObserver(mHandler);
        mObserver.observe();
    }

    @Override
    protected void cleanup() {
        if (mObserver != null) {
            mContext.getContentResolver().unregisterContentObserver(mObserver);
            mObserver = null;
        }
        super.cleanup();
    }

    @Override
    protected void doEnable() {
        Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.EXPANDED_VIEW_WIDGET, 1);
    }

    @Override
    protected void doDisable() {
        Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.EXPANDED_VIEW_WIDGET, 0);
    }

    @Override
    protected void updateView() {
        boolean enabled = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.EXPANDED_VIEW_WIDGET, 0) == 1;
        setEnabledState(enabled);
        setIcon(enabled ? R.drawable.ic_pwtoggle_on : R.drawable.ic_pwtoggle_off);
        setLabel(enabled ? R.string.quick_settings_pwtoggle_on
                : R.string.quick_settings_pwtoggle_off);
        super.updateView();
    }

    class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            ContentResolver resolver = mContext.getContentResolver();
            resolver.registerContentObserver(Settings.System
                    .getUriFor(Settings.System.EXPANDED_VIEW_WIDGET), false,
                    this);
        }

        @Override
        public void onChange(boolean selfChange) {
            scheduleViewUpdate();
        }
    }

}
