package com.example.simplebitmap.PartOfGL

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer



class Triangle(private val vertices: List<Float>) {
    private val vertexBuffer: FloatBuffer
    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private val color = floatArrayOf(0.0f, 0.0f, 1.0f, 1.0f) // Blue color

    private var program: Int = 0

    private var positionHandle: Int = 0
    private var colorHandle: Int = 0
    private val COORDS_PER_VERTEX = 3

    init {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
        val buffer = ByteBuffer.allocateDirect(vertices.size * 4)
        buffer.order(ByteOrder.nativeOrder())
        vertexBuffer = buffer.asFloatBuffer()
        vertexBuffer.put(vertices.toFloatArray())
        vertexBuffer.position(0)
    }

    fun draw() {
        val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
        GLES20.glUseProgram(program)
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )
            colorHandle = GLES20.glGetUniformLocation(program, "vColor").also { location ->
                GLES20.glUniform4fv(location, 1, color, 0)
            }
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.size / 3)
            GLES20.glDisableVertexAttribArray(it)
        }

    }
    fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

}