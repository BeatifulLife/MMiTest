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
public class ProximitySensor implements SensorEventListener {

    private Activity mActivity;
    private TextView mTextview;
    private static boolean isProximitySensorTest = false;
    private SensorManager mProxumitySensor;
    private LinearLayout mLinearLayout;
    public ProximitySensor(Activity activity){
        mActivity = activity;
        mTextview = mActivity.findViewById(R.id.proximitysensortips);
        mTextview.setTextColor(Color.YELLOW);
        mTextview.setText(mActivity.getResources().getString(R.string.proximitysensornodata));
        mLinearLayout = mActivity.findViewById(R.id.proximitysensoritem);
    }

    private void init(){
        mProxumitySensor = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        Sensor lightsensor = mProxumitySensor.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mProxumitySensor.registerListener(this,lightsensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void startProxumitySensor(){
        init();
    }

    public void stopProxumitySensor(){
        mProxumitySensor.unregisterListener(this);
    }

    public void inVisible(){
        mLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isProximitySensorTest && (Float.compare(event.values[0],0.0f)>0 ||Float.compare(event.values[1],0.0f)>0||Float.compare(event.values[2],0.0f)>0)){
            isProximitySensorTest = true;
            mTextview.setTextColor(Color.GREEN);

        }
        mTextview.setText(String.format(" %f %f %f",event.values[0],event.values[1],event.values[2]));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
