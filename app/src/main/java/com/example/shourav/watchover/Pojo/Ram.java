package com.example.shourav.watchover.Pojo;

import android.graphics.drawable.Drawable;

/**
 * Created by Musa on 9/17/2018.
 */

public class Ram {
    Drawable appIcon;
    String appName;

    public Ram(Drawable appIcon, String appName) {
        this.appIcon = appIcon;
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getAppName() {
        return appName;
    }
}
