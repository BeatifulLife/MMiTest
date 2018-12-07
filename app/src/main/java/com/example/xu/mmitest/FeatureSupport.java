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
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author xuzhaoyou
 * @date 2018/10/18
 */
public class FeatureSupport {

    private final boolean HAS_LED = true;
    private final boolean HAS_RECEIVER = true;
    private final boolean HAS_HEADSET = false;
    private final boolean HAS_SPEAKER = false;
    private final boolean HAS_KEY = true;
    private final boolean HAS_WIFI = true;
    private final boolean HAS_LCD = false;
    private final boolean HAS_FM = true;
    private final boolean HAS_BLUETOOTH = true;
    private final boolean HAS_BACKCAMERA = true;
    private final boolean HAS_FRONTCAMERA = true;
    private final boolean HAS_FRONTFLASH = false;
    private final boolean HAS_BACKTFLASH = true;
    private final boolean HAS_GPS = true;
    private final boolean HAS_GSENSOR = true;
    private final boolean HAS_VIBRATOR = true;
    private final boolean HAS_MAINMIC = true;
    private final boolean HAS_LIGHTSENSOR = false;
    private final boolean HAS_PROXIMITY = false;
    private final boolean HAS_CFT = true;
    private final boolean HAS_SDCARD = true;
    private final boolean HAS_OTG = true;
    private final boolean HAS_SIM = true;
    private Activity mActivity;
    private String backCamereId;
    private String frontCameraId;
    public FeatureSupport(Activity activity){
        mActivity = activity;
    }

    public boolean isSupportBluetooth(){
        return HAS_BLUETOOTH;
        /*
        BluetoothManager mBlueManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        if(mBlueManager== null || mBlueManager.getAdapter()==null){
            return false;
        }else{
            return true;
        }*/
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
        return HAS_BACKTFLASH;
        //return isSupportCameraFeature(CAMERA_FEATURE.FLASH_BACK);
    }

    public boolean isSupportFrontFlash(){
        return HAS_FRONTFLASH;
        //return isSupportCameraFeature(CAMERA_FEATURE.FLASH_FRONT);
    }

    public boolean isSupportBackCamera(){
        return HAS_BACKCAMERA;
        //return isSupportCameraFeature(CAMERA_FEATURE.CAMERA_BACK);
    }

    public boolean isSupportFrontCamera(){
        return HAS_FRONTCAMERA;
        //return isSupportCameraFeature(CAMERA_FEATURE.CAMERA_FRONT);
    }

    public boolean isSupportGps(){
        return HAS_GPS;
        /*
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager!=null && locationManager.getProvider(LocationManager.GPS_PROVIDER)!=null){
            return true;
        }else{
            return  false;
        }
        */
    }

    public boolean isSupportGsensor(){
        return HAS_GSENSOR;
        /*
        SensorManager sensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager!=null && sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            return true;
        }else{
            return false;
        }*/
    }

    public boolean isSupportVibrator(){
        return HAS_VIBRATOR;
        /*
        Vibrator vibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator!=null && vibrator.hasVibrator()){
            return true;
        }else{
            return false;
        }*/
    }

    public boolean isSupportMainMic(){
        return HAS_MAINMIC;
        /*
        AudioManager audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[]audioDeviceInfos = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        for (AudioDeviceInfo audioDeviceInfo: audioDeviceInfos){
            if (AudioDeviceInfo.TYPE_BUILTIN_MIC == audioDeviceInfo.getType()){
                return true;
            }
        }
        return false;
        */
    }

    public boolean isSupportLightSensor(){
        return HAS_LIGHTSENSOR;
        /*
        SensorManager sensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager!=null && sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!=null){
            return true;
        }else{
            return false;
        }*/
    }

    public boolean isSupportProximitySensor(){
        return HAS_PROXIMITY;
        /*
        SensorManager sensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager!=null && sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!=null){
            return true;
        }else{
            return false;
        }*/
    }

    public boolean isSupportLed(){ return HAS_LED; }

    public boolean isSupportReceiver(){ return HAS_RECEIVER; }

    public boolean isSupportHeadset(){ return  HAS_HEADSET; }

    public boolean isSupportSpeaker() { return HAS_SPEAKER; }

    public boolean isSupportKey() { return HAS_KEY; }

    public boolean isSupportWifi() { return  HAS_WIFI; }

    public boolean isSupportLcd() { return  HAS_LCD; }

    public boolean isSupportFm() { return  HAS_FM; }

    public boolean isSupportCFT() { return  HAS_CFT; }

    public boolean isSupportSdcard() { return  HAS_SDCARD; }

    public boolean isSupportOtg() { return  HAS_OTG; }

    public boolean isSupportSim() { return  HAS_SIM; }

}
