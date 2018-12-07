package com.example.xu.mmitest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class OTG implements Item {

    private Activity mActivity;
    private TextView mTextView;
    private static boolean isOtgTest = false;
    private StorageManager mStorageManager;
    private boolean isReges = false;
    private Object obj = new Object();
    public OTG(Activity activity){
        mActivity = activity;
        mTextView = mActivity.findViewById(R.id.otgtips);
        mTextView.setTextColor(Color.RED);
        mStorageManager = (StorageManager) mActivity.getSystemService(Context.STORAGE_SERVICE);
    }
    @Override
    public void startItem() {
        if (isOtgTest){
            return;
        }
        synchronized (obj){
            if (!isReges){
                try {
                    Method registerListenerMethod = mStorageManager.getClass().getMethod("registerListener",new Class[]{StorageEventListener.class});
                    registerListenerMethod.invoke(mStorageManager,new Object[]{storageEventListener});
                    isReges = true;
                    checkHasOtgcard();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void stopItem() {
        synchronized (obj) {
            if (isReges) {
                try {
                    Method unregisterListenerMethod = mStorageManager.getClass().getMethod("unregisterListener", new Class[]{StorageEventListener.class});
                    unregisterListenerMethod.invoke(mStorageManager, new Object[]{storageEventListener});
                    isReges = false;
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void inVisible() {
        mActivity.findViewById(R.id.otgitem).setVisibility(View.GONE);
        mActivity.findViewById(R.id.otgline).setVisibility(View.GONE);
    }

    StorageEventListener storageEventListener = new StorageEventListener(){
        @Override
        public void onStorageStateChanged(String path, String oldState, String newStat) {
            super.onStorageStateChanged(path, oldState, newStat);
        }

        @Override
        public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) {
            if (vol.disk.isUsb()) {
                if (newState == VolumeInfo.STATE_MOUNTED) {
                    mTextView.setTextColor(Color.GREEN);
                    mTextView.setText(R.string.otgfind);
                    isOtgTest = true;
                }
            }
        }
    };

    //反射，不然又红线不舒服
    private boolean checkHasOtgcard(){
        try {
            Method getVolumesMethod = mStorageManager.getClass().getMethod("getVolumes",null);
            List<VolumeInfo> volumeInfos = (List<VolumeInfo>) getVolumesMethod.invoke(mStorageManager,null);
            for (VolumeInfo volumeInfo : volumeInfos){
                if (volumeInfo.disk != null && volumeInfo.disk.isUsb()) {
                    if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState(volumeInfo.getPath()))){
                        mTextView.setTextColor(Color.GREEN);
                        mTextView.setText(R.string.otgfind);
                        isOtgTest = true;
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


}
