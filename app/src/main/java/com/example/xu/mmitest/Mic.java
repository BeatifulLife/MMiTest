package com.example.xu.mmitest;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

public class Mic implements View.OnClickListener {

    private final String RECORDFILENAME = "/sdcard/audiotest.amr";
    private final int[] waveform = new int[10];
    private Activity mActivity;
    private Button micBtn;
    private WaveView maveView;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mMediaplay;
    private SimpleWaveRenderer simpleWaveRenderer;
    private boolean isStart = false;
    private ScheduledExecutorService mSchedule;
    Object obj = new Object();

    public Mic(Activity activity){
        mActivity = activity;
        micBtn = activity.findViewById(R.id.audiorecordbtn);
        maveView = activity.findViewById(R.id.maveid);
        micBtn.setOnClickListener(this);
        simpleWaveRenderer = SimpleWaveRenderer.newInstance(Color.CYAN, Color.GRAY);
        maveView.setRenderer(simpleWaveRenderer);

    }


    public void stopMic(){
        stopPlay();
        stopRecord();
    }

    private android.os.Handler myHandler = new android.os.Handler();
    private void startRecord() {
        if (mediaRecorder != null) {return;}
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(RECORDFILENAME);
            mediaRecorder.prepare();
            mediaRecorder.start();
            isStart = true;

            mSchedule = new ScheduledThreadPoolExecutor(1);
            mSchedule.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    startGetDb();
                }
            },0,100,TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("MYTEST", "error:" + e.getMessage());
        }

    }

    private void stopRecord() {
        if (mSchedule!=null){
            mSchedule.shutdown();
            mSchedule = null;
        }
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
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        maveView.setWave(waveform);
                    }
                });

           }
        }
    }

    private void stopGetDb() {
        isStart = false;
        synchronized (obj) {
            maveView.setWave(null);
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
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.audiorecordbtn:
                if (!isStart) {
                    stopPlay();
                    startRecord();
                    micBtn.setText(R.string.audiorecordplay);
                } else {
                    stopRecord();
                    micBtn.setText(R.string.audiorecord);
                    startPlayRecord();
                }
                break;
        }
    }

    public void inVisible(){
        mActivity.findViewById(R.id.micitem).setVisibility(View.GONE);
    }
}
