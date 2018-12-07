package com.example.xu.mmitest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Gsensor implements SensorEventListener,Item {
    private SensorManager mSensorManager;
    private Sensor mGsensor;
    private TextView mTextView;
    private Activity mActivity;
    private String tips = new String();
    private boolean isReges = false;
    private Object obj = new Object();
    private ScheduledExecutorService scheduledExecutorService;

    public  Gsensor(Activity activity){
        mActivity = activity;
        mTextView = activity.findViewById(R.id.gsensortips);
    }

    private void init(){
        mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        mGsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mGsensor!=null) {
            mSensorManager.registerListener(this, mGsensor, SensorManager.SENSOR_DELAY_NORMAL);
            isReges = true;
            mTextView.setTextColor(Color.GREEN);
        }else{
            mTextView.setTextColor(Color.RED);
            mTextView.setText(R.string.nogsensor);
        }
    }

    public void startGsensor(){
        synchronized (obj) {
            init();
        }
    }

    public void stopGsensor(){
        synchronized (obj) {
            if (mGsensor != null && isReges) {
                isReges = false;
                mSensorManager.unregisterListener(this);
            }
        }
    }

    @Override
    public void inVisible(){
        mActivity.findViewById(R.id.gsensoritem).setVisibility(View.GONE);
        mActivity.findViewById(R.id.gsensorline).setVisibility(View.GONE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        tips = String.format("  %f  %f  %f",event.values[0],event.values[1],event.values[2]);
        mTextView.setText(tips);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void startItem() {
        scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                startGsensor();
            }
        },2000,TimeUnit.MILLISECONDS);

    }

    @Override
    public void stopItem() {
        if (scheduledExecutorService!= null){
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
        stopGsensor();
    }
}
