package com.example.xu.mmitest;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.util.Log;

/**
 * @author xuzhaoyou
 * @date 2018/10/18
 */
public class FeatureSupport {

    private Activity mActivity;
    private String backCamereId;
    private String frontCameraId;
    public FeatureSupport(Activity activity){
        mActivity = activity;
    }

    public boolean isSupportBluetooth(){
        BluetoothManager mBlueManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        if(mBlueManager== null || mBlueManager.getAdapter()==null){
            return false;
        }else{
            return true;
        }
    }

    public enum CAMERA_FEATURE{
        FLASH_BACK,     //后闪光灯
        FLASH_FRONT,    //前闪光灯
        CAMERA_BACK,    //后摄像头
        CAMERA_FRONT    //前摄像头
    }

    private boolean isSupportCameraFeature(CAMERA_FEATURE camerafeature ){
        CameraManager mCameramanager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        if (mCameramanager==null) {
            return false;
        }
        try {
            String[] cameraList = mCameramanager.getCameraIdList();
            for (String cameraid: cameraList){
                CameraCharacteristics cameraCharacteristics = mCameramanager.getCameraCharacteristics(cameraid);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                Boolean flashavalible = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);

                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    backCamereId = cameraid;
                    if (camerafeature == CAMERA_FEATURE.FLASH_BACK){
                        return (flashavalible!=null)?flashavalible:false;
                    }else if (camerafeature == CAMERA_FEATURE.CAMERA_BACK){
                        return true;
                    }

                }else if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT){
                    frontCameraId = cameraid;
                    if (camerafeature == CAMERA_FEATURE.FLASH_FRONT){
                        return (flashavalible!=null)?flashavalible:false;
                    }else if (camerafeature == CAMERA_FEATURE.CAMERA_FRONT){
                        return true;
                    }
                }

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getBackCamereId(){
        return backCamereId;
    }

    public String getFrontCameraId(){
        return frontCameraId;
    }

    public boolean isSupportBackFlash(){
        return isSupportCameraFeature(CAMERA_FEATURE.FLASH_BACK);
    }

    public boolean isSupportFrontFlash(){
        return isSupportCameraFeature(CAMERA_FEATURE.FLASH_FRONT);
    }

    public boolean isSupportBackCamera(){
        return isSupportCameraFeature(CAMERA_FEATURE.CAMERA_BACK);
    }

    public boolean isSupportFrontCamera(){
        return isSupportCameraFeature(CAMERA_FEATURE.CAMERA_FRONT);
    }

    public boolean isSupportGps(){
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager!=null && locationManager.getProvider(LocationManager.GPS_PROVIDER)!=null){
            return true;
        }else{
            return  false;
        }
    }

    public boolean isSupportGsensor(){
        SensorManager sensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager!=null && sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            return true;
        }else{
            return false;
        }
    }

    public boolean isSupportVibrator(){
        Vibrator vibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator!=null && vibrator.hasVibrator()){
            return true;
        }else{
            return false;
        }
    }

    public boolean isSupportMainMic(){
        AudioManager audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[]audioDeviceInfos = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        for (AudioDeviceInfo audioDeviceInfo: audioDeviceInfos){
            /*
            Log.i("MYTEST","Type:"+audioDeviceInfo.getType()+",Address:"+audioDeviceInfo.getAddress()+
            ",Name:"+audioDeviceInfo.getProductName()+",Id"+audioDeviceInfo.getId());
            */

            if (AudioDeviceInfo.TYPE_BUILTIN_MIC == audioDeviceInfo.getType()){
                return true;
            }
        }
        return false;
    }


}
