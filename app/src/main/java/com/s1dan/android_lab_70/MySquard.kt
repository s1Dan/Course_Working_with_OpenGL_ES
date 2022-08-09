package com.s1dan.android_lab_70

import com.s1dan.android_lab_70.MySquare
import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MySquare {
    private val mProgram: Int
    private val vertexShaderCode = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            " gl_Position = uMVPMatrix * vPosition;" +
            "}"
    private val fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "gl_FragColor = vColor;" +
            "}"
    private var positionHandle = 0
    private var colorHandle = 0
    private val vertexBuffer: FloatBuffer
    private val drawListBuffer: ShortBuffer
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) //порядок рисования
    var color = floatArrayOf(0.0f, 0.5f, 0.5f, 1.0f)
    private val vertexCount = squareCoords.size / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4
    fun draw() {
        // Добавляем программу в среду OpenGL ES
        GLES20.glUseProgram(mProgram)
        // Получаем элемент vPosition вершинного шейдера
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
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
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")
        // Устанавливаем цвет для рисования треугольника
        GLES20.glUniform4fv(colorHandle, 1, color, 0)
        // Рисуем треугольник
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        // Отключаем массив вершин
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    companion object {
        const val COORDS_PER_VERTEX = 3
        var squareCoords = floatArrayOf(
            -0.5f, 0.5f, 0.0f,  // верхняя левая вершина
            -0.5f, -0.5f, 0.0f,  // нижняя левая вершина
            0.5f, -0.5f, 0.0f,  // нижняя правая вершина
            0.5f, 0.5f, 0.0f) // верхняя правая вершина
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
        val vertexShader: Int = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int =
            MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // создание пустой OpenGL ES программы
        mProgram = GLES20.glCreateProgram()
        // Добавление вершинного шейдера в программу
        GLES20.glAttachShader(mProgram, vertexShader)
        // Добавление фрагментного шейдера в программу
        GLES20.glAttachShader(mProgram, fragmentShader)
        // Связывание программы
        GLES20.glLinkProgram(mProgram)
        //
    }
}