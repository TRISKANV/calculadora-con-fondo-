package com.tuapp.calculadora

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var inputActual = ""
    private var primerNumero = 0.0
    private var operacionActual = ""
    private var modoRegistro = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvDisplay = findViewById<TextView>(R.id.tvDisplay)
        val btnIgual = findViewById<Button>(R.id.btnIgual)
        val btnBorrar = findViewById<Button>(R.id.btnBorrar)
        
        val prefs = getSharedPreferences("BovedaPrefs", Context.MODE_PRIVATE)

        // 1. Botones numéricos
        val botonesIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )
        botonesIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                val boton = it as Button
                inputActual += boton.text.toString()
                tvDisplay.text = inputActual
            }
        }

        // 2. Botones de operación
        val opBotones = listOf(R.id.btnSuma, R.id.btnResta, R.id.btnMulti, R.id.btnDiv)
        opBotones.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                val boton = it as Button
                if (inputActual.isNotEmpty()) {
                    primerNumero = inputActual.toDouble()
                    operacionActual = boton.text.toString()
                    inputActual = ""
                    tvDisplay.text = "0"
                }
            }
        }

        // 3. Botón Borrar (AC)
        btnBorrar.setOnClickListener {
            inputActual = ""
            primerNumero = 0.0
            operacionActual = ""
            tvDisplay.text = "0"
        }

        // 4. Botón Igual (La magia)
        btnIgual.setOnClickListener {
            val claveGuardada = prefs.getString("clave_secreta", null)

            // SI ES LA CLAVE SECRETA: Abre la bóveda
            if (inputActual == claveGuardada && operacionActual == "") {
                val intent = Intent(this, BovedaActivity::class.java)
                startActivity(intent)
                inputActual = ""
                tvDisplay.text = "0"
            } 
            // SI ESTAMOS EN MODO REGISTRO:
            else if (claveGuardada == null && inputActual.length == 4) {
                prefs.edit().putString("clave_secreta", inputActual).apply()
                Toast.makeText(this, "PIN Guardado", Toast.LENGTH_SHORT).show()
                inputActual = ""
                tvDisplay.text = "0"
            }
            // SI ES UNA OPERACIÓN MATEMÁTICA:
            else if (inputActual.isNotEmpty() && operacionActual.isNotEmpty()) {
                val segundoNumero = inputActual.toDouble()
                var resultado = 0.0
                when (operacionActual) {
                    "+" -> resultado = primerNumero + segundoNumero
                    "-" -> resultado = primerNumero - segundoNumero
                    "×" -> resultado = primerNumero * segundoNumero
                    "÷" -> resultado = if (segundoNumero != 0.0) primerNumero / segundoNumero else 0.0
                }
                tvDisplay.text = resultado.toString()
                inputActual = resultado.toString()
                operacionActual = ""
            }
        }
    }
}
