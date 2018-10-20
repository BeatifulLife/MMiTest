package com.example.xu.mmitest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xuzhaoyou
 * @date 2018/10/19
 */
public class Bluetooth {
    private TextView mTextView;
    private Activity mActivity;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private int mBlueState;
    private Resources mResources;
    private Map<String,String> map = new HashMap<String,String>();
    private Object obj = new Object();
    private boolean isBluetoothReges = false;
    private boolean isHasTest = false;

    public Bluetooth(Activity activity) {
        this.mTextView = activity.findViewById(R.id.bluetoothtips);
        this.mActivity = activity;
        mResources= activity.getResources();
        mBluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    private void init(){
        if (mBluetoothManager!=null){
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (!mBluetoothAdapter.isEnabled()){
                mBluetoothAdapter.enable();
            }
            //SystemClock.sleep(5000);会卡顿

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            mActivity.registerReceiver(bluetoothReceiver,intentFilter);
            isBluetoothReges = true;
            mBluetoothAdapter.startDiscovery();
            mTextView.setTextColor(Color.YELLOW);
            mTextView.setText(R.string.bluetoothscan);

        }
    }

    private Handler myHandler = new Handler();
    public void startBluetooth(){
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

    public void stopBluetooth(){
        synchronized (obj) {
            if (isBluetoothReges) {
                mActivity.unregisterReceiver(bluetoothReceiver);
                isBluetoothReges = false;
                /*
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                }*/
            }
        }
    }

    public void inVisible(){
        mActivity.findViewById(R.id.bluetoothitem).setVisibility(View.GONE);
    }

    BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){
                mTextView.setTextColor(Color.YELLOW);
                mBlueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,0);
                if (mBlueState == BluetoothAdapter.STATE_ON){
                    mTextView.setText(R.string.bluetoothstateon);
                }else if (mBlueState == BluetoothAdapter.STATE_TURNING_ON){
                    mTextView.setText(R.string.bluetoothstateturnon);
                    mBluetoothAdapter.startDiscovery();
                }else if(mBlueState == BluetoothAdapter.STATE_TURNING_OFF){
                    mTextView.setText(R.string.bluetoothstateturnoff);
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())){
                mTextView.setTextColor(Color.YELLOW);
                mTextView.setText(R.string.bluetoothscan);
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!map.containsKey(bluetoothDevice.getAddress())) {
                    map.put(bluetoothDevice.getAddress(),bluetoothDevice.getName());
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                String tips = "";
                int devicenum = 0;
                for(String mac:map.keySet()){
                    devicenum++;
                    tips += " " +map.get(mac);
                    if (devicenum!=map.size()){
                        tips+=":";
                    }
                }
                if (devicenum>0) {
                    mTextView.setTextColor(Color.GREEN);
                    mTextView.setText(tips);
                }else{

                    mTextView.setTextColor(Color.RED);
                    mTextView.setText(mResources.getString(R.string.notfindbluetooth));
                }

                if (devicenum==0){
                    SystemClock.sleep(5000);
                    if (isBluetoothReges) {
                        mBluetoothAdapter.startDiscovery();
                    }
                }else {
                    isHasTest = true;
                    stopBluetooth();
                }
            }
        }
    };
}
