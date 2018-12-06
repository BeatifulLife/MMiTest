package com.example.xu.mmitest;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xuzhaoyou
 * @date 2018/10/22
 * @// TODO: 2018/10/22  allow system_app sysfs:file { read write};
 */
public class LED implements View.OnClickListener,Item {
    private final String TURN_ON = "255";
    private final String TURN_OFF = "0";
    private Object obj = new Object();
    private ScheduledExecutorService mSchedule;
    private static boolean isLedTest = false;
    private boolean isLedStart = false;
    private final String LED_PATHS[] = {
            "/sys/class/leds/red/brightness",
            "/sys/class/leds/green/brightness",
            "/sys/class/leds/blue/brightness"
    };
    private TextView mTextView;
    private Map<String,String> ledValueMap = new HashMap<String, String>();
    private Activity mActivity;
    public LED(Activity activity){
        this.mActivity = activity;
        mTextView = activity.findViewById(R.id.ledtips);
        mTextView.setTextColor(Color.YELLOW);
        activity.findViewById(R.id.ledfailbtn).setOnClickListener(this);
        activity.findViewById(R.id.ledpassbtn).setOnClickListener(this);
    }

    @Override
    public void inVisible(){
        mActivity.findViewById(R.id.leditem).setVisibility(View.GONE);
    }

    public void startLed(){
        synchronized (obj) {
            if (isLedTest){return;}
            getLedValues();
            isLedStart = true;
            startLedTest();
        }
    }

    public void stopLed(){
        synchronized (obj) {
            if (isLedStart) {
                isLedStart = false;
                mSchedule.shutdown();
                mSchedule = null;
                reStoreLedValues();
            }
        }
    }

    private void startLedTest(){
        mSchedule = new ScheduledThreadPoolExecutor(1);

        mSchedule.scheduleAtFixedRate(new Runnable() {
            private int indexled = 0;
            @Override
            public void run() {
                int temindex = 0;
                for (String key:ledValueMap.keySet()){
                    if (temindex == indexled) {
                        turnOffLeds();
                        writeFile(key, TURN_ON);
                        indexled = (++indexled) % ledValueMap.size();
                        return;
                    }
                    temindex++;

                }
            }
        },500,1000,TimeUnit.MILLISECONDS);

    }

    private void getLedValues(){
        ledValueMap.clear();
        for (int i=0;i<LED_PATHS.length;i++){
            String value = readFile(LED_PATHS[i]);
            if (value != null){
                ledValueMap.put(LED_PATHS[i],value);
            }
        }
    }

    private void reStoreLedValues(){
        for (String key:ledValueMap.keySet()){
            writeFile(key,ledValueMap.get(key));
        }
    }

    private String readFile(String path){
        File file = new File(path);
        String value = null;
        FileReader fr = null;
        BufferedReader br = null;
        if (file.exists()){
            try {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                value = br.readLine();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i("MYTEST","Read Error0:"+e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("MYTEST","Read Error1:"+e.getMessage());
            }finally {
                try {
                    if (br!=null) {
                        br.close();
                    }
                    if(fr!=null){
                        fr.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    private void writeFile(String path,String value){
        File file = new File(path);
        FileWriter fw = null;
        BufferedWriter bw = null;
        if (file.exists()){
            try {
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
                bw.write(value);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i("MYTEST","Write Error0:"+e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("MYTEST","Write Error1:"+e.getMessage());
            }finally {
                try {
                    if(bw!=null) {
                        bw.close();
                    }
                    if(fw!=null) {
                        fw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void turnOffLeds(){
        for (String key:ledValueMap.keySet()){
            writeFile(key,TURN_OFF);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.ledfailbtn){
            mTextView.setTextColor(Color.RED);
            mTextView.setText(R.string.testfail);
        }else if (v.getId()==R.id.ledpassbtn){
            mTextView.setTextColor(Color.GREEN);
            mTextView.setText(R.string.testsuccess);
        }
        isLedTest = true;
        mActivity.findViewById(R.id.ledbtnline).setVisibility(View.INVISIBLE);
        stopLed();
    }

    @Override
    public void startItem() {
        startLed();
    }

    @Override
    public void stopItem() {
        stopLed();
    }
}
