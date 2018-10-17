package com.example.xu.mmitest;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

public class WaveView extends View {
    private WaveRenderer mRender;
    private int [] mWaveform;
    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setRenderer(WaveRenderer renderer){
        mRender = renderer;
    }

    public void setWave(int []waveform){
        if (waveform==null){
            mWaveform = null;
        }else {
            mWaveform = Arrays.copyOf(waveform, waveform.length);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRender!=null){
            mRender.render(canvas,mWaveform);
        }
    }
}
