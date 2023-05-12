package com.tencent.trackx.demo.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.tencent.trackx.api.CacheTracePointImpl;
import com.tencent.trackx.api.TrackerX;
import com.tencent.trackx.api.model.CacheTracePoint;
import com.tencent.trackx.api.model.EntityVerifiedResult;
import com.tencent.trackx.api.model.EvictedInfo;
import com.tencent.trackx.api.model.RefluxResult;
import com.tencent.trackx.api.trace.TerminalOptions;
import com.tencent.trackx.api.trace.TraceConfig;
import com.tencent.trackx.api.trace.TraceEventCallback;
import com.tencent.trackx.api.trace.TracePointSource;
import com.tencent.trackx.api.trace.Tracer;
import com.tencent.trackx.demo.BuildConfig;
import com.tencent.trackx.demo.R;
import com.tencent.trackx.demo.SingletonHelper;
import com.tencent.trackx.support.nav.loc.TrackerXNavLocation;
import com.tencent.trackx.support.std.loc.TrackerXStandardLocation;

public class TrackxUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TrackxUpload";

    private TrackerX mTrackX;
    private Tracer mTracer;
    private TraceConfig traceConfig;

    private TextView mLatestPointText;
    private TextView mUploadResultText;

    private boolean mTraceIntervalEnable = true;
    private boolean packIntervalEnable = true;
    private boolean mMaxCacheCountEnable = true;
    private int mProviderTypeEnable = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackx_upload);
        mTrackX = SingletonHelper.getInstance(getApplicationContext());

        findViewById(R.id.start_service_button).setOnClickListener(this);
        findViewById(R.id.end_service_button).setOnClickListener(this);
        findViewById(R.id.start_trace_button).setOnClickListener(this);
        findViewById(R.id.end_trace_button).setOnClickListener(this);


        // 其他配置
        findViewById(R.id.采集频率).setOnClickListener(this);
        findViewById(R.id.上报频率).setOnClickListener(this);
        findViewById(R.id.最大缓存条数).setOnClickListener(this);
        findViewById(R.id.定位类型).setOnClickListener(this);

        mLatestPointText = findViewById(R.id.lastest_point_text);
        mLatestPointText.setMovementMethod(ScrollingMovementMethod.getInstance());
        mUploadResultText = findViewById(R.id.upload_result_text);
        mUploadResultText.setMovementMethod(ScrollingMovementMethod.getInstance());

        traceConfig = TraceConfig.newBuilder().build();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.start_trace_button) {
            if (mTracer == null) {
                mTracer = mTrackX.tracer(TerminalOptions.newBuilder("your service id", "your entity name")
                        .entityId("your entity id")
                        .build());
            }

            if (BuildConfig.FLAVOR.equals("std")) {
                // 定位轨迹源
                mTracer.setTracePointSource(TrackerXStandardLocation.create(getApplicationContext()));
            } else {
                // 导航轨迹源
                mTracer.setTracePointSource(TrackerXNavLocation.create(getApplicationContext()));
            }

            mTracer.setConfig(traceConfig);
            mTracer.addTraceEventCallback(traceEventCallback);
            mTracer.beginTrace();
        } else if (id == R.id.end_trace_button) {
            if (mTracer == null) {
                mTracer = mTrackX.tracer(TerminalOptions.newBuilder("your service id", "your entity name")
                        .entityId("your entity id")
                        .build());
            }
            mTracer.endTrace(true);
        } else if (id == R.id.start_service_button) {
            if (mTracer == null) {
                mTracer = mTrackX.tracer(TerminalOptions.newBuilder("your service id", "your entity name")
                        .entityId("your entity id")
                        .build());
            }
            mTracer.startService(createForegroundNotification());
        } else if (id == R.id.end_service_button) {
            if (mTracer == null) {
                mTracer = mTrackX.tracer(TerminalOptions.newBuilder("your service id", "your entity name")
                        .entityId("your entity id")
                        .build());
            }
            mTracer.stopService();
//            mTracer.removeTraceEventCallback(traceEventCallback);
        } else if (id == R.id.采集频率) {
            if (mTraceIntervalEnable) {
                traceConfig = TraceConfig.newBuilder(traceConfig).traceInterval(3).build();
                mTracer.setConfig(traceConfig);
                Toast.makeText(TrackxUploadActivity.this, "采集频率设置为3秒", Toast.LENGTH_SHORT).show();
            } else {
                traceConfig = TraceConfig.newBuilder(traceConfig).traceInterval(1).build();
                mTracer.setConfig(traceConfig);
                Toast.makeText(TrackxUploadActivity.this, "采集频率设置为1秒", Toast.LENGTH_SHORT).show();
            }
            mTraceIntervalEnable = !mTraceIntervalEnable;
        } else if (id == R.id.上报频率) {
            if (packIntervalEnable) {
                traceConfig = TraceConfig.newBuilder(traceConfig).packInterval(60).build();
                mTracer.setConfig(traceConfig);
                Toast.makeText(TrackxUploadActivity.this, "上报频率设置为60秒", Toast.LENGTH_SHORT).show();
            } else {
                traceConfig = TraceConfig.newBuilder(traceConfig).packInterval(5).build();
                mTracer.setConfig(traceConfig);
                Toast.makeText(TrackxUploadActivity.this, "上报频率设置为5秒", Toast.LENGTH_SHORT).show();
            }
            packIntervalEnable = !packIntervalEnable;
        } else if (id == R.id.最大缓存条数) {
            if (mMaxCacheCountEnable) {
                traceConfig = TraceConfig.newBuilder(traceConfig).maxCacheCount(1000).build();
                mTracer.setConfig(traceConfig);
                Toast.makeText(TrackxUploadActivity.this, "最大缓存条数设置为1000条", Toast.LENGTH_SHORT).show();
            } else {
                traceConfig = TraceConfig.newBuilder(traceConfig).packInterval(36000).build();
                mTracer.setConfig(traceConfig);
                Toast.makeText(TrackxUploadActivity.this, "最大缓存条数设置为36000条", Toast.LENGTH_SHORT).show();
            }
            mMaxCacheCountEnable = !mMaxCacheCountEnable;
        } else if (id == R.id.定位类型) {
            if (mProviderTypeEnable > 2) {
                mProviderTypeEnable = 0;
            }
            if (mProviderTypeEnable == 0) {
                traceConfig = TraceConfig.newBuilder(traceConfig).tracePointProvider(TracePointSource.Provider.NETWORK).build();
                mTracer.setConfig(traceConfig);
                Toast.makeText(TrackxUploadActivity.this, "设置定位模式为：NETWORK", Toast.LENGTH_SHORT).show();
            } else if (mProviderTypeEnable == 1){
                traceConfig = TraceConfig.newBuilder(traceConfig).tracePointProvider(TracePointSource.Provider.GPS).build();
                mTracer.setConfig(traceConfig);
                Toast.makeText(TrackxUploadActivity.this, "设置定位模式为：GPS", Toast.LENGTH_SHORT).show();
            } else if (mProviderTypeEnable == 2) {
                traceConfig = TraceConfig.newBuilder(traceConfig).tracePointProvider(TracePointSource.Provider.FUSE).build();
                mTracer.setConfig(traceConfig);
                Toast.makeText(TrackxUploadActivity.this, "设置定位模式为：FUSE(高精)", Toast.LENGTH_SHORT).show();
            }
            mProviderTypeEnable++;
        }
    }

    private final TraceEventCallback traceEventCallback = new TraceEventCallback() {
        @Override
        public void onEntityVerified(EntityVerifiedResult entityVerifiedResult) {
            long code = entityVerifiedResult.getStatus();
            String message = entityVerifiedResult.getMessage();
            String suid = entityVerifiedResult.getSuid();
            EntityVerifiedResult.EntityCreateResult wsInfo = entityVerifiedResult.getEntityCreateResult();
            Log.i(TAG, "onEntityVerified, code: " + code + ", message: " + message + ", suid: " + suid);
            Log.i(TAG, "onEntityVerified, wsInfo: " + wsInfo);
            if (code == 0) {
                Toast.makeText(TrackxUploadActivity.this, "设备验证成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTraceDidStart() {
            Log.i(TAG, "onTraceDidStart");
        }

        @Override
        public void onTraceDidStop() {
            Log.i(TAG, "onTraceDidStop");
        }

        @Override
        public void onTracePointPreCache(CacheTracePoint cacheTracePoint) {
            cacheTracePoint.addExtraParam("test1", "1")
                    .addExtraParam("test2", "2")
                    .addExtraParam("time", String.valueOf(System.currentTimeMillis()));
//            Log.i(TAG, "onTracePointPreCache: " + cacheTracePoint.getExtraParams().toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mLatestPointText.setText(((CacheTracePointImpl) cacheTracePoint).toString());
                }
            });
        }

        @Override
        public void onTracePointRefluxed(RefluxResult refluxResult) {
            Log.i(TAG, "onTracePointRefluxed: " + "code: " + refluxResult.getCode()
                    + ", message: " + refluxResult.getMessage());
            Log.i(TAG, "onTracePointRefluxed: " + "current: " + refluxResult.getCurrent().toString()
                    + ", remaining: " + refluxResult.getRemaining().toString());
            mUploadResultText.setText(refluxResult.toString());
        }

        @Override
        public void onTracePointEvicted(EvictedInfo evictedInfo) {
            Log.i(TAG, "onTracePointEvicted: " + evictedInfo.toString());
        }
    };

    /**
     * 创建服务通知
     */
    private Notification createForegroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), "tracer_service_channel");
        //通知小图标
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        //通知标题
        builder.setContentTitle("轨迹Title");
        //通知内容
        builder.setContentText("轨迹Content");
        //设置通知显示的时间
        builder.setWhen(System.currentTimeMillis());
        //设定启动的内容
        Intent activityIntent = new Intent(this, TrackxUploadActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        //设置通知优先级
        builder.setPriority(Notification.PRIORITY_HIGH);
        //设置为进行中的通知
        builder.setOngoing(true);
        //创建通知并返回
        return builder.build();
    }
}