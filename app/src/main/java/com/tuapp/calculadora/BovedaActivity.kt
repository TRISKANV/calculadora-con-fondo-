package com.tuapp.calculadora

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BovedaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val vistaSecreta = TextView(this)
        vistaSecreta.text = "¡Bóveda Abierta! Aquí irán tus fotos."
        vistaSecreta.textSize = 24f
        vistaSecreta.setTextColor(android.graphics.Color.WHITE)
        setContentView(vistaSecreta)
    }
}
