package com.example.xu.mmitest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;


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
    private BackFlashLight mBackFlash;
    private FrontFlashLight mFrontFlash;
    private LightSensor mLightsensor;
    private LED mLed;
    private LcdBrightness mLcd;
    private Receiver mReceiver;
    private Speaker mSpeaker;
    private Headset mHeadset;
    private ProximitySensor mProximitySensor;
    private FM mFm;
    private CFT mCft;
    private Camera mCamera;
    private Sdcard mSdcard;
    private OTG mOtg;
    private SIM mSim;
    private static boolean isInit = false;

    private ArrayList<Item> itemList = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        initItem();
        //requestAllPermission();
    }

    private void init(){
        mFeatureSupport = new FeatureSupport(this);
        mKeyView = findViewById(R.id.keylayout);
        mMic = new Mic(this);
        mGsnesor = new Gsensor(this);
        mGps = new GPS(this);
        mWifi = new Wifi(this);
        mBluetooth = new Bluetooth(this);
        mVibrator = new Vibrator(this);
        mBackFlash = new BackFlashLight(this);
        mFrontFlash = new FrontFlashLight(this);
        mLightsensor = new LightSensor(this);
        mLed = new LED(this);
        mLcd = new LcdBrightness(this);
        mReceiver = new Receiver(this);
        mSpeaker = new Speaker(this);
        mHeadset = new Headset(this);
        mProximitySensor = new ProximitySensor(this);
        mFm = new FM(this);
        mCft = new CFT(this);
        mCamera = new Camera(this,mFeatureSupport.isSupportBackCamera(),mFeatureSupport.isSupportFrontCamera());
        mSdcard = new Sdcard(this);
        mOtg = new OTG(this);
        mSim = new SIM(this);
        isInit = true;
    }

    private void initItem(){

        if (mFeatureSupport== null || !isInit){
            init();
        }
        if (mFeatureSupport.isSupportKey()){
            itemList.add(mKeyView);
        }else{
            mKeyView.inVisible();
        }

        if (mFeatureSupport.isSupportMainMic()){
            itemList.add(mMic);
        }else {
            mMic.inVisible();
        }

        if (mFeatureSupport.isSupportGsensor()){
            itemList.add(mGsnesor);
        }else {
            mGsnesor.inVisible();
        }

        if (mFeatureSupport.isSupportGps()){
            itemList.add(mGps);
        }else{
            mGps.inVisible();
        }

        if (mFeatureSupport.isSupportWifi()){
            itemList.add(mWifi);
        }else{
            mWifi.inVisible();
        }

        if (mFeatureSupport.isSupportBluetooth()){
            itemList.add(mBluetooth);
        }else {
            mBluetooth.inVisible();
        }

        if (mFeatureSupport.isSupportVibrator()){
            itemList.add(mVibrator);
        }else {
            mVibrator.inVisible();
        }

        if (mFeatureSupport.isSupportBackFlash()){
            itemList.add(mBackFlash);
        }else{
            mBackFlash.inVisible();
        }

        if (mFeatureSupport.isSupportFrontFlash()){
            itemList.add(mFrontFlash);
        }else{
            mFrontFlash.inVisible();
        }

        if (mFeatureSupport.isSupportLightSensor()){
            itemList.add(mLightsensor);
        }else{
            mLightsensor.inVisible();
        }

        if (mFeatureSupport.isSupportLed()){
            itemList.add(mLed);
        }else{
            mLed.inVisible();
        }

        if (mFeatureSupport.isSupportLcd()){
            itemList.add(mLcd);
        }else{
            mLcd.inVisible();
        }

        if (mFeatureSupport.isSupportReceiver()){
            itemList.add(mReceiver);
        }else{
            mReceiver.inVisible();
        }

        if (mFeatureSupport.isSupportSpeaker()){
            itemList.add(mSpeaker);
        }else {
            mSpeaker.inVisible();
        }

        if (mFeatureSupport.isSupportHeadset()){
            itemList.add(mHeadset);
        }else{
            mHeadset.inVisible();
        }

        if (mFeatureSupport.isSupportProximitySensor()){
            itemList.add(mProximitySensor);
        }else{
            mProximitySensor.inVisible();
        }

        if (mFeatureSupport.isSupportFm()){
            itemList.add(mFm);
        }else {
            mFm.inVisible();
        }

        if (mFeatureSupport.isSupportCFT()){
            itemList.add(mCft);
        }else{
            mCft.inVisible();
        }

        if (mFeatureSupport.isSupportBackCamera() || mFeatureSupport.isSupportFrontCamera()){
            itemList.add(mCamera);
        }else{
            mCamera.inVisible();
        }

        if (mFeatureSupport.isSupportSdcard()){
            itemList.add(mSdcard);
        }else {
            mSdcard.inVisible();
        }

        if (mFeatureSupport.isSupportOtg()){
            itemList.add(mOtg);
        }else {
            mOtg.inVisible();
        }

        if (mFeatureSupport.isSupportSim()){
            itemList.add(mSim);
        }else {
            mSim.inVisible();
        }
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
        doTestAll();
    }


    private void doTestAll(){

        synchronized (obj) {
            new Runnable() {
                @Override
                public void run() {
                    if (isStartTest) {return;}
                    for (Item item :itemList){
                        item.startItem();
                    }
                    isStartTest = true;
                }
            }.run();
        }

    }

    private void doStopAll(){
        synchronized (obj) {
            if (!isStartTest) {return;}
            for (Item item :itemList){
                item.stopItem();
            }
            isStartTest = false;
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
