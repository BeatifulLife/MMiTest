package com.example.xu.mmitest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BackFlashLight implements View.OnClickListener,Item {

    private MainActivity mActivity;
    private Resources mResource;
    private TextView mBackTextView;
    private Button backpassBtn;
    private Button backfailBtn;
    private LinearLayout mBackLinearlayout;
    private static boolean isbackflashlightTest = false;

    public BackFlashLight(MainActivity activity) {
        this.mActivity = activity;
        mResource = activity.getResources();
        mBackTextView = mActivity.findViewById(R.id.backflashlighttips);
        mBackTextView.setTextColor(Color.YELLOW);
        this.mBackLinearlayout = mActivity.findViewById(R.id.backflashlightbtnline);
        this.backfailBtn = mActivity.findViewById(R.id.backflashlightfailbtn);
        this.backpassBtn = mActivity.findViewById(R.id.backflashlightpassbtn);
        this.backfailBtn.setOnClickListener(this);
        this.backpassBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.backflashlightfailbtn:
            case R.id.backflashlightpassbtn:
                if (v.getId()==R.id.backflashlightfailbtn){
                    mBackTextView.setTextColor(Color.RED);
                    mBackTextView.setText(mResource.getString(R.string.testfail));
                }else if (v.getId()==R.id.backflashlightpassbtn){
                    mBackTextView.setTextColor(Color.GREEN);
                    mBackTextView.setText(mResource.getString(R.string.testsuccess));
                }
                isbackflashlightTest = true;
                mBackLinearlayout.setVisibility(View.INVISIBLE);
                break;
        }

    }

    @Override
    public void startItem() {

    }

    @Override
    public void stopItem() {

    }

    @Override
    public void inVisible() {
        mActivity.findViewById(R.id.backflashlightitem).setVisibility(View.GONE);
    }
}
