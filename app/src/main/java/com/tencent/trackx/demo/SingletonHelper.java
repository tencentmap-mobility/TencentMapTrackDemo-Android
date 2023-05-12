package com.tencent.trackx.demo;

import android.content.Context;

import com.tencent.trackx.api.TrackerX;
import com.tencent.trackx.api.TrackerXCreator;

public class SingletonHelper {

    private static volatile TrackerX instance;

    private SingletonHelper() {

    }

    public static TrackerX getInstance(Context context) {
        if (instance == null) {
            synchronized (SingletonHelper.class) {
                if (instance == null) {
                    instance = TrackerXCreator.instance(context).newTrackX();
                }
            }
        }
        return instance;
    }

}
