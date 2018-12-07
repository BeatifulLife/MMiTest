package com.example.xu.mmitest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Receiver implements View.OnClickListener,Item {
    private Activity mActivity;
    private TextView mTextView;
    private static boolean isReceiverTest = false;
    private MediaPlayer mMediaPlay;
    private ScheduledExecutorService scheduledExecutorService;
    public Receiver(Activity activity){
        mActivity = activity;
        mTextView = activity.findViewById(R.id.receivertips);
        mTextView.setTextColor(Color.YELLOW);
        mActivity.findViewById(R.id.receiverfailbtn).setOnClickListener(this);
        mActivity.findViewById(R.id.receiverpassbtn).setOnClickListener(this);

    }

    public void startReceiver(){
        if (isReceiverTest) {return;}
        AudioManager audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
        audioManager.setRouting(AudioManager.MODE_IN_CALL, AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
        mMediaPlay = new MediaPlayer();
        AudioAttributes.Builder builder = new AudioAttributes.Builder();
        builder.setLegacyStreamType(AudioManager.STREAM_VOICE_CALL);
        mMediaPlay.setAudioAttributes(builder.build());
        mMediaPlay.reset();
        mMediaPlay.setVolume(1.0f, 1.0f);
        try {
            mMediaPlay.setDataSource(mActivity,Uri.parse("android.resource://"+mActivity.getPackageName()+"/"+R.raw.receiver));
            mMediaPlay.setLooping(true);
            mMediaPlay.prepare();
            mMediaPlay.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopReceiver(){
        if (mMediaPlay!=null){
            mMediaPlay.stop();
            mMediaPlay.release();
            mMediaPlay = null;
        }
    }

    @Override
    public void inVisible(){
        mActivity.findViewById(R.id.receiveritem).setVisibility(View.GONE);
        mActivity.findViewById(R.id.receiverline).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.receiverfailbtn){
            mTextView.setTextColor(Color.RED);
            mTextView.setText(R.string.testfail);
        }else if (v.getId()==R.id.receiverpassbtn){
            mTextView.setTextColor(Color.GREEN);
            mTextView.setText(R.string.testsuccess);
        }
        isReceiverTest = true;
        mActivity.findViewById(R.id.receiverbtnline).setVisibility(View.INVISIBLE);
        stopReceiver();
    }

    @Override
    public void startItem() {
        scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                startReceiver();
            }
        },4000,TimeUnit.MILLISECONDS);

    }

    @Override
    public void stopItem() {
        if (scheduledExecutorService!= null){
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
        }
        stopReceiver();
    }
}
