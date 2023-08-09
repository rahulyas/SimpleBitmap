package com.example.simplebitmap.myView;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MatrixState {
    private static float[] mProjMatrix = new float[16];//4x4 matrix for projection
    private static float[] mVMatrix = new float[16];//Camera position orientation 9 parameter matrix
    private static float[] currMatrix;//The movement and rotation matrix of the specific object, rotation, translation
    private static float[] lightLocation=new float[]{0,0,0};//Locating the position of the light source
    public static FloatBuffer lightPositionFB;
    public static FloatBuffer cameraFB;
    //Protects the stack of transformation matrices
    private static float[][] mStack=new float[10][16];
    private static int stackTop=-1;

    //Get the initial matrix without transformation
    public static void setInitMatrix() {
        currMatrix=new float[16];
        Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
    }
    //Set to move along the xyz axis
    public static void translate(float x,float y,float z) {
        Matrix.translateM(currMatrix, 0, x, y, z);
    }
    //Set to move around the xyz axis
    public static void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(currMatrix,0,angle,x,y,z);
    }
    //protection transformation matrix
    public static void pushMatrix(){
        stackTop++;
        System.arraycopy(currMatrix, 0, mStack[stackTop], 0, 16);
    }
    //restore transformation matrix
    public static void popMatrix(){
        System.arraycopy(mStack[stackTop], 0, currMatrix, 0, 16);
        stackTop--;
    }

    //set up the camera
    private static ByteBuffer llbb= ByteBuffer.allocateDirect(3*4);
    private static float[] cameraLocation=new float[3];//camera position
    public static void setCamera(float cx, float cy, float cz, float tx, float ty, float tz, float upx, float upy, float upz)
    {
        Matrix.setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
        cameraLocation[0]=cx;
        cameraLocation[1]=cy;
        cameraLocation[2]=cz;

        llbb.clear();
        llbb.order(ByteOrder.nativeOrder());//set byte order
        cameraFB=llbb.asFloatBuffer();
        cameraFB.put(cameraLocation);
        cameraFB.position(0);
    }

    //Set perspective projection parameters
    public static void setProjectFrustum(float left, float right, float bottom, float top, float near, float far)
    {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //How to set the light position
    private static ByteBuffer llbbL = ByteBuffer.allocateDirect(3*4);
    public static void setLightLocation(float x, float y, float z)
    {
        llbbL.clear();
        lightLocation[0]=x;lightLocation[1]=y;lightLocation[2]=z;
        llbbL.order(ByteOrder.nativeOrder());//set byte order
        lightPositionFB=llbbL.asFloatBuffer();
        lightPositionFB.put(lightLocation);
        lightPositionFB.position(0);
    }

    //Get the transformation matrix of a specific object
    public static float[] getMMatrix()
    {
        return currMatrix;
    }

    //Get the total transformation matrix of a specific object
    private static float[] mMVPMatrix=new float[16];
    public static float[] getFinalMatrix()
    {
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }
}
