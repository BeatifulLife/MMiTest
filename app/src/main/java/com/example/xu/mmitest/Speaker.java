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

public class Speaker implements View.OnClickListener,Item {

    private TextView mTextView;
    private MediaPlayer mMediaPlay;
    private Activity mActivity;
    private static boolean isSpeakerTest = false;
    private ScheduledExecutorService scheduledExecutorService;
    public Speaker(Activity activity){
        mActivity = activity;
        mTextView = mActivity.findViewById(R.id.speakertips);
        mTextView.setTextColor(Color.YELLOW);
        mActivity.findViewById(R.id.speakerfailbtn).setOnClickListener(this);
        mActivity.findViewById(R.id.speakerpassbtn).setOnClickListener(this);
    }

    public void startSpeaker(){
        if (isSpeakerTest) {return;}
        AudioManager audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
        audioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_SPEAKER, AudioManager.ROUTE_ALL);
        mMediaPlay = new MediaPlayer();
        AudioAttributes.Builder builder = new AudioAttributes.Builder();
        builder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlay.setAudioAttributes(builder.build());
        mMediaPlay.reset();
        mMediaPlay.setVolume(1.0f, 1.0f);
        try {
            mMediaPlay.setDataSource(mActivity,Uri.parse("android.resource://"+mActivity.getPackageName()+"/"+R.raw.speaker));
            mMediaPlay.setLooping(true);
            mMediaPlay.prepare();
            mMediaPlay.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopSpeaker(){
        if (mMediaPlay!=null){
            mMediaPlay.stop();
            mMediaPlay.release();
            mMediaPlay = null;
        }
    }

    @Override
    public void inVisible(){
        mActivity.findViewById(R.id.speakeritem).setVisibility(View.GONE);
        mActivity.findViewById(R.id.speakerline).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.speakerfailbtn){
            mTextView.setTextColor(Color.RED);
            mTextView.setText(R.string.testfail);
        }else if (v.getId()==R.id.speakerpassbtn){
            mTextView.setTextColor(Color.GREEN);
            mTextView.setText(R.string.testsuccess);
        }
        isSpeakerTest = true;
        mActivity.findViewById(R.id.speakerbtnline).setVisibility(View.INVISIBLE);
        stopSpeaker();
    }

    @Override
    public void startItem() {
        scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                startSpeaker();
            }
        },5000,TimeUnit.MILLISECONDS);

    }

    @Override
    public void stopItem() {
        if (scheduledExecutorService!=null){
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
        }
        stopSpeaker();
    }
}
