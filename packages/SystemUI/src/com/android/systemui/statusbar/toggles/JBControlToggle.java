package com.android.systemui.statusbar.toggles;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.android.systemui.R;

public class JBControlToggle extends BaseToggle {

    @Override
    public void init(Context c, int style) {
        super.init(c, style);
        setIcon(R.drawable.ic_qs_control);
        setLabel(R.string.quick_settings_jbcontrol_label);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(ComponentName
                .unflattenFromString("com.sourcery.magiccontrol/.MagicControlActivity"));
        intent.addCategory("android.intent.category.LAUNCHER");

        collapseStatusBar();
        dismissKeyguard();
        startActivity(intent);
    }

}
