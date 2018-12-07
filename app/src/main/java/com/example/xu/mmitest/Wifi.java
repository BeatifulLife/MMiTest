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
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class Wifi implements Item{

    private Activity mActivity;
    private TextView mTextView;
    private WifiManager mWifiManager;
    private Resources mResource;
    private static boolean isWifiReges = false;
    private Object obj = new Object();
    private boolean isWifiTest = false;
    private int level = -200;
    private int count = 0;


    public Wifi(Activity activity) {
        this.mActivity = activity;
        this.mTextView = activity.findViewById(R.id.wifitips);
        mResource = mActivity.getResources();
    }

    private void init(){
        if (isWifiReges){return;}
        mWifiManager = (WifiManager) mActivity.getSystemService(Context.WIFI_SERVICE);
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);

        }
        //SystemClock.sleep(5000);会卡顿

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mActivity.registerReceiver(wifiReceiver,filter);
        isWifiReges = true;
        mWifiManager.startScan();
        mTextView.setTextColor(Color.YELLOW);
        mTextView.setText(R.string.wifiscan);

    }

    public void startWifi(){
            synchronized (obj) {
                if (isWifiTest) {return;}
                init();
            }
    }

    public void stopWifi(){
        synchronized (obj) {
            if (isWifiReges) {
                mActivity.unregisterReceiver(wifiReceiver);
                isWifiReges = false;
            }
        }
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())){
                updateText();

                if (level>=-55) {
                    isWifiTest = true;
                    stopWifi();
                }else{
                    mWifiManager.startScan();
                }

            }
        }
    };

    private void updateText(){
        List<ScanResult> wifiList = mWifiManager.getScanResults(); //未打开定位功能无法获取结果?
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

    @Override
    public void startItem() {
        startWifi();
    }

    @Override
    public void stopItem() {
        stopWifi();
    }

    @Override
    public void inVisible() {
        mActivity.findViewById(R.id.wifiitem).setVisibility(View.GONE);
        mActivity.findViewById(R.id.wifiline).setVisibility(View.GONE);
    }
}
