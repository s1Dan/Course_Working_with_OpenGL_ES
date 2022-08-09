package com.s1dan.android_lab_70

import com.s1dan.android_lab_70.MyGLRenderer.Companion.loadShader
import com.s1dan.android_lab_70.MyCube
import android.opengl.GLES20
import com.s1dan.android_lab_70.MyGLRenderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MyCube {
    private val mProgramm: Int
    private val vertexBuffer: FloatBuffer
    private val drawListBuffer: ShortBuffer
    private val vertexShaderCode = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            " gl_Position = uMVPMatrix * vPosition;" +  //                    "gl_PointSize = 10.0" +
            "}"
    private var vPMatrixHandle = 0
    private val fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "gl_FragColor = vColor;" +
            "}"
    private val drawOrder = shortArrayOf(
        0, 1, 2, 0, 2, 3,
        4, 5, 6, 4, 6, 7,
        7, 6, 1, 7, 1, 0,
        1, 6, 5, 1, 6, 2,
        3, 2, 5, 3, 5, 4,
        7, 0, 3, 7, 3, 4)
    var color = floatArrayOf(0.0f, 0.7f, 0.7f, 1.0f)
    private var positionHandle = 0
    private var colorHandle = 0
    private val vertexCount = squareCoords.size / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4
    fun draw(mvpMatrix: FloatArray?) {
        // Добавляем программу в среду OpenGL ES
        GLES20.glUseProgram(mProgramm)
        // Получаем элемент vPosition вершинного шейдера
        positionHandle = GLES20.glGetAttribLocation(mProgramm, "vPosition")
        // Подключаем массив вершин
        GLES20.glEnableVertexAttribArray(positionHandle)
        // Подготовка координат вершин треугольника
        GLES20.glVertexAttribPointer(positionHandle,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer)
        // Получаем элемент vColor фрагментного шейдера
        colorHandle = GLES20.glGetUniformLocation(mProgramm, "vColor")
        // Устанавливаем цвет для рисования треугольника
        GLES20.glUniform4fv(colorHandle, 1, color, 0)
        // Рисуем треугольник
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgramm, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT, drawListBuffer)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount)
        // Отключаем массив вершин
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    companion object {
        const val COORDS_PER_VERTEX = 3
        var squareCoords = floatArrayOf(
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f)
    }

    init {
        val bb = ByteBuffer.allocateDirect(squareCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(squareCoords)
        vertexBuffer.position(0)
        val dlb = ByteBuffer.allocateDirect(drawOrder.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(drawOrder)
        drawListBuffer.position(0)
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        mProgramm = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgramm, vertexShader)
        GLES20.glAttachShader(mProgramm, fragmentShader)
        GLES20.glLinkProgram(mProgramm)
    }
}