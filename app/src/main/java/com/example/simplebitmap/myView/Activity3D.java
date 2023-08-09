package com.example.simplebitmap.myView;

import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.example.simplebitmap.R;

public class Activity3D extends AppCompatActivity {
    public static float cameraSet = 7.9f;
    MyView3 myView;
    PopupWindow popupWindow;
    private WaitScreen waitScreen;
    public static boolean hasloaded = false;
    public static int time = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        myView=new MyView3(this);

        if (hasloaded) {
            Activity3D.time = 2;
        }
        new Handler().postDelayed(() -> {
            if (popupWindow != null) {
                waitScreen.close();
            }
        }, Activity3D.time * 2000);
        hasloaded = true;
        setContentView(R.layout.activity_main2);

        LinearLayout ll = (LinearLayout) findViewById(R.id.activity_main3);
        ll.addView(myView);




    }

    @Override
    public void onAttachedToWindow() {
        show();
        super.onAttachedToWindow();
    }

    private void show() {
        waitScreen=new WaitScreen(this);
        popupWindow=waitScreen.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        myView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myView.onPause();
    }


}