package com.example.simplebitmap.TiltedAnimation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.simplebitmap.R;


public class TiltedActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnEmbedDialogFragment, btnDialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilted);

//        TiltedLineView tiltedLineView = findViewById(R.id.tiltedLineView);
//        tiltedLineView.startAnimation();
        btnEmbedDialogFragment = findViewById(R.id.btnEmbedDialogFragment);
        btnDialogFragment = findViewById(R.id.btnDialogFragment);

        btnEmbedDialogFragment.setOnClickListener(this);
        btnDialogFragment.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnEmbedDialogFragment:
                DialogFragment dialogFragment = new DialogFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayout, dialogFragment);
                ft.commit();
                break;

            case R.id.btnDialogFragment:
                dialogFragment = new DialogFragment();
                ft = getSupportFragmentManager().beginTransaction();
                dialogFragment.show(ft, "dialog");
                break;
        }
    }
}