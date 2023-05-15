package com.tencent.trackx.demo.activity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.gaya.foundation.api.comps.service.net.NetRequest;
import com.tencent.trackx.api.TrackerX;
import com.tencent.trackx.api.model.QueryOptions;
import com.tencent.trackx.api.model.QueryResult;
import com.tencent.trackx.api.query.Querier;
import com.tencent.trackx.api.query.QueryCallback;
import com.tencent.trackx.demo.R;
import com.tencent.trackx.demo.SingletonHelper;

public class TrackxQueryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TrackxQuery";

    private Querier mQuerier;
    private TextView mQueryResultText;
    private EditText mStartTimeText;
    private EditText mEndTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackx_query);
        TrackerX mTrackX = SingletonHelper.getInstance(getApplicationContext());
        mQuerier = mTrackX.querier();

        findViewById(R.id.query_trace_button).setOnClickListener(this);
        mQueryResultText = findViewById(R.id.query_result_text);
        mQueryResultText.setMovementMethod(ScrollingMovementMethod.getInstance());
        mStartTimeText = findViewById(R.id.start_time_text);
        mEndTimeText = findViewById(R.id.end_time_text);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.query_trace_button) {
            String startTime = mStartTimeText.getText().toString();
            String endTime = mEndTimeText.getText().toString();
            Log.i(TAG, "开始时间戳：" + startTime);
            Log.i(TAG, "结束时间戳：" + endTime);
            long startTimeL = startTime.length() == 10 ? Long.parseLong(startTime) : (System.currentTimeMillis() - 3600 * 24000L) / 1000L;
            long endTimeL = endTime.length() == 10 ? Long.parseLong(endTime) : System.currentTimeMillis() / 1000L;
            NetRequest request = mQuerier.queryTrace(
                    QueryOptions.newBuilder("your service id",
                                    "your entity id",
                                    startTimeL,
                                    endTimeL)
                            .build(), new QueryCallback() {
                        @Override
                        public void onQueryResult(QueryResult result) {
                            Log.i(TAG, "onQueryResult: " + result.toString());
                            if (result.getQueryInfo().getResult() != null) {
                                mQueryResultText.setText(result.getQueryInfo().getResult().toString());
                            }
                        }
                    });
            // 测试取消查询功能
//            if (request != null) {
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        boolean result = request.cancel();
//                        Log.i(TAG, "cancel query result: " + result);
//                    }
//                }, 50);
//            }
        }
    }
}