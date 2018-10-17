package com.example.xu.mmitest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

import java.util.List;

public class Wifi {

    private Context mContext;
    private TextView mTextView;
    private WifiManager mWifiManager;
    private Resources mResource;
    private boolean isWifiReges = false;
    private Object obj = new Object();
    private boolean isHasTest = false;


    public Wifi(Context mContext, TextView mTextView) {
        this.mContext = mContext;
        this.mTextView = mTextView;
        mResource = mContext.getResources();
    }

    private void init(){
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);

        }
        //SystemClock.sleep(5000);会卡顿

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mContext.registerReceiver(wifiReceiver,filter);
        isWifiReges = true;
        //mWifiManager.startScan();
        mTextView.setText(R.string.wifiscan);
        updateText();
    }

    private Handler myHandler = new Handler();
    public void startWifi(){
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (obj) {
                    if (isHasTest) return;
                    init();
                }
            }
        });
    }

    public void stopWifi(){
        synchronized (obj) {
            if (isWifiReges) {
                mContext.unregisterReceiver(wifiReceiver);
                isWifiReges = false;
                /*
                if(mWifiManager.isWifiEnabled()){
                    mWifiManager.setWifiEnabled(false);
                }
                */
            }
        }
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.endsWith(intent.getAction())){
                updateText();
                isHasTest = true;
                stopWifi();
            }
        }
    };

    private void updateText(){
        List<ScanResult> wifiList = mWifiManager.getScanResults();
        String wifiText = "";
        int count = 0;
        int level = -200;
        String ssid = "";
        if (wifiList!=null){
            for(ScanResult result:wifiList){
                count++;
                if (result.level>level){
                    level = result.level;
                    ssid = result.SSID;
                }
            }
        }
        if (count>0) {

            wifiText = String.format(" %s:%d %s:%s（%d）",
                    mResource.getString(R.string.wifinum), count,
                    mResource.getString(R.string.maxlevelwifi), ssid,
                    level);
        }else{
            wifiText = mResource.getString(R.string.notfindwifi);
        }
        if (level>=-55){
            mTextView.setTextColor(Color.GREEN);
        }else{
            mTextView.setTextColor(Color.RED);
        }
        mTextView.setText(wifiText);
    }

}
