package com.tencent.trackx.demo;

import android.app.Application;
import android.util.Log;

import com.tencent.navix.api.NavigatorConfig;
import com.tencent.navix.api.NavigatorZygote;
import com.tencent.tencentmap.mapsdk.maps.TencentMapInitializer;
import com.tencent.trackx.api.TrackerXCreator;

import java.util.Date;

public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 使用导航SDK轨迹源
        Log.i("TrackXInit", "MApplication onCreate");
        TencentMapInitializer.setAgreePrivacy(this, true);
        NavigatorZygote.with(this).init(NavigatorConfig.builder()
                .setUserAgreedPrivacy(true)
//                .setDeviceId("develop_123456")
                .setServiceConfig(NavigatorConfig.ServiceConfig.builder()
                        .build())
                .setMapOptions(NavigatorConfig.MapOptions.builder()
//                    .setTrafficStyle(TrafficStyle().also {
//                        it.setWidth(1)
//                    })
                        .build())
                .build());

        TrackerXCreator.setUserAgreePrivacy(true);
    }
}
