package com.tencent.trackx.demo.activity;

import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static android.provider.Settings.EXTRA_CHANNEL_ID;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.trackx.api.InitCallback;
import com.tencent.trackx.api.InitConfig;
import com.tencent.trackx.api.InitResult;
import com.tencent.trackx.api.TrackerX;
import com.tencent.trackx.api.model.NetworkStatus;
import com.tencent.trackx.demo.R;
import com.tencent.trackx.demo.SingletonHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private final List<Item> demoItems = new ArrayList<>();
    private int reqCount = 0;

    private TrackerX mTrackX;

    private boolean hasTagInList(String desc) {
        for (Item item : demoItems) {
            if (desc.equals(item.desc)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reqCount = 0;

        ActionBar actionBar = super.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        PackageManager pm = getPackageManager();
        try {
            ActivityInfo[] activityInfos = pm
                    .getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES).activities;

            for (int i = 0; i < activityInfos.length; i++) {
                ActivityInfo activityInfo = activityInfos[i];
                if (activityInfo.descriptionRes != 0) {

                    String desc = getString(activityInfo.descriptionRes);
                    if (!hasTagInList(desc)) {
                        Item item = new Item();
                        item.desc = desc;
                        item.isTag = true;
                        demoItems.add(item);
                    }
                    Item item = new Item();
                    item.clazz = activityInfo.name;
                    item.desc = desc;
                    item.isTag = false;
                    item.name = activityInfo.loadLabel(pm).toString();
                    demoItems.add(item);
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        initRv();

        mTrackX = SingletonHelper.getInstance(getApplicationContext());
        mTrackX.init(InitConfig.newBuilder()
                .appKey("your develop key")
                .deviceId("your device id")
                .secretKey("your develop secret key")
                .debuggable(true)
                .build(), new InitCallback() {
            @Override
            public void onInitResult(InitResult result) {
                long code = result.getCode();
                String message = result.getMessage();
                Log.i(TAG, "onInitResult, code: " + code + ", message: " + message + ", suid: " + result.getSuid());
                if (code == NetworkStatus.OK.getCode()) {
                    Toast.makeText(MainActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
        boolean isOpened = manager.areNotificationsEnabled();
        Log.i(TAG, "is notify enable: " + isOpened);
        reqCount++;
        if (!isOpened && reqCount <= 2 ) {
            try {
                // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                    intent.putExtra(EXTRA_APP_PACKAGE, getPackageName());
                    intent.putExtra(EXTRA_CHANNEL_ID, getApplicationInfo().uid);
                } else {
                    //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);
                }
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
                Intent intent = new Intent();
                //下面这种方案是直接跳转到当前应用的设置界面。
                //https://blog.csdn.net/ysy950803/article/details/71910806
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private void initRv() {
        RecyclerView lsRv = findViewById(R.id.ls_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        lsRv.setLayoutManager(manager);
        lsRv.setAdapter(new RvAdapter());
    }

    private static class Item {

        String name;
        String desc;
        String clazz;
        boolean isTag;
    }

    public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {

        public static final String LOG_TAG = "navi";

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ls_layout_recy_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Item item = demoItems.get(position);
            if (item.isTag) {
                holder.tvContent.setVisibility(View.GONE);
                holder.tvDesc.setText("> " + item.desc);
                holder.allLayout.setClickable(false);
            } else {
                holder.tvContent.setVisibility(View.VISIBLE);
                holder.tvContent.setText(item.name);
                holder.tvDesc.setText(item.clazz);
                holder.listener.position = position;
                holder.listener.setViewHolder(holder);
                holder.allLayout.setClickable(true);
                holder.allLayout.setOnClickListener(holder.listener);
            }
        }

        @Override
        public int getItemCount() {
            return demoItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            MyClickListener listener = new MyClickListener();

            LinearLayout allLayout;
            TextView tvContent;
            TextView tvDesc;

            public ViewHolder(View view) {
                super(view);
                tvContent = view.findViewById(R.id.tv_recycler_item_content);
                tvDesc = view.findViewById(R.id.tv_recycler_item_description);
                allLayout = view.findViewById(R.id.all_layout);
            }
        }

        class MyClickListener implements View.OnClickListener {

            public WeakReference<ViewHolder> wrf;
            public int position;

            public void setViewHolder(ViewHolder viewHolder) {
                wrf = new WeakReference<>(viewHolder);
            }

            @Override
            public void onClick(View v) {
                if (wrf == null || wrf.get() == null) {
                    Log.e(LOG_TAG, "view holder or wrf null");
                    return;
                }

                String strGotName = demoItems.get(position).clazz;
                try {
                    @SuppressWarnings("rawtypes")
                    Class clazz = Class.forName(strGotName);
                    Intent intent = new Intent(MainActivity.this, clazz);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}