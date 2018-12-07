package com.example.xu.mmitest;

import android.graphics.Color;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CFT implements Item{
    private MainActivity mActivity;
    private static boolean isCftTest = false;
    private static final String DEFAULT_VALUE =  "NOPROP";
    private TextView mTextView;
    private ScheduledExecutorService scheduledExecutorService;
    public CFT(MainActivity activity){
        mActivity = activity;
        mTextView = mActivity.findViewById(R.id.calitips);
    }

    @Override
    public void inVisible(){
        mActivity.findViewById(R.id.caliitem).setVisibility(View.GONE);
        mActivity.findViewById(R.id.caliline).setVisibility(View.GONE);
    }

    public void startCft(){
        if (isCftTest){
            return;
        }

        scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            int count = 0;
            @Override
            public void run() {
                String value = SystemProperties.get("gsm.mmi.cali",DEFAULT_VALUE);
                Log.i("MYTEST","gsm.mmi.cali="+value);
                if (DEFAULT_VALUE.equals(value)){
                    count++;
                    if (count > 30){
                        isCftTest = true;
                        mActivity.myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setTextColor(Color.RED);
                                mTextView.setText(R.string.calinoserver);
                            }
                        });
                    }
                }else if ("NODATA".equals(value) || "EXC".equals(value)){
                    isCftTest = true;
                    mActivity.myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mTextView.setTextColor(Color.RED);
                            mTextView.setText(R.string.calinodata);
                        }
                    });
                }else if ("NOGSM".equals(value)){
                    isCftTest = true;
                    mActivity.myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mTextView.setTextColor(Color.RED);
                            mTextView.setText(R.string.calinogsm);
                        }
                    });
                }else{
                    isCftTest = true;
                    if (value.endsWith("10P")){
                        mActivity.myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setTextColor(Color.GREEN);
                                mTextView.setText(R.string.calisucess);
                            }
                        });
                    }else{
                        mActivity.myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setTextColor(Color.RED);
                                mTextView.setText(R.string.califail);
                            }
                        });
                    }

                    if (isCftTest){
                        CFT.this.scheduledExecutorService.shutdown();

                    }
                }
            }
        },2000,2000, TimeUnit.MILLISECONDS);
    }

    public void stopCft(){
        if (scheduledExecutorService!=null){
            scheduledExecutorService.shutdown();
        }
    }

    @Override
    public void startItem() {
        startCft();
    }

    @Override
    public void stopItem() {
        stopCft();
    }
}
