package com.example.xu.mmitest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.view.View;
import android.widget.TextView;

public class Headset implements Item{
    private TextView mTextView;
    private Activity mActivity;
    private static boolean isHeadsetTest = false;
    private boolean isReges = false;
    private Object obj = new Object();
    public Headset(Activity activity){
        mActivity = activity;
        mTextView = activity.findViewById(R.id.headsettips);
    }

    @Override
    public void inVisible(){
        mActivity.findViewById(R.id.headsetitem).setVisibility(View.GONE);
    }

    public void startHeadset() {
        synchronized (obj){
            if (isHeadsetTest) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
            intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            mActivity.registerReceiver(registerReceiver, intentFilter);
            isReges = true;
        }
    }

    public void stopHeadset(){
        synchronized (obj) {
            if (isReges) {
                isReges = false;
                mActivity.unregisterReceiver(registerReceiver);
            }
        }
    }

    private BroadcastReceiver registerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())){
                mTextView.setTextColor(Color.RED);
                mTextView.setText(R.string.headsetnoplus);
            }else if (AudioManager.ACTION_HEADSET_PLUG.equals(intent.getAction())){
                mTextView.setTextColor(Color.GREEN);
                mTextView.setText(R.string.headsetplus);
                isHeadsetTest = true;
                stopHeadset();
            }
        }
    };

    @Override
    public void startItem() {
        startHeadset();
    }

    @Override
    public void stopItem() {
        stopHeadset();
    }
}
