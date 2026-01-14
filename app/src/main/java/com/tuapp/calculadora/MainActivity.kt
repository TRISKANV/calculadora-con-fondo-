package com.tuapp.calculadora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var inputActual = ""
    private val CLAVE_SECRETA = "2580" // CAMBIA TU CLAVE AQUÍ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvDisplay = findViewById<TextView>(R.id.tvDisplay)
        val btnIgual = findViewById<Button>(R.id.btnIgual)
        
        // Ejemplo para un botón numérico
        val btn7 = findViewById<Button>(R.id.btn7)
        btn7.setOnClickListener {
            inputActual += "7"
            tvDisplay.text = inputActual
        }

        btnIgual.setOnClickListener {
            if (inputActual == CLAVE_SECRETA) {
                // Abre la galería secreta
                val intent = Intent(this, BovedaActivity::class.java)
                startActivity(intent)
            } else {
                // Aquí iría la lógica de suma/resta normal
                tvDisplay.text = "Error" 
                inputActual = ""
            }
        }
    }
}
