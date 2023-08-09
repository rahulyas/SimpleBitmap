package com.example.simplebitmap.myView;

import android.app.ActionBar;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;

import com.example.simplebitmap.R;


public class WaitScreen {

    private PopupWindow popupWindow;
    private Context context;
    private View view;
    public WaitScreen(Context context) {
        this.context=context;
        view= LayoutInflater.from(context).inflate(R.layout.poplayout, null);
        popupWindow=new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT,  ActionBar.LayoutParams.MATCH_PARENT);
    }


    public PopupWindow show() {
        popupWindow.showAsDropDown(view);
        return popupWindow;
    }


    public void close() {
        if (popupWindow!=null&&popupWindow.isShowing()) {
            popupWindow.setFocusable(false);
            new Handler().postDelayed(() -> popupWindow.dismiss(), 1000);
            View progressBar1=view.findViewById(R.id.progressBar1);
            progressBar1.setVisibility(View.GONE);
            View left=view.findViewById(R.id.left);
            View right=view.findViewById(R.id.right);
            TranslateAnimation leftAnimation= new TranslateAnimation(0,-left.getWidth(),0f,0f);
            leftAnimation.setDuration(1000);//Set animation duration to 3 seconds
            leftAnimation.setInterpolator(context, android.R.anim.overshoot_interpolator);//Set up animation inserter
            leftAnimation.setFillAfter(true);//Keep the current position after the animation ends (that is, do not return to the position before the animation started)
            left.startAnimation(leftAnimation);
            TranslateAnimation rightAnimation= new TranslateAnimation(0f,left.getWidth(),0f,0f);
            rightAnimation.setDuration(1000);//Set animation duration to 3 seconds
            rightAnimation.setInterpolator(context, android.R.anim.overshoot_interpolator);//Set up animation inserter
            rightAnimation.setFillAfter(true);//Keep the current position after the animation ends (that is, do not return to the position before the animation started)
            right.startAnimation(rightAnimation);
        }
    }


    public void dismiss() {
        if (popupWindow!=null&&popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}

