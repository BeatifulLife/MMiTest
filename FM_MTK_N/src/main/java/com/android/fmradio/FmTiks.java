package com.android.fmradio;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fm_interface.FMcommon;
import com.example.fm_mtk_n.R;

public class FmTiks implements FMcommon,View.OnClickListener {
    private static final String TAG = "FM";
    private FmService mService = null;
    private boolean mIsServiceStarted = false;
    private boolean mIsServiceBinded = false;
    private AudioManager mAudioManager = null;
    private int mCurrentStation = 1057;
    private boolean mIsPlaying = false;
    private boolean mIsSearching = false;
    private PlayFMAsyncTask mPlayFMAsyncTask = null;
    private Activity mActivity;
    private TextView mTextView;
    private Button searchBtn;
    public FmTiks(Activity activity, Button button, TextView textView){
        mActivity = activity;
        mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        mTextView = textView;
        searchBtn = button;
        searchBtn.setOnClickListener(this);
    }

    public void startFm(){
        ComponentName cn = mActivity.startService(new Intent(mActivity, FmService.class));
        if (null == cn) {
            Log.e(TAG, "Error: Cannot start FM service");
        } else {
            Log.d(TAG, "Start FM service successfully.");
            mIsServiceStarted = true;
            mIsServiceBinded = mActivity.bindService(new Intent(mActivity, FmService.class),
                    mServiceConnection, Context.BIND_AUTO_CREATE);
        }
        if (!mIsServiceBinded) {
            Log.e(TAG, "Error: Cannot bind FM service");
            mActivity.finish();
            return;
        } else {
            Log.d(TAG, "Bind FM service successfully.");
        }
    }

    public void stopFM(){
        exitService();
    }

    private void exitService() {
        if (mIsServiceBinded) {
            mActivity.unbindService(mServiceConnection);
            mIsServiceBinded = false;
        }

        if (mIsServiceStarted) {
            boolean isSuccess = mActivity.stopService(new Intent(mActivity, FmService.class));
            if (!isSuccess) {
                Log.e(TAG, "Error: Cannot stop the FM service.");
            }
            mIsServiceStarted = false;
        }

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        /**
         * called by system when bind service
         *
         * @param className component name
         * @param service   service binder
         */
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "FMRadioActivity.onServiceConnected start");
            mService = ((FmService.ServiceBinder) service).getService();
            if (null == mService) {
                Log.e(TAG, "ServiceConnection: Error: can't get Service");
                mActivity.finish();
            } else {
                if (!isServiceInit()) {
                    Log.d(TAG, "ServiceConnection: FM service is not init");
                    mTextView.setText("105.7");
                    initService(1057);
                    InitialThread thread = new InitialThread();
                    thread.start();

                } else {
                    Log.d(TAG, "ServiceConnection: FM service is already init");
                    if (isDeviceOpen()) {
                        updateCurrentStation();
                        mIsPlaying = isPowerUp();
                        // mIsRDSSupported = isRDSSupported();
                        mIsSearching = mService.isSeeking();

                        if (mIsPlaying) {
                            Log.d(TAG, "ServiceConnection: FM is already power up");
                            // if when searching channel, switch language,
                            // activity will be destroy, may be occur FMRadio is
                            // power up but mediaplayer doesn't play, need to resume FM
                            // audio to make FMPlayer play
                            onPlayFM();
                        }
                    } else {
                        // This is theoretically never happen.
                        Log.e(TAG, "Error: FM device is not open");
                    }
                }

            }
            Log.d(TAG, "FMRadioActivity.onServiceConnected end");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void startScan(){
        searchBtn.setVisibility(View.INVISIBLE);
        mTextView.setText(R.string.fmtips);
        setRds(false);
        setMute(true);
        short[] fmlist = autoScan(0);
        setRds(true);
        if (fmlist!=null){
            float freq = (float)(fmlist[fmlist.length-1]/10.0);
            mTextView.setText(""+freq);
            tune(freq);
            setMute(false);
        }else{
            mTextView.setText(R.string.fmnoradio);
        }
        searchBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        startScan();
    }

    private class PlayFMAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // When seek to a station, disable all other buttons and start an animation
            Log.d(TAG, "disable all other buttons and start animation when start play FM!");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "doInBackground: Play FM" + (float)(mCurrentStation/10));
            powerUp((float)105.7);
            Log.d(TAG, "doInBackground" + (float)(mCurrentStation/10));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "enable all other buttons!");
        }
    }

    private void onPlayFM() {
        Log.v(TAG, "start onPlayFM");
        // If the seek AsyncTask has been executed and not canceled, cancel it
        // before start new.
        if (null != mPlayFMAsyncTask && !mPlayFMAsyncTask.isCancelled()) {
            mPlayFMAsyncTask.cancel(true);
            mPlayFMAsyncTask = null;
        }
        mPlayFMAsyncTask = new PlayFMAsyncTask();
        mPlayFMAsyncTask.execute();

        Log.v(TAG, "end onPlayFM");
    }

    private boolean isServiceInit() {
        Log.v(TAG, "FMRadioActivity.isServiceInit");
        if (null != mService) {
            return mService.isServiceInited();
        }
        return false;
    }

    private void initService(int currentStation) {
        Log.v(TAG, "FMRadioActivity.initService: " + currentStation);
        if (null != mService) {
            mService.initService(currentStation);
        }
    }

    private boolean isDeviceOpen() {
        Log.v(TAG, "FMRadioActivity.isDeviceOpen");
        if (null != mService) {
            return mService.isDeviceOpen();
        }
        return false;
    }

    private boolean isPowerUp() {
        Log.v(TAG, "FMRadioActivity.isPowerUp");
        if (null != mService) {
            return mService.isPowerUp();
        }
        return false;
    }

    private int getFrequency() {
        Log.v(TAG, "FMRadioActivity.getFrequency");
        if (null != mService) {
            return mService.getFrequency();
        }
        return 0;
    }

    private void updateCurrentStation() {
        // get the frequency from service, set frequency in activity, UI, database
        // same as the frequency in service
        int freq = getFrequency();
        //if (FMRadioUtils.isValidStation(freq))
        {
            if (mCurrentStation != freq) {
                Log.d(TAG, "frequency in service isn't same as in database");
                mCurrentStation = freq;
                // FMRadioStation.setCurrentStation(mContext, mCurrentStation);
                //refreshStationUI(mCurrentStation);
            }
        }
    }

    class InitialThread extends Thread {

        /**
         * called when first enter FMRadio app, will open device and play fm
         */
        @Override
        public void run() {
            if (openDevice()) {
                Log.d(TAG, "opendev succeed.");
                // Check if RDS is supported.
                Bundle bundle = new Bundle();
                bundle.putFloat("frequency", (float)105.7);
                mService.handlePowerUp(bundle);
            } else {
                // ... If failed, exit?
                Log.e(TAG, "Error: opendev failed.");
            }
        }
    }

    private boolean openDevice() {
        Log.v(TAG, "FMRadioActivity.openDevice");
        if (null != mService) {
            return mService.openDevice();
        }
        return false;
    }

    @Override
    public boolean openDev(){
        return FmNative.openDev();
    }

    @Override
    public boolean closeDev(){
        return FmNative.closeDev();
    }

    @Override
    public boolean powerUp(float frequency) {
        return FmNative.powerUp(frequency);
    }

    @Override
    public boolean powerDown(int type) {
        return FmNative.powerDown(type);
    }

    @Override
    public boolean tune(float frequency) {
        return FmNative.tune(frequency);
    }

    @Override
    public float seek(float frequency, boolean isUp) {
        return FmNative.seek(frequency,isUp);
    }

    @Override
    public short[] autoScan(int startFreq) {
        return FmNative.autoScan();
    }

    @Override
    public boolean stopScan() {
        return FmNative.stopScan();
    }

    @Override
    public int setRds(boolean rdson) {
        return FmNative.setRds(rdson);
    }

    @Override
    public short readRds() {
        return FmNative.readRds();
    }

    @Override
    public byte[] getPs() {
        return FmNative.getPs();
    }

    @Override
    public byte[] getLrText() {
        return FmNative.getLrText();
    }

    @Override
    public short activeAf() {
        return FmNative.activeAf();
    }

    @Override
    public int setMute(boolean mute) {
        return FmNative.setMute(mute);
    }

    @Override
    public int isRdsSupport() {
        return FmNative.isRdsSupport();
    }

    @Override
    public int switchAntenna(int antenna) {
        return FmNative.switchAntenna(antenna);
    }


}
