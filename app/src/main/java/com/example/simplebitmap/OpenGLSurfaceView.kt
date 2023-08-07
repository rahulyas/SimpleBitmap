package com.example.simplebitmap

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simplebitmap.PartOfGL.OpenGLRenderer
import com.example.simplebitmap.databinding.ActivityGlsurfaceViewBinding

class OpenGLSurfaceView : AppCompatActivity() {
    private lateinit var glSurfaceView: GLSurfaceView

    private lateinit var binding: ActivityGlsurfaceViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlsurfaceViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val triangleVertices = listOf(
            // Triangle 1
            0.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            // Triangle 2
            // ... (provide vertices for the rest of the triangles)
        )
        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(OpenGLRenderer(listOf(triangleVertices)))
        setContentView(glSurfaceView)

    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

}