package com.example.simplebitmap.myView;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ShaderUtil {
    //Load the specified shader
    private static int loadShader(int shaderType, String source){
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0){
            GLES20.glShaderSource(shader,source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0)
            {
                Log.e("ES20_ERROR", "Could not compile shader " + shaderType + ":");
                Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }
    //Methods of creating shader programs
    public static int createProgram(String vertexSource, String fragmentSource){
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0){
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0)
        {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        if(program != 0){
            //Adding a vertex shader to the program
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            //Adding a fragment shader to the program
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            //linker
            GLES20.glLinkProgram(program);
            //An array storing the number of successfully linked programs
            int[] linkStatus = new int[1];
            //Get the link status of the program
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            //If the link fails, report an error and delete the program
            if (linkStatus[0] != GLES20.GL_TRUE)
            {
                Log.e("ES20_ERROR", "Could not link program: ");
                Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
    private static void checkGlError(String op){
        int error;
        if((error = GLES20.glGetError()) !=GLES20.GL_NO_ERROR){
            Log.e("ES20_ERROR", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
    //Method to load shader content from sh script
    public static String loadFromAssertsFile(String fname, Resources r){
        String result = null;
        try {
            InputStream in = r.getAssets().open(fname);
            int ch;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch=in.read())!=-1){
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            in.close();
            result=new String(buff,"UTF-8");
            result = result.replaceAll("\\r\\n","\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}

