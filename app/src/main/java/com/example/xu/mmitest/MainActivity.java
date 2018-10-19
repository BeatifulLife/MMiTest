package com.example.xu.mmitest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ALL_PERMISSION = 1;
    private FeatureSupport mFeatureSupport;
    private KeyView mKeyView;
    private Gsensor mGsnesor;
    private GPS mGps;
    private Wifi mWifi;
    private Bluetooth mBluetooth;
    private Vibrator mVibrator;
    private Mic mMic;
    private FlashLight mFlashLight;
    private boolean isStartTest = false;
    Object obj = new Object();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFeatureSupport = new FeatureSupport(this);
        mKeyView = findViewById(R.id.keylayout);
        mMic = new Mic(this);
        mGsnesor = new Gsensor(this);
        mGps = new GPS(this);
        mWifi = new Wifi(this);
        mBluetooth = new Bluetooth(this);
        mVibrator = new Vibrator(this);
        mFlashLight = new FlashLight(this);
        requestAllPermission();

        //new FeatureSupport(this).isSupportMainMic();
    }


    private void requestAllPermission() {
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BODY_SENSORS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CAMERA}, REQUEST_ALL_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ALL_PERMISSION) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        doTestAll();
    }


    private void doTestAll(){


        new Runnable(){
            @Override
            public void run() {

                if(mFeatureSupport.isSupportGsensor()) {
                    mGsnesor.startGsensor();
                }else{
                    mGsnesor.Invisible();
                }
                if (mFeatureSupport.isSupportGps()) {
                    mGps.startGps();
                }else{
                    mGps.InVisible();
                }
                if (!mFeatureSupport.isSupportMainMic()){
                    mMic.Invisible();
                }
                if (mFeatureSupport.isSupportBluetooth()){
                    mBluetooth.startBluetooth();
                }else{
                    mBluetooth.inVisible();
                }

                if (mFeatureSupport.isSupportVibrator()){
                    mVibrator.startVibrator();
                }else{
                    mVibrator.Invisible();
                }

                if (mFeatureSupport.isSupportBackFlash()){
                    Log.i("MYTEST","BackfFlash Support");
                    mFlashLight.startBackFlashLight();
                }else{
                    Log.i("MYTEST","BackfFlash not Support");
                    mFlashLight.InvisibleBackFlashLight();
                }

                if (mFeatureSupport.isSupportFrontFlash()){
                    mFlashLight.startFrontFlahgLight();
                }else{
                    mFlashLight.InvisibleFrontFlashLight();
                }
                mWifi.startWifi();



            }
        }.run();

    }

    private void doStopAll(){
        isStartTest = false;
        if(mFeatureSupport.isSupportGsensor()) {
            mGsnesor.stopGsensor();
        }
        if (mFeatureSupport.isSupportGps()) {
            mGps.stopGps();
        }
        if (mFeatureSupport.isSupportMainMic()){
            mMic.stopMic();
        }
        if (mFeatureSupport.isSupportBluetooth()){
            mBluetooth.stopBluetooth();
        }

        if (mFeatureSupport.isSupportVibrator()){
            mVibrator.stopVibrator();
        }

        if (mFeatureSupport.isSupportBackFlash()){
            mFlashLight.stopBackFlashLight();
        }

        if (mFeatureSupport.isSupportFrontFlash()){
            mFlashLight.stopFrontFlashLight();
        }
        mWifi.stopWifi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        doStopAll();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!mKeyView.isKeyTestPass() && mKeyView.KeyViewDispatchKeyEvent(event)) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public FeatureSupport getmFeatureSupport(){
        return mFeatureSupport;
    }

}
