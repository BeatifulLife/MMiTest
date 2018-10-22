package com.example.xu.mmitest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author xuzhaoyou
 * @date 2018/10/19
 */
public class LightSensor implements SensorEventListener {

    private Activity mActivity;
    private TextView mTextview;
    private static boolean isLightSensorTest = false;
    private SensorManager mLightSensor;
    private LinearLayout mLinearLayout;
    public LightSensor(Activity activity){
        mActivity = activity;
        mTextview = mActivity.findViewById(R.id.lightsensortips);
        mTextview.setTextColor(Color.YELLOW);
        mTextview.setText(mActivity.getResources().getString(R.string.lightsensornodata));
        mLinearLayout = mActivity.findViewById(R.id.lightsensoritem);
    }

    private void init(){
        mLightSensor = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        Sensor lightsensor = mLightSensor.getDefaultSensor(Sensor.TYPE_LIGHT);
        mLightSensor.registerListener(this,lightsensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void startLightSensor(){
        init();
    }

    public void stopLightSensor(){
        mLightSensor.unregisterListener(this);
    }

    public void inVisible(){
        mLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isLightSensorTest && (Float.compare(event.values[0],0.0f)>0 ||Float.compare(event.values[1],0.0f)>0||Float.compare(event.values[2],0.0f)>0)){
            isLightSensorTest = true;
            mTextview.setTextColor(Color.GREEN);

        }
        mTextview.setText(String.format(" %f %f %f",event.values[0],event.values[1],event.values[2]));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
