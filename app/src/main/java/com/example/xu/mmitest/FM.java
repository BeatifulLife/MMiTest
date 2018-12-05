package com.example.xu.mmitest;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.fmradio.FmNative;
import com.android.fmradio.FmService;

public class FM implements View.OnClickListener {

    private static final String TAG = "MMI-FM";
    private MainActivity mActicity;
    private Button searchBtn;
    private Button nextBtn;
    private Button passBtn;
    private Button failBtn;
    private static boolean isFmTest = false;
    private boolean isReges = false;
    private Object obj = new Object();
    private FmService mService;
    private int defaultFreq = 1057;
    private AudioManager mAudioManager;
    private PlayFMAsyncTask mPlayFMAsyncTask = null;
    private boolean mIsServiceStarted = false;
    private boolean mIsServiceBinded = false;
    private TextView mTextView;
    private short fmList[] = null;
    private int index = 0;
    public FM(MainActivity activity){
        mActicity = activity;
        searchBtn = mActicity.findViewById(R.id.fmsearchbtn);
        nextBtn = mActicity.findViewById(R.id.fmnextbtn);
        passBtn = mActicity.findViewById(R.id.fmpassbtn);
        failBtn = mActicity.findViewById(R.id.fmfailbtn);
        mTextView = mActicity.findViewById(R.id.fmtips);
        searchBtn.setEnabled(false);
        nextBtn.setEnabled(false);
        searchBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        passBtn.setOnClickListener(this);
        failBtn.setOnClickListener(this);
        mAudioManager = (AudioManager) mActicity.getSystemService(Context.AUDIO_SERVICE);
    }

    private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())){
                exitService();
            }else if (AudioManager.ACTION_HEADSET_PLUG.equals(intent.getAction())){
                startService();
            }
        }
    };

    public void startFm(){
        if (isFmTest) {
            return;
        }
        synchronized (obj) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.setPriority(1000);
            intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
            mActicity.registerReceiver(headsetReceiver, intentFilter);
            isReges = true;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        }

    }

    public void stopFm(){
        exitService();
    }

    private void startService(){
        ComponentName cn = mActicity.startService(new Intent(mActicity, FmService.class));
        if (null == cn) {
            Log.e(TAG, "Error: Cannot start FM service");
        } else {
            Log.d(TAG, "Start FM service successfully.");
            mIsServiceStarted = true;
            mIsServiceBinded = mActicity.bindService(new Intent(mActicity, FmService.class),
                    mServiceConnection, Context.BIND_AUTO_CREATE);
        }
        if (!mIsServiceBinded) {
            Log.e(TAG, "Error: Cannot bind FM service");
            return;
        } else {
            Log.d(TAG, "Bind FM service successfully.");
        }
    }

    private void exitService() {
        if (mIsServiceBinded) {
            mActicity.unbindService(mServiceConnection);
            mIsServiceBinded = false;
        }

        if (mIsServiceStarted) {
            boolean isSuccess = mActicity.stopService(new Intent(mActicity, FmService.class));
            if (!isSuccess) {
                Log.e(TAG, "Error: Cannot stop the FM service.");
            }
            mIsServiceStarted = false;

        }

        mActicity.myHandler.post(new Runnable() {
            @Override
            public void run() {
                searchBtn.setEnabled(false);
                nextBtn.setEnabled(false);
            }
        });

    }

    private void searchFm(){
        mActicity.myHandler.post(new Runnable() {
            @Override
            public void run() {
                searchBtn.setEnabled(false);
                mTextView.setText(R.string.fmtips);
                if (mService!=null){
                    if (mService.isScanning()){
                        mService.stopScan();
                    }
                    FmNative.setMute(true);
                    FmNative.setRds(false);
                    fmList = FmNative.autoScan();
                    if (fmList != null && fmList.length > 0){
                        defaultFreq = fmList[0];
                        float value = (float) defaultFreq/10f;
                        index = 0;
                        mService.tuneStationAsync(value);
                        mTextView.setText(""+value);
                        FmNative.setRds(true);
                        FmNative.setMute(false);
                    }
                }
                nextBtn.setEnabled(true);
                searchBtn.setEnabled(true);

            }
        });

    }

    private void nextFm(){
        mActicity.myHandler.post(new Runnable() {

            @Override
            public void run() {
                nextBtn.setEnabled(false);
                if (mService == null){
                    return;
                }
                if (fmList!=null && fmList.length>0){
                    index++;
                    if (index>=fmList.length){
                        index = 0;
                    }
                    defaultFreq = fmList[index];
                    mService.tuneStationAsync(defaultFreq/10f);
                }
                nextBtn.setEnabled(true);
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fmsearchbtn:
                searchFm();
                break;
            case R.id.fmnextbtn:
                nextFm();
                break;
            case R.id.fmpassbtn:
            case R.id.fmfailbtn:
                exitService();
                synchronized (obj) {
                    isFmTest = true;
                }
                mActicity.findViewById(R.id.fmbtnline).setVisibility(View.GONE);
                if (id == R.id.fmfailbtn){
                    mTextView.setTextColor(Color.RED);
                    mTextView.setText(R.string.testfail);
                }else{
                    mTextView.setTextColor(Color.GREEN);
                    mTextView.setText(R.string.testsuccess);
                }

                break;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((FmService.ServiceBinder)service).getService();
            if (mService == null){

            }else{
                if (!isServiceInit()){
                    initService(defaultFreq);
                    InitialThread thread = new InitialThread();
                    thread.start();
                }else{
                    if (isDeviceOpen()) {
                        updateCurrentStation();
                        onPlayFM();
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private boolean isServiceInit() {
        if (null != mService) {
            return mService.isServiceInited();
        }
        return false;
    }

    private void initService(int currentStation) {
        if (null != mService) {
            mService.initService(currentStation);
        }
    }

    private boolean openDevice() {
        if (null != mService) {
            return mService.openDevice();
        }
        return false;
    }

    private boolean isDeviceOpen() {
        if (null != mService) {
            return mService.isDeviceOpen();
        }
        return false;
    }

    private int getFrequency() {
        if (null != mService) {
            return mService.getFrequency();
        }
        return 0;
    }

    private void updateCurrentStation() {
        int freq = getFrequency();
        if (defaultFreq != freq) {
            defaultFreq = freq;
        }
    }

    private boolean isPowerUp() {
        if (null != mService) {
            return mService.isPowerUp();
        }
        return false;
    }

    private boolean powerUp(float frequency) {
        if (null != mService) {
            return mService.powerUp(frequency);
        }
        return false;
    }

    private void onPlayFM() {
        if (null != mPlayFMAsyncTask && !mPlayFMAsyncTask.isCancelled()) {
            mPlayFMAsyncTask.cancel(true);
            mPlayFMAsyncTask = null;
        }
        mPlayFMAsyncTask = new PlayFMAsyncTask();
        mPlayFMAsyncTask.execute();
    }

    class InitialThread extends Thread {

        @Override
        public void run() {
            if (openDevice()) {
                Bundle bundle = new Bundle();
                bundle.putFloat("frequency", (float)defaultFreq);
                mService.handlePowerUp(bundle);
                mActicity.myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        searchBtn.setEnabled(true);
                        if (fmList!=null && fmList.length>0){
                            nextBtn.setEnabled(true);
                        }
                    }
                });
            }
        }
    }

    private class PlayFMAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            powerUp((float)defaultFreq/10f);
            mActicity.myHandler.post(new Runnable() {
                @Override
                public void run() {
                    searchBtn.setEnabled(true);
                    if (fmList!=null && fmList.length>0){
                        nextBtn.setEnabled(true);
                    }
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }
}
