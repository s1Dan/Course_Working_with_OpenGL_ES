package com.s1dan.android_lab_70

import android.content.Context
import android.opengl.GLSurfaceView

class MyGLSurfaceView(context: Context?) : GLSurfaceView(context) {
    init {
        setEGLContextClientVersion(2)
    }
}