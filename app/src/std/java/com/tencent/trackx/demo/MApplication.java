package com.tencent.trackx.demo;

import android.app.Application;

import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.trackx.api.TrackerXCreator;

public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 使用定位SDK轨迹源
        TencentLocationManager.setUserAgreePrivacy(true);

        TrackerXCreator.setUserAgreePrivacy(true);
    }
}
