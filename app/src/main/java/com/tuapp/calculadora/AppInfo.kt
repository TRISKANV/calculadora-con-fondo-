package com.tuapp.calculadora

import android.graphics.drawable.Drawable

data class AppInfo(
    val nombre: String,
    val packageName: String,
    val icono: Drawable,
    var estaBloqueada: Boolean = false
)
