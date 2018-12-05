package com.example.xu.mmitest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ALL_PERMISSION = 1;
    private FeatureSupport mFeatureSupport;
    private boolean isStartTest = false;
    Object obj = new Object();
    private KeyView mKeyView;
    private Gsensor mGsnesor;
    private GPS mGps;
    private Wifi mWifi;
    private Bluetooth mBluetooth;
    private Vibrator mVibrator;
    private Mic mMic;
    private FlashLight mFlashLight;
    private LightSensor mLightsensor;
    private LED mLed;
    private LcdBrightness mLcd;
    private Receiver mReceiver;
    private Speaker mSpeaker;
    private Headset mHeadset;
    private ProximitySensor mProximitySensor;
    private FM mFm;
    private CFT mCft;

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
        mLightsensor = new LightSensor(this);
        mLed = new LED(this);
        mLcd = new LcdBrightness(this);
        mReceiver = new Receiver(this);
        mSpeaker = new Speaker(this);
        mHeadset = new Headset(this);
        mProximitySensor = new ProximitySensor(this);
        mFm = new FM(this);
        mCft = new CFT(this);
        requestAllPermission();
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
            doTestAll();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    private void doTestAll(){

        synchronized (obj) {
            new Runnable() {
                @Override
                public void run() {
                    if (isStartTest) {return;}
/*
                    if (mFeatureSupport.isSupportGsensor()) {
                        mGsnesor.startGsensor();
                    } else {
                        mGsnesor.inVisible();
                    }
                    if (mFeatureSupport.isSupportGps()) {
                        mGps.startGps();
                    } else {
                        mGps.inVisible();
                    }
                    if (!mFeatureSupport.isSupportMainMic()) {
                        mMic.inVisible();
                    }
                    if (mFeatureSupport.isSupportBluetooth()) {
                        mBluetooth.startBluetooth();
                    } else {
                        mBluetooth.inVisible();
                    }

                    if (mFeatureSupport.isSupportVibrator()) {
                        mVibrator.startVibrator();
                    } else {
                        mVibrator.inVisible();
                    }

                    if (mFeatureSupport.isSupportBackFlash()) {
                        Log.i("MYTEST", "BackfFlash Support");
                        mFlashLight.startBackFlashLight();
                    } else {
                        Log.i("MYTEST", "BackfFlash not Support");
                        mFlashLight.inVisibleBackFlashLight();
                    }

                    if (mFeatureSupport.isSupportFrontFlash()) {
                        mFlashLight.startFrontFlahgLight();
                    } else {
                        mFlashLight.inVisibleFrontFlashLight();
                    }


                    if (mFeatureSupport.isSupportLightSensor()) {
                        mLightsensor.startLightSensor();
                    } else {
                        mLightsensor.inVisible();
                    }

                    if (mFeatureSupport.isSupportLed()){
                        mLed.startLed();
                    }else{
                        mLed.inVisible();
                    }

                    if (mFeatureSupport.isSupportReceiver()) {
                        mReceiver.startReceiver();
                    }else{
                        mReceiver.inVisible();
                    }

                    if (mFeatureSupport.isSupportHeadset()){
                        mHeadset.startHeadset();
                    }else{
                        mHeadset.inVisible();
                    }

                    if (mFeatureSupport.isSupportProximitySensor()){
                        mProximitySensor.startProxumitySensor();
                    }else{
                        mProximitySensor.inVisible();
                    }
                    mWifi.startWifi();
                    mLcd.statLcdBrightness();
                    mSpeaker.startSpeaker();
*/
                    mFm.startFm();
                    mCft.startCft();
                }
            }.run();
        }

    }

    private void doStopAll(){
        synchronized (obj) {
            if (!isStartTest) {return;}
            isStartTest = false;
/*
            if (mFeatureSupport.isSupportGsensor()) {
                mGsnesor.stopGsensor();
            }
            if (mFeatureSupport.isSupportGps()) {
                mGps.stopGps();
            }
            if (mFeatureSupport.isSupportMainMic()) {
                mMic.stopMic();
            }
            if (mFeatureSupport.isSupportBluetooth()) {
                mBluetooth.stopBluetooth();
            }

            if (mFeatureSupport.isSupportVibrator()) {
                mVibrator.stopVibrator();
            }

            if (mFeatureSupport.isSupportBackFlash()) {
                mFlashLight.stopBackFlashLight();
            }

            if (mFeatureSupport.isSupportFrontFlash()) {
                mFlashLight.stopFrontFlashLight();
            }

            if (mFeatureSupport.isSupportLightSensor()) {
                mLightsensor.stopLightSensor();
            }

            if (mFeatureSupport.isSupportLed()){
                mLed.stopLed();
            }

            if (mFeatureSupport.isSupportReceiver()) {
                mReceiver.stopReceiver();
            }

            if (mFeatureSupport.isSupportHeadset()){
                mHeadset.stopHeadset();
            }

            if(mFeatureSupport.isSupportProximitySensor()){
                mProximitySensor.stopProxumitySensor();
            }
            mWifi.stopWifi();
            mLcd.stopLcdBrightness();
            mSpeaker.stopSpeaker();
*/
            mFm.stopFm();
            mCft.stopCft();
        }
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

    public Handler myHandler = new Handler();

}
