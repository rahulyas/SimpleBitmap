package com.example.simplebitmap.TiltedAnimation;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.simplebitmap.R;

import java.util.ArrayList;
import java.util.Arrays;

public class DialogFragment extends androidx.fragment.app.DialogFragment {
    ArrayList<Float> values = new ArrayList<Float>(Arrays.asList(90.0f,220.0f,110.0f,160.0f,123.0f,135.0f,140.0f));
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);
        TiltedLineView tiltedLineView = view.findViewById(R.id.tiltedLineView);
        for(int i=0 ; i<values.size();i++) {
            int finalI = i;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tiltedLineView.startAnimation(values.get(finalI));
                }
            },500);
        }
        return view;
    }
}