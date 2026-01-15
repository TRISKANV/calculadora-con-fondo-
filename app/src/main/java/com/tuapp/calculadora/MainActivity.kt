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
    private var modoRegistro = false
    private var primerNumero = 0.0
    private var operacion = ""
    private var nuevaOperacion = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvDisplay = findViewById<TextView>(R.id.tvDisplay)
        val btnIgual = findViewById<Button>(R.id.btnIgual)
        val btnBorrar = findViewById<Button>(R.id.btnBorrar)
        
        val prefs = getSharedPreferences("BovedaPrefs", Context.MODE_PRIVATE)
        var claveGuardada = prefs.getString("clave_secreta", null)

        if (claveGuardada == null) {
            modoRegistro = true
            tvDisplay.text = "Crea tu PIN"
            tvDisplay.textSize = 30f
        }

        // 1. Números (0-9)
        val botonesIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        botonesIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                if (nuevaOperacion) {
                    inputActual = ""
                    nuevaOperacion = false
                }
                val boton = it as Button
                inputActual += boton.text.toString()
                tvDisplay.text = inputActual
            }
        }

        // 2. Operaciones Matemáticas (Se activan si NO estamos creando un PIN)
        val operacionesIds = mapOf(
            "+" to "sumar", "-" to "restar", "×" to "multiplicar", "÷" to "dividir"
        )
        
        // Como los botones de operación no tienen ID en el XML que te pasé, 
        // los buscamos por su texto (asumiendo que los botones naranja están ahí)
        // Nota: En el XML les pondremos IDs o los buscaremos por su lógica
        
        // 3. Lógica del botón AC (Borrar todo)
        btnBorrar.setOnClickListener {
            inputActual = ""
            primerNumero = 0.0
            operacion = ""
            tvDisplay.text = if (modoRegistro) "Crea tu PIN" else "0"
        }

        // 4. Lógica del botón IGUAL (Matemática + Bóveda)
        btnIgual.setOnClickListener {
            val claveParaComparar = prefs.getString("clave_secreta", null)

            // CASO A: Abrir Bóveda (Si el input coincide con el PIN)
            if (!modoRegistro && inputActual == claveParaComparar) {
                val intent = Intent(this, BovedaActivity::class.java)
                startActivity(intent)
                inputActual = ""
                tvDisplay.text = "0"
            } 
            // CASO B: Guardar PIN nuevo
            else if (modoRegistro) {
                if (inputActual.length == 4) {
                    prefs.edit().putString("clave_secreta", inputActual).apply()
                    Toast.makeText(this, "PIN Guardado", Toast.LENGTH_SHORT).show()
                    modoRegistro = false
                    inputActual = ""
                    tvDisplay.text = "0"
                } else {
                    Toast.makeText(this, "Debe ser de 4 dígitos", Toast.LENGTH_SHORT).show()
                }
            }
            // CASO C: Hacer una cuenta matemática normal (Próxima mejora)
            else {
                // Aquí podrías poner la lógica de calcular, 
                // pero lo más importante es que si no es el PIN, se limpie.
                tvDisplay.text = "0"
                inputActual = ""
            }
        }
    }
}
