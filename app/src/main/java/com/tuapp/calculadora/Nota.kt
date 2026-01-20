package com.tuapp.calculadora

import java.text.SimpleDateFormat
import java.util.*

data class Nota(
    val id: String = UUID.randomUUID().toString(),
    var contenido: String,
    val fecha: Long = System.currentTimeMillis()
) {
    fun getFechaFormateada(): String {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        return sdf.format(Date(fecha))
    }
}
