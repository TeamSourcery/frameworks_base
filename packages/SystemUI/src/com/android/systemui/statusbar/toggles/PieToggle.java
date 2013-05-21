
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

public class PieToggle extends StatefulToggle {

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
                Settings.System.PIE_CONTROLS, 2);
    }

    @Override
    protected void doDisable() {
        Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.PIE_CONTROLS, 0);
    }

    @Override
    protected void updateView() {
        boolean enabled = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_CONTROLS, 0) == 2;
        setEnabledState(enabled);
        setIcon(enabled ? R.drawable.ic_qs_pie_on : R.drawable.ic_qs_pie_off);
        setLabel(enabled ? R.string.quick_settings_pie_on_label
                : R.string.quick_settings_pie_off_label);
        super.updateView();
    }

    class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            ContentResolver resolver = mContext.getContentResolver();
            resolver.registerContentObserver(Settings.System
                    .getUriFor(Settings.System.PIE_CONTROLS), false,
                    this);
        }

        @Override
        public void onChange(boolean selfChange) {
            scheduleViewUpdate();
        }
    }

}
