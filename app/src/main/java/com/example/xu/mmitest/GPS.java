package com.example.xu.mmitest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class GPS implements LocationListener {
    private Context mContext;
    private LocationManager mLocationManager;
    private Double mLongitude;
    private Double mLatitude;
    private int mCount;
    private int mCounter;
    private TextView mTextView;
    private android.location.GpsStatus mGpsStatus;
    private Resources mResource;
    private MyGpsStatus myGpsStatus;
    private boolean mPRN1Found = false;
    private boolean isGpsReges = false;
    private Object obj= new Object();
    private boolean isHasTest = false;

    public GPS(Context context, TextView mTipsTextView) {
        this.mContext = context;
        mTextView = mTipsTextView;
        mResource = context.getResources();
    }


    private void init() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myGpsStatus = new MyGpsStatus();
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mGpsStatus= mLocationManager.getGpsStatus(null);
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            mLocationManager.addGpsStatusListener(myGpsStatus);
            isGpsReges = true;
        }catch (Exception e){
            Log.i("MYTEST","Error:"+e.getMessage());
        }
    }

    private Handler myHandler = new Handler();
    public void startGps(){
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (obj) {
                    if (isHasTest)return;
                    init();
                };
            }
        });
    }

    public void stopGps(){
        synchronized (obj) {
            if(isGpsReges) {
                isGpsReges = false;
                mLocationManager.removeUpdates(this);
                mLocationManager.removeGpsStatusListener(myGpsStatus);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLongitude = location.getLongitude();
        mLatitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Set<String> set = extras.keySet();
        String key=provider;
        if (set!=null){
            Iterator<String> it = set.iterator();
            while (it.hasNext()){
                key += ":"+it.next().toString();

                //Log.i("MYTEST","key:"+key+",value:"+extras.get(key));
            }
        }
        mTextView.setText(key);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("MYTEST","onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("MYTEST","onProviderDisabled");
    }


    class MyGpsStatus implements android.location.GpsStatus.Listener {

        @Override
        public void onGpsStatusChanged(int event) {
            Log.i("MYTEST","test");
            updateText();
        }
    }

    @SuppressLint("MissingPermission")
    private void updateText(){
        mCounter++;
        if(mLocationManager==null)return;
        mGpsStatus = mLocationManager.getGpsStatus(null);

        Iterator<GpsSatellite> it = mGpsStatus.getSatellites().iterator();
        mCount = 0;
        String SatellitesPRNs=" ";
        String SatellitesSNRs="";
        while (it.hasNext()){
            int PRN = 0;
            float SNR = 0.0f;
            try {
                GpsSatellite mSatellite = it.next();
                PRN = mSatellite.getPrn();
                SNR = mSatellite.getSnr();
            }catch (NoSuchElementException e){
                Log.e("MYTEST", "BUG: Here should be a Satellite!");
            }
            if( PRN == 1 ) {
                mPRN1Found = true;
            }
            SatellitesPRNs += Integer.toString(PRN)+ (it.hasNext() ? "/":" ");
            SatellitesSNRs += Float.toString(SNR)+ (it.hasNext() ? "/":" ");
            mCount++;
        }
        String tips="";
        if (mCounter>=60){
            isHasTest = true;
            stopGps();
        }

        if (mCount>0) {
            mTextView.setTextColor(Color.GREEN);
            if (mCounter>=60){
                tips = String.format(" %s%d PRN:%s SNR:%s", mResource.getString(R.string.satellitecount),mCount,
                        SatellitesPRNs,
                        SatellitesSNRs);
            }else {
                tips = String.format(" (%d)%s%d PRN:%s SNR:%s", mCounter, mResource.getString(R.string.satellitecount), mCount,
                        SatellitesPRNs,
                        SatellitesSNRs);
            }
        }else{
            mTextView.setTextColor(Color.RED);
            tips = String.format(" (%d)",mCounter);
        }
        mTextView.setText(tips);
    }

}