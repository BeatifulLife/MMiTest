package com.example.xu.mmitest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ALL_PERMISSION = 1;
    private final String RECORDFILENAME = "/sdcard/audiotest.amr";
    private Button audioBtn;
    private MediaRecorder mediaRecorder;
    private SimpleWaveRenderer simpleWaveRenderer;
    private WaveView maveView;
    private boolean isStart = false;
    Object obj = new Object();
    private Timer mTimer;
    private final int[] waveform = new int[10];
    private MediaPlayer mMediaplay;
    private KeyView mKeyView;
    private TextView mGsensorTextView;
    private Gsensor mGsnesor;
    private TextView mGpsTextView;
    private GPS mGps;
    private LocationManager mLocationManager;
    private Double mLongitude;
    private Double mLatitude;
    private android.location.GpsStatus mGpsStatus;
    private int mGpsCount;
    private Wifi mWifi;
    private TextView mWifiTips;
    private Bluetooth mBluetooth;
    private TextView mBluetoothTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioBtn = findViewById(R.id.audiorecordbtn);
        maveView = findViewById(R.id.maveid);
        audioBtn.setOnClickListener(this);
        mKeyView = findViewById(R.id.keylayout);
        mGsensorTextView = findViewById(R.id.gsensortips);
        mGsensorTextView.setTextColor(Color.GREEN);
        mGsnesor = new Gsensor(this, mGsensorTextView);

        mGpsTextView = findViewById(R.id.gpstips);
        mGps = new GPS(this,mGpsTextView);

        mWifiTips = findViewById(R.id.wifitips);
        mWifi = new Wifi(this,mWifiTips);

        mBluetoothTips = findViewById(R.id.bluetoothtips);
        mBluetooth = new Bluetooth(this,mBluetoothTips);

        simpleWaveRenderer = SimpleWaveRenderer.newInstance(Color.CYAN, Color.GRAY);
        maveView.setRenderer(simpleWaveRenderer);
        requestAllPermission();
    }


    private void requestAllPermission() {
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BODY_SENSORS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE}, REQUEST_ALL_PERMISSION);
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
                mTimer = new Timer();
                mGsnesor.startGsensor();
                mGps.startGps();
                mWifi.startWifi();
                mBluetooth.startBluetooth();
            }
        }.run();

    }

    private void doStopAll(){
        mTimer.cancel();
        mTimer = null;
        stopPlay();
        stopRecord();
        mGsnesor.stopGsensor();
        mGps.stopGps();
        mWifi.stopWifi();
        mBluetooth.stopBluetooth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        doStopAll();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.audiorecordbtn:
                if (!isStart) {
                    stopPlay();
                    startRecord();
                    audioBtn.setText(R.string.audiorecordplay);
                } else {
                    stopRecord();
                    audioBtn.setText(R.string.audiorecord);
                    startPlayRecord();
                }
                break;
        }
    }

    private Handler handler = new Handler();

    private void startRecord() {
        if (mediaRecorder != null) return;
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(RECORDFILENAME);
            mediaRecorder.prepare();
            mediaRecorder.start();
            isStart = true;
            mTimer.schedule(new RenderTask(), 0, 200);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("MYTEST", "error:" + e.getMessage());
        }

    }

    private void stopRecord() {
        if (mediaRecorder != null) {
            stopGetDb();
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void startGetDb() {
        synchronized (obj) {
            if (isStart) {
                for (int i = 0; i < waveform.length; i++) {
                    waveform[i] = mediaRecorder.getMaxAmplitude();

                    //Log.i("MYTEST","waveform["+i+"]="+waveform[i]);

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                maveView.setWave(waveform);
            }
        }
    }

    private void stopGetDb() {
        isStart = false;
        synchronized (obj) {
            maveView.setWave(null);
        }
    }

    class RenderTask extends TimerTask {

        @Override
        public void run() {
            startGetDb();
        }
    }

    private void startPlayRecord() {
        mMediaplay = new MediaPlayer();
        mMediaplay.setVolume((float) 1.0, (float) 1.0);
        mMediaplay.setLooping(false);
        try {
            mMediaplay.setDataSource(RECORDFILENAME);
            mMediaplay.prepare();
            mMediaplay.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MYTEST", e.getMessage());
        }
    }

    private void stopPlay() {
        if (mMediaplay != null) {
            mMediaplay.stop();
            mMediaplay.reset();
            mMediaplay.release();
            mMediaplay = null;
        }
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

}
