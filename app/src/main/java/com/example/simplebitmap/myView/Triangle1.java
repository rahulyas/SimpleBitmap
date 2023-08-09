package com.example.simplebitmap.myView;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.simplebitmap.myView.firstActivity3D.list1;
import static com.example.simplebitmap.myView.firstActivity3D.triangleList;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import io.github.jdiemke.triangulation.Triangle2D;

public class Triangle1 {

    public float xAngle=0;//The angle of rotation around the x-axis
    public float yAngle=0;//The angle of rotation around the x-axis

    private int mProgram;//Custom rendering pipeline program id
    private int muMVPMatrixHandle;//Total transformation matrix reference id
    private int maPositionHandle; //Vertex position attribute reference id
    private int gcolorHandle;//Color attribute reference id

    private FloatBuffer mVertexBuffer;//Vertex coordinate data buffer
    private int vCount=0;

    static List<Triangle2D> trianglelist;
    public static boolean triangleFlag = false;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private final int COORDS_PER_VERTEX = 3;
    float[] vertices;
    private int colorHandle = 0;
    private float[] color = {0.0f, 0.0f, 1.0f, 1.0f}; // Blue color

    public Triangle1(MyView3 mv3) {
        //Initialize vertex coordinates and shading data
        initVertexData();
        //Initialize the shader
        initShader(mv3);
    }

    private void initVertexData()
    {
        //Initialization of vertex coordinate data
//        List<Double> sourcelist = new_finallist;
        List<Float> list;
        if (!triangleFlag){
            long startTime = System.currentTimeMillis();   //get start time
            trianglelist = triangleList;
            Log.d("TAG", "inittrianglelist "+trianglelist);
            long endTime=System.currentTimeMillis(); //get end time
            System.out.println("Triangulation runtime: "+(startTime-endTime)+"ms");
            triangleFlag = true;
        }
        System.out.println("cut out"+trianglelist.size()+"triangles");
        list = list1;

        Log.d("TAG", "initTriangle: "+list);
        vCount=list.size()/3;
        vertices =  new float[list.size()];
        //Point Cloud Reduction Algorithm
        for (int i = 0; i < list.size(); i++) {
            vertices[i] = list.get(i);
        }
        Log.d(TAG, "initVertexData: vertices.length ="+vertices.length +"==" +vertices.length*4);
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

    }

    //Initialize the shader
    private void initShader(MyView3 mv)
    {
        //Load the script content of the vertex shader
        String mVertexShader = ShaderUtil.loadFromAssertsFile("vertex2.sh", mv.getResources());
//        String mVertexShader = String.valueOf(loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode));
        //Load the script content of the fragment shader
        String mFragmentShader = ShaderUtil.loadFromAssertsFile("frag2.sh", mv.getResources());
//        String mFragmentShader = String.valueOf(loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode));
        //Create programs based on vertex shaders and fragment shaders
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
/*        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, Integer.parseInt(mVertexShader));
        GLES20.glAttachShader(mProgram, Integer.parseInt(mFragmentShader));
        GLES20.glLinkProgram(mProgram);*/

        //Get the vertex position attribute reference id in the program
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        gcolorHandle = GLES20.glGetUniformLocation(mProgram, "gcolor");

    }

    public void drawSelf()
    {
        MatrixState.rotate(xAngle, 1, 0, 0);//Rotate around the X axis
        MatrixState.rotate(yAngle, 0, 0, 1);//Rotate around the Y axis

        //Formulate and use a certain shader program
        GLES20.glUseProgram(mProgram);
        //pass value to total transformation matrix
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //Specify vertex position data for brushes
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3*4, mVertexBuffer);

        GLES20.glUniform4f(gcolorHandle, 1f, 1f, 1f, 1.0f);
        //Allows arrays of vertex position data
        GLES20.glEnableVertexAttribArray(maPositionHandle);

        GLES20.glDrawArrays(GLES20.GL_LINES,0, vCount);
    }


}
