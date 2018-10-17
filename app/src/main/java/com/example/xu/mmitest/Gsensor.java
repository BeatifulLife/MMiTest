package com.example.xu.mmitest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;


public class Gsensor implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mGsensor;
    private TextView mTextView;
    private Context mContext;
    private String tips = new String();

    public  Gsensor(Context context,TextView textView){
        mContext = context;
        mTextView = textView;
    }

    private void init(){
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mGsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this,mGsensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void startGsensor(){
        init();
    }

    public void stopGsensor(){
        mSensorManager.unregisterListener(this);
    }

    private android.os.Handler handler = new android.os.Handler();

    @Override
    public void onSensorChanged(SensorEvent event) {
        tips = String.format("  %f  %f  %f",event.values[0],event.values[1],event.values[2]);
        mTextView.setText(tips);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
