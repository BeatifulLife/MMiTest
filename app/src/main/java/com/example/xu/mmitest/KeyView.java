package com.example.xu.mmitest;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class KeyView extends LinearLayout implements Item {


    private static boolean isKeyTest = false;
    private  KeyMap[] keymap = {
      new KeyMap(R.string.volup,KeyEvent.KEYCODE_VOLUME_UP,false),
      new KeyMap(R.string.voldown,KeyEvent.KEYCODE_VOLUME_DOWN,false),
    };

    public KeyView(Context context) {
        super(context);
    }

    public KeyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    public KeyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public KeyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void createView(Context context){
        Resources resources = context.getResources();
        for (int i=0;i<keymap.length;i++){
            Button button = new Button(context);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,resources.getDimensionPixelSize(R.dimen.keyBtnHeight));
            button.setLayoutParams(layoutParams);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP,resources.getDimensionPixelSize(R.dimen.keyBtnTextSize));
            button.setTextColor(ContextCompat.getColor(context,R.color.keybtntextcolor));
            button.setText(keymap[i].keystrid);
            button.setBackgroundColor(Color.RED);
            button.setEnabled(false);
            keymap[i].setButton(button);
            this.addView(button);
        }
    }

    @Override
    public void startItem() {

    }

    @Override
    public void stopItem() {

    }

    @Override
    public void inVisible() {
        isKeyTest = true;
        Context context = this.getContext();
        if (context instanceof Activity){
            Activity activity = (Activity) context;
            activity.findViewById(R.id.keyitem).setVisibility(GONE);
            activity.findViewById(R.id.keyline).setVisibility(GONE);
        }
    }

    class KeyMap{
        public KeyMap(int keystrid, int keycode,boolean pressed) {
            this.keystrid = keystrid;
            this.keycode = keycode;
            this.pressed = pressed;
        }

        int keystrid;
        int keycode;
        Button button;
        boolean pressed;

        void setButton(Button button){
            this.button = button;
        }

        Button getButton(){
            return button;
        }

    }

    public boolean isKeyTestPass(){
        if (isKeyTest) {return true;}
        for (int i=0;i<keymap.length;i++){
            if(!keymap[i].pressed){
                return false;
            }
        }
        isKeyTest = true;
        return true;
    }


    public boolean KeyViewDispatchKeyEvent(KeyEvent event) {
        for (int i=0;i<keymap.length;i++){
            if(!keymap[i].pressed&&event.getKeyCode()==keymap[i].keycode){
                keymap[i].pressed = true;
                keymap[i].getButton().setBackgroundColor(Color.GREEN);
                return true;
            }
        }
        return false;
    }
}
