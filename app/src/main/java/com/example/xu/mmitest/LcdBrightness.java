package com.example.xu.mmitest;

import android.app.Activity;
import android.graphics.Color;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LcdBrightness implements View.OnClickListener,Item {
    private Activity mActivity;
    private ScheduledExecutorService mSchedule;
    private boolean isHight = true;
    private int orgBrightness;
    private final int lowbrightness = 20;
    private final int hightbrightness = 255;
    private TextView mTextView;
    private static boolean isLcdTest = false;
    private Object obj = new Object();

    public LcdBrightness(Activity activity){
        mActivity = activity;
        mTextView = activity.findViewById(R.id.lcdtips);
        mTextView.setTextColor(Color.YELLOW);
        activity.findViewById(R.id.lcdfailbtn).setOnClickListener(this);
        activity.findViewById(R.id.lcdpassbtn).setOnClickListener(this);
    }

    private void setBrightness(int brightness){
        Settings.System.putInt(mActivity.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,brightness);
    }

    private int getBrightness(){
        try {
            return Settings.System.getInt(mActivity.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return 125;
    }

    public void statLcdBrightness(){
        synchronized (obj) {
            if (isLcdTest) {
                return;
            }
            orgBrightness = getBrightness();
            mSchedule = new ScheduledThreadPoolExecutor(1);
            mSchedule.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (isHight) {
                        setBrightness(lowbrightness);
                    } else {
                        setBrightness(hightbrightness);
                    }
                    isHight = !isHight;
                }
            }, 500, 1000, TimeUnit.MILLISECONDS);
        }
    }

    public void stopLcdBrightness(){
        synchronized (obj) {
            if (mSchedule != null) {
                mSchedule.shutdown();
                mSchedule = null;
                setBrightness(orgBrightness);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.lcdfailbtn){
            mTextView.setTextColor(Color.RED);
            mTextView.setText(R.string.testfail);
        }else if (v.getId()==R.id.lcdpassbtn){
            mTextView.setTextColor(Color.GREEN);
            mTextView.setText(R.string.testsuccess);
        }
        isLcdTest = true;
        mActivity.findViewById(R.id.lcdbtnline).setVisibility(View.INVISIBLE);
        stopLcdBrightness();
    }

    @Override
    public void startItem() {
        statLcdBrightness();
    }

    @Override
    public void stopItem() {
        stopLcdBrightness();
    }

    @Override
    public void inVisible() {
        mActivity.findViewById(R.id.lcditem).setVisibility(View.GONE);
        mActivity.findViewById(R.id.lcdline).setVisibility(View.GONE);
    }
}
