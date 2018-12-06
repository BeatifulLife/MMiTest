package com.example.xu.mmitest;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Vibrator implements View.OnClickListener,Item {

    private Activity mActivity;
    private TextView mTextView;
    private android.os.Vibrator mVibrator;
    private long vibtime[] = new long[]{1000,3000};
    private ScheduledExecutorService schedule;
    private boolean isStartVib = false;
    Object obj = new Object();
    private Resources mResource;
    private Button passBtn;
    private Button failBtn;
    private LinearLayout mLinearLayout;
    private static boolean isVibTest = false;
    public Vibrator(Activity activity){
        mActivity = activity;
        mTextView = activity.findViewById(R.id.vibratortips);
        mResource = activity.getResources();
        passBtn = activity.findViewById(R.id.vibpassbtn);
        failBtn = activity.findViewById(R.id.vibfailbtn);
        mLinearLayout = activity.findViewById(R.id.vibbtnline);
        passBtn.setOnClickListener(this);
        failBtn.setOnClickListener(this);
    }

    private void init(){
        mVibrator = (android.os.Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("MYTEST","Vibrator Here0");
            VibrationEffect vibrationEffect = VibrationEffect.createWaveform(vibtime, 0);
            mVibrator.vibrate(vibrationEffect);
        }else{
            Log.i("MYTEST","Vibrator Here1");
            mVibrator.vibrate(vibtime,0);
        }
        schedule = new ScheduledThreadPoolExecutor(1);
        schedule.schedule(new Runnable() {
            @Override
            public void run() {
                stopVibrator();
            }
        },10000,TimeUnit.MILLISECONDS);
        isStartVib = true;
        mTextView.setTextColor(Color.YELLOW);
        mTextView.setText(mResource.getString(R.string.vibratortips));
    }

    public void startVibrator(){
        synchronized (obj) {
            if (!isStartVib) {
                if (isVibTest) {return;}
                init();
            }
        }
    }

    public void stopVibrator(){

        synchronized (obj) {
            if (schedule!=null){
                schedule.shutdown();
                schedule = null;
            }
            if (isStartVib) {
                isStartVib = false;
                mVibrator.cancel();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.vibpassbtn){
            mTextView.setTextColor(Color.GREEN);
            mTextView.setText(mResource.getString(R.string.testsuccess));
        }else if (v.getId()==R.id.vibfailbtn){
            mTextView.setTextColor(Color.RED);
            mTextView.setText(mResource.getString(R.string.testfail));
        }
        synchronized (obj) {
            isVibTest = true;
        }
        mLinearLayout.setVisibility(View.INVISIBLE);
        stopVibrator();
    }

    @Override
    public void inVisible(){
        mActivity.findViewById(R.id.vibratoritem).setVisibility(View.GONE);
    }

    @Override
    public void startItem() {
        startVibrator();
    }

    @Override
    public void stopItem() {
        stopVibrator();
    }
}
