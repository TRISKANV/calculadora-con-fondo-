package com.tuapp.calculadora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tuapp.calculadora.R

class MainActivity : AppCompatActivity() {

    private lateinit var tvPantalla: TextView
    private var entradaActual = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvPantalla = findViewById(R.id.tvPantalla)

        // Configurar todos los botones numéricos
        val idsNumeros = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        for (id in idsNumeros) {
            findViewById<Button>(id).setOnClickListener {
                val boton = it as Button
                if (entradaActual == "0") entradaActual = ""
                entradaActual += boton.text.toString()
                tvPantalla.text = entradaActual
            }
        }

        // Botón IGUAL (Es el que valida la clave)
        findViewById<Button>(R.id.btnIgual).setOnClickListener {
            // CAMBIA "1234" por la clave que prefieras
            if (entradaActual == "1234") {
                val intent = Intent(this, BovedaActivity::class.java)
                startActivity(intent)
            }
            // Siempre limpiamos al dar igual para que parezca una calculadora normal
            entradaActual = ""
            tvPantalla.text = "0"
        }
    }
}
