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

public class FrontFlashLight implements View.OnClickListener,Item {

    private MainActivity mActivity;
    private Resources mResource;

    private TextView mFrontTextView;
    private Button frontpassBtn;
    private Button frontfailBtn;
    private LinearLayout mFrontLinearlayout;
    private static boolean isfrontflashlightTest = false;


    public FrontFlashLight(MainActivity activity) {
        this.mActivity = activity;
        mResource = activity.getResources();

        mFrontTextView = mActivity.findViewById(R.id.frontflashlighttips);
        mFrontTextView.setTextColor(Color.YELLOW);
        this.mFrontLinearlayout = mActivity.findViewById(R.id.frontflashlightbtnline);
        this.frontfailBtn = mActivity.findViewById(R.id.frontflashlightfailbtn);
        this.frontpassBtn = mActivity.findViewById(R.id.frontflashlightpassbtn);
        this.frontfailBtn.setOnClickListener(this);
        this.frontpassBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.frontflashlightfailbtn:
            case R.id.frontflashlightpassbtn:
                if (v.getId()==R.id.backflashlightfailbtn){
                    mFrontTextView.setTextColor(Color.RED);
                    mFrontTextView.setText(mResource.getString(R.string.testfail));
                }else if (v.getId()==R.id.backflashlightpassbtn){
                    mFrontTextView.setTextColor(Color.GREEN);
                    mFrontTextView.setText(mResource.getString(R.string.testsuccess));
                }
                isfrontflashlightTest = true;
                mFrontLinearlayout.setVisibility(View.INVISIBLE);
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
        mActivity.findViewById(R.id.frontflashlightitem).setVisibility(View.GONE);
    }
}
