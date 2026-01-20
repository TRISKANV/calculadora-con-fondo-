package com.tuapp.calculadora

import java.util.UUID

data class Nota(
    val id: String = UUID.randomUUID().toString(),
    var titulo: String,
    var contenido: String,
    val fecha: Long = System.currentTimeMillis()
)
