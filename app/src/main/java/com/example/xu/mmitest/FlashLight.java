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

public class FlashLight implements View.OnClickListener {

    private MainActivity mActivity;
    private Resources mResource;
    private static TextView mBackTextView;
    private Button backpassBtn;
    private Button backfailBtn;
    private LinearLayout mBackLinearlayout;

    private TextView mFrontTextView;
    private Button frontpassBtn;
    private Button frontfailBtn;
    private LinearLayout mFrontLinearlayout;
    private CameraManager mCameramanager;
    private static boolean isbackflashlightTest = false;
    private static boolean isfrontflashlightTest = false;
    private ScheduledExecutorService backScheduled;
    private ScheduledExecutorService frontScheduled;

    public FlashLight(MainActivity activity) {
        this.mActivity = activity;
        mResource = activity.getResources();
        mBackTextView = mActivity.findViewById(R.id.backflashlighttips);
        mBackTextView.setTextColor(Color.YELLOW);
        this.mBackLinearlayout = mActivity.findViewById(R.id.backflashlightbtnline);
        this.backfailBtn = mActivity.findViewById(R.id.backflashlightfailbtn);
        this.backpassBtn = mActivity.findViewById(R.id.backflashlightpassbtn);
        this.backfailBtn.setOnClickListener(this);
        this.backpassBtn.setOnClickListener(this);

        mFrontTextView = mActivity.findViewById(R.id.frontflashlighttips);
        mFrontTextView.setTextColor(Color.YELLOW);
        this.mFrontLinearlayout = mActivity.findViewById(R.id.frontflashlightbtnline);
        this.frontfailBtn = mActivity.findViewById(R.id.frontflashlightfailbtn);
        this.frontpassBtn = mActivity.findViewById(R.id.frontflashlightpassbtn);
        this.frontfailBtn.setOnClickListener(this);
        this.frontpassBtn.setOnClickListener(this);
        mCameramanager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
    }

    public void startBackFlashLight(){
        if (isbackflashlightTest){return;}
        backScheduled = new ScheduledThreadPoolExecutor(1);
        backScheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    mCameramanager.setTorchMode(mActivity.getmFeatureSupport().getBackCamereId(),true);
                    SystemClock.sleep(100);
                    mCameramanager.setTorchMode(mActivity.getmFeatureSupport().getBackCamereId(),false);
                    SystemClock.sleep(100);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

            }
        },500,200,TimeUnit.MILLISECONDS);
    }

    public void stopBackFlashLight(){
        if (backScheduled!=null){
            backScheduled.shutdown();
        }
    }

    public void startFrontFlahgLight(){
        if (isfrontflashlightTest){return;}
        frontScheduled = new ScheduledThreadPoolExecutor(1);
        frontScheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    mCameramanager.setTorchMode(mActivity.getmFeatureSupport().getFrontCameraId(),true);
                    SystemClock.sleep(100);
                    mCameramanager.setTorchMode(mActivity.getmFeatureSupport().getFrontCameraId(),false);
                    SystemClock.sleep(100);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        },0,200,TimeUnit.MILLISECONDS);
    }

    public void stopFrontFlashLight(){
        if (frontScheduled!=null){
            frontScheduled.shutdown();
        }
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
                stopBackFlashLight();
                break;
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
                stopFrontFlashLight();
                break;
        }

    }

    public void inVisibleBackFlashLight(){
        mActivity.findViewById(R.id.backflashlightitem).setVisibility(View.GONE);
    }

    public void inVisibleFrontFlashLight(){
        mActivity.findViewById(R.id.frontflashlightitem).setVisibility(View.GONE);
    }
}
