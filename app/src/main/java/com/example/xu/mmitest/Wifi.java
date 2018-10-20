package com.example.xu.mmitest;

import android.app.Activity;
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
    private int level = -200;
    private int count = 0;


    public Wifi(Activity activity) {
        this.mContext = activity;
        this.mTextView = activity.findViewById(R.id.wifitips);
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
        mWifiManager.startScan();
        mTextView.setTextColor(Color.YELLOW);
        mTextView.setText(R.string.wifiscan);

    }

    private Handler myHandler = new Handler();
    public void startWifi(){
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (obj) {
                    if (isHasTest) {return;}
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
            }
        }
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())){
                updateText();
                /*
                if (level>=-55) {
                    isHasTest = true;
                    stopWifi();
                }else{
                    mWifiManager.startScan();
                }
                */
                if (count==0) {
                    mWifiManager.startScan();
                }
            }
        }
    };

    private void updateText(){
        List<ScanResult> wifiList = mWifiManager.getScanResults();
        String wifiText = "";
        String ssid = "";
        count = 0;
        int temleavel = -200;
        if (wifiList!=null){
            for(ScanResult result:wifiList){
                count++;
                if (result.level>temleavel){
                    temleavel = result.level;
                    ssid = result.SSID;
                }
            }
        }
        level = temleavel;

        if (count>0) {
            if (level>=-55){
                mTextView.setTextColor(Color.GREEN);
            }else{
                mTextView.setTextColor(Color.GRAY);
            }
            wifiText = String.format("%s:%d %s:%s（%d）",
                    mResource.getString(R.string.wifinum), count,
                    mResource.getString(R.string.maxlevelwifi), ssid,
                    level);
        }else{
            mTextView.setTextColor(Color.RED);
            wifiText = mResource.getString(R.string.notfindwifi);
        }

        mTextView.setText(wifiText);
    }

}
