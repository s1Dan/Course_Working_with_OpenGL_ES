package com.s1dan.android_lab_70

import com.s1dan.android_lab_70.MyTriangle
import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class MyTriangle {
    private val mProgram: Int
    private val vertexShaderCode = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            " gl_Position = uMVPMatrix * vPosition;" +
            "}"
    private var vPMatrixHandle = 0
    private val fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "gl_FragColor = vColor;" +
            "}"
    private val vertexBuffer: FloatBuffer
    var color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)
    private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4
    private var positionHandle = 0
    private var colorHandle = 0

    // 4 байта на вершину
    fun draw(mvpMatrix: FloatArray?) {
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
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        // Отключаем массив вершин
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    companion object {
        const val COORDS_PER_VERTEX = 3
        var triangleCoords = floatArrayOf(0.0f,
            0.622008459f,
            0.0f,
            -0.5f,
            -0.311004243f,
            0.0f,
            0.5f,
            -0.311004243f,
            0.0f)
    }

    init {
        //инициализируем буфер байтов вершин для координат фигуры; 4 байта на одно значение float
        val bb = ByteBuffer.allocateDirect(triangleCoords.size *
                4)
        bb.order(ByteOrder.nativeOrder())
        // создаем буфер вершин из ByteBuffer
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(triangleCoords)
        //        ColorBuffer.put(color);
        vertexBuffer.position(0)
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
    }
}