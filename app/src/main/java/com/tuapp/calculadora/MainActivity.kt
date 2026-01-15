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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvDisplay = findViewById<TextView>(R.id.tvDisplay)
        val btnIgual = findViewById<Button>(R.id.btnIgual)
        
        // 1. Revisar si ya existe una clave guardada
        val prefs = getSharedPreferences("BovedaPrefs", Context.MODE_PRIVATE)
        val claveGuardada = prefs.getString("clave_secreta", null)

        if (claveGuardada == null) {
            modoRegistro = true
            tvDisplay.text = "Crea tu PIN"
            tvDisplay.textSize = 40f // Un poco más pequeño para que quepa el texto
        }

        // 2. Configurar botones numéricos (0-9)
        val botonesIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        botonesIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                if (inputActual.length < 4) { // Limitamos a 4 dígitos
                    val boton = it as Button
                    inputActual += boton.text.toString()
                    tvDisplay.text = inputActual
                }
            }
        }

        // 3. Lógica del botón IGUAL
        btnIgual.setOnClickListener {
            if (modoRegistro) {
                if (inputActual.length == 4) {
                    // Guardar la nueva clave
                    prefs.edit().putString("clave_secreta", inputActual).apply()
                    Toast.makeText(this, "PIN Guardado. Ingrésalo ahora.", Toast.LENGTH_SHORT).show()
                    
                    // Reiniciar para que ahora entre en modo normal
                    modoRegistro = false
                    inputActual = ""
                    tvDisplay.text = "0"
                } else {
                    Toast.makeText(this, "El PIN debe ser de 4 dígitos", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Modo normal: Comprobar clave para entrar a la bóveda
                if (inputActual == claveGuardada) {
                    val intent = Intent(this, BovedaActivity::class.java)
                    startActivity(intent)
                    inputActual = ""
                    tvDisplay.text = "0"
                } else {
                    // Si falla, actúa como calculadora (limpia pantalla)
                    inputActual = ""
                    tvDisplay.text = "0"
                    Toast.makeText(this, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
