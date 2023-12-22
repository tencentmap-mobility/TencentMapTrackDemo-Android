package com.tencent.trackx.demo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.navix.api.layer.NavigatorLayerRootDrive;
import com.tencent.navix.api.layer.NavigatorViewStub;
import com.tencent.navix.api.map.MapApi;
import com.tencent.trackx.demo.R;

public class TrackMapView extends FrameLayout {

    private NavigatorLayerRootDrive navigatorLayerRootDrive;

    public TrackMapView(@NonNull Context context) {
        super(context);
        init();
    }

    public TrackMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TrackMapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.track_map_view_layout, this);
        NavigatorViewStub viewStub = this.findViewById(R.id.navigation_view);
        viewStub.inflate();
        navigatorLayerRootDrive = viewStub.getNavigatorView();
    }

    public void onStart() {
        navigatorLayerRootDrive.onStart();
    }

    public void onResume() {
        navigatorLayerRootDrive.onResume();
    }

    public void onPause() {
        navigatorLayerRootDrive.onPause();
    }

    public void onStop() {
        navigatorLayerRootDrive.onStop();
    }

    public void onDestroy() {
        navigatorLayerRootDrive.onDestroy();
    }

    public MapApi getMapApi() {
        return navigatorLayerRootDrive.getMapApi();
    }
}
