package com.wyp.materialqqlite;


import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

public class ExitApplication extends Application {

    private static ExitApplication sInstance;
    private List<Activity> mActivityList = new LinkedList<Activity>();

    public static ExitApplication getInstance() {
        if (sInstance == null) {
            sInstance = new ExitApplication();
        }
        return sInstance;
    }

    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public void exit() {
        for (Activity activity : mActivityList) {
            activity.finish();
        }
        System.exit(0);
    }
}

