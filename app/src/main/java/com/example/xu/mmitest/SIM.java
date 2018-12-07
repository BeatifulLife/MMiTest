package com.example.xu.mmitest;


import android.content.Context;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.android.internal.telephony.PhoneConstants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SIM implements Item {

    private MainActivity mActivity;
    private TextView mTextView;
    private TelephonyManager mTelecomManager;
    private ScheduledExecutorService scheduledExecutorService;
    private static boolean isSimTest = false;
    private final int SIMS[] = {
            PhoneConstants.SIM_ID_1,
            PhoneConstants.SIM_ID_2,
            PhoneConstants.SIM_ID_3,
            PhoneConstants.SIM_ID_4
    };

    public SIM(MainActivity activity){
        mActivity =activity;
        mTextView = mActivity.findViewById(R.id.simtips);
        mTelecomManager = (TelephonyManager) mActivity.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public void startItem() {
        if (isSimTest){
            return;
        }
        scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                checkSim();
            }
        },5000,TimeUnit.MILLISECONDS);
    }

    @Override
    public void stopItem() {
        if (scheduledExecutorService!=null){
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
    }

    @Override
    public void inVisible() {
        mActivity.findViewById(R.id.simitem).setVisibility(View.GONE);
        mActivity.findViewById(R.id.simline).setVisibility(View.GONE);
    }

    private void checkSim(){
        int count = mTelecomManager.getPhoneCount();
        count = Math.min(SIMS.length,count);
        Method hasIccCardMethod = null;
        Boolean hasSim = false;
        String simtips = "";
        Resources resources = mActivity.getResources();
        try {
            hasIccCardMethod = mTelecomManager.getClass().getMethod("hasIccCard",new Class[]{int.class});
            for(int i=0;i<count;i++){
                hasSim  = (Boolean) hasIccCardMethod.invoke(mTelecomManager,new Object[]{SIMS[i]});
                if (hasSim){
                    simtips += String.format(resources.getString(R.string.simpass),SIMS[i]+1);
                }else{
                    simtips += String.format(resources.getString(R.string.simfaile),SIMS[i]+1);
                }
            }
            final String ftips = simtips;
            mActivity.myHandler.post(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(Html.fromHtml(ftips,0));
                    isSimTest = true;
                }
            });

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }
}
