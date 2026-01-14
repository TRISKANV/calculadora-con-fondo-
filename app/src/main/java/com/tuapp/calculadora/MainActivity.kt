package com.tuapp.calculadora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var inputActual = ""
    private val CLAVE_SECRETA = "2580" // Esta es tu clave actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvDisplay = findViewById<TextView>(R.id.tvDisplay)
        val btnIgual = findViewById<Button>(R.id.btnIgual)

        // Lista de IDs de todos tus botones numéricos
        val botonesIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        // Configuramos todos los números automáticamente
        botonesIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                val boton = it as Button
                inputActual += boton.text.toString()
                tvDisplay.text = inputActual
            }
        }

        // Configuración del botón IGUAL (El disparador de la bóveda)
        btnIgual.setOnClickListener {
            if (inputActual == CLAVE_SECRETA) {
                // Si la clave es correcta, abre la bóveda
                val intent = Intent(this, BovedaActivity::class.java)
                startActivity(intent)
                
                // Limpiamos la pantalla por seguridad
                inputActual = ""
                tvDisplay.text = "0"
            } else {
                // Si no es la clave, reinicia como una calculadora normal
                // (Aquí podrías añadir la lógica de suma/resta después)
                tvDisplay.text = "0"
                inputActual = ""
            }
        }
    }
}
