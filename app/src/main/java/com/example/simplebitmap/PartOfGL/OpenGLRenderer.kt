package com.example.simplebitmap.PartOfGL

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer(private val triangles: List<List<Float>>) : GLSurfaceView.Renderer {

    private val triangleList: List<Triangle>

    init {
        triangleList = triangles.map { vertices -> Triangle(vertices) }
    }


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background color of the OpenGL surface
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // Adjust the OpenGL viewport based on the new surface size
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        triangleList.forEach { it.draw() }
    }

}
