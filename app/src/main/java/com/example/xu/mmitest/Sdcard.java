package com.example.xu.mmitest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Sdcard implements Item {

    private StorageManager mStorageManager;
    private ScheduledExecutorService scheduledExecutorService;
    private Activity mActicity;
    private TextView mTextView;
    private boolean isReges = false;
    private Object obj = new Object();
    private static boolean isSdcardTest = false;
    public Sdcard(Activity activity){
        mActicity = activity;
        mTextView = mActicity.findViewById(R.id.sdcardtips);
        mTextView.setTextColor(Color.RED);
        mStorageManager = (StorageManager) mActicity.getSystemService(Context.STORAGE_SERVICE);
    }


    @Override
    public void startItem() {
        if (isSdcardTest){
            return;
        }
        scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                synchronized (obj) {
                    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
                    if (!isReges) {
                        mActicity.registerReceiver(sdcardReceiver, intentFilter);
                        isReges = true;
                    }
                }
                checkHasSdcard();
            }
        },1000,TimeUnit.MILLISECONDS);
    }

    //反射，不然又红线不舒服
    private boolean checkHasSdcard(){
        try {
            Method getVolumesMethod = mStorageManager.getClass().getMethod("getVolumes",null);
            List<VolumeInfo> volumeInfos = (List<VolumeInfo>) getVolumesMethod.invoke(mStorageManager,null);
            for (VolumeInfo volumeInfo : volumeInfos){
                if (volumeInfo.disk != null && volumeInfo.disk.isSd()) {
                    if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState(volumeInfo.getPath()))){
                        mTextView.setTextColor(Color.GREEN);
                        mTextView.setText(R.string.sdcardfind);
                        isSdcardTest = true;
                        stopItem();
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void stopItem() {
        if (scheduledExecutorService!=null){
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }

        synchronized (obj) {
            if (isReges) {
                mActicity.unregisterReceiver(sdcardReceiver);
                isReges = false;
            }
        }
    }

    @Override
    public void inVisible() {
        mActicity.findViewById(R.id.sdcarditem).setVisibility(View.GONE);
        mActicity.findViewById(R.id.sdcardline).setVisibility(View.GONE);
    }

    private BroadcastReceiver sdcardReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
                checkHasSdcard();
            }
        }
    };
}
