package com.example.xu.mmitest;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

public class SimpleWaveRenderer implements WaveRenderer {

    private final int mBackColor;
    private final Paint mPaint;
    private final Path mPath;

    private SimpleWaveRenderer(int mBackColor, Paint mPaint, Path mPath) {
        this.mBackColor = mBackColor;
        this.mPaint = mPaint;
        this.mPath = mPath;
    }

    public static SimpleWaveRenderer newInstance(int backgroundColor,int foregroundcolor){
        Paint paint = new Paint();
        paint.setColor(foregroundcolor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5.0f);
        paint.setStyle(Paint.Style.STROKE);

        Path path = new Path();
        return new SimpleWaveRenderer(backgroundColor,paint,path);
    }


    @Override
    public void render(Canvas canvas, int[] waveform) {
        canvas.drawColor(mBackColor);
        float width = canvas.getWidth();
        float height = canvas.getHeight();

        mPath.reset();
        //Log.i("MYTEST","length="+(waveform==null?"null":waveform.length));
        if (waveform != null) {
            // 绘制波形
            renderWaveForm(waveform, width, height);
        } else {
            // 绘制直线
            renderBlank(width, height);
        }

        canvas.drawPath(mPath, mPaint);
    }

    private void renderWaveForm(int []waveform,float width,float height){
        float xIncreament = width/(float)waveform.length;
        float yIncreament = height/(float)8000;
        float ymiddle = height*0.5f;

        mPath.moveTo(0,height);
        for (int i=0;i<waveform.length;i++){
            float ypos = height- (yIncreament * waveform[i]);
            //Log.i("MYTEST","waveform[i]="+waveform[i]+",ypos="+ypos);
            mPath.lineTo(xIncreament*i,ypos);
        }
        mPath.lineTo(width,height);
    }

    private void renderBlank(float width, float height) {
        int y = (int) (height * 0.5);
        mPath.moveTo(0, y);
        mPath.lineTo(width, y);
    }
}
