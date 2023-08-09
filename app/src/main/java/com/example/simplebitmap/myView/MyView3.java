package com.example.simplebitmap.myView;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyView3 extends GLSurfaceView {

    SceneRenderer3 mRenderer;
    Triangle1 triangle1;

    private final float TOUCH_SCALE_FACTOR = 180.0f/320;//Angle scaling
    private float mPreviousY;//The Y coordinate of the last touch position
    private float mPreviousX;//The X coordinate of the last touch position

    public MyView3(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new SceneRenderer3();
        this.setRenderer(mRenderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public MyView3(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //Touch event callback method
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;//Calculate the stylus Y displacement
                float dx = x - mPreviousX;//Calculate stylus X displacement
                triangle1.yAngle+= dx * TOUCH_SCALE_FACTOR;
                triangle1.xAngle += dy * TOUCH_SCALE_FACTOR;
        }
        mPreviousY = y;//Record stylus position
        mPreviousX = x;//Record stylus position
        return true;
    }

    private class SceneRenderer3 implements Renderer{

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //Set the screen background color RGBA
            GLES20.glClearColor(0f,0f,0f,1.0f);
            triangle1 = new Triangle1(MyView3.this);
            //Turn on deep detection
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //Set window size and position
            GLES20.glViewport(0, 0, width, height);
            //Calculate aspect ratio of GLSurfaceView
            float ratio = (float) width / height;
            //Call this method to calculate the perspective projection matrix
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 15);
            // Call this method to generate camera 9 parameter position matrix
            MatrixState.setCamera(0, 0f, Activity3D.cameraSet,  0f, 0f, 0f,  0f, 1.0f, 0.0f);

            MatrixState.setInitMatrix();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //Clear depth buffer and color buffer
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //protect the scene
            MatrixState.pushMatrix();

            //draw triangle pairs
            MatrixState.pushMatrix();
            triangle1.drawSelf();
            MatrixState.popMatrix();

            //recovery site
            MatrixState.popMatrix();
        }
    }
}
