package com.tuapp.calculadora

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvPantalla: TextView
    private var entradaActual = ""
    private var esPrimeraVez = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvPantalla = findViewById(R.id.tvPantalla)

        // Revisar si ya existe una contraseña guardada
        val prefs = getSharedPreferences("Seguridad", Context.MODE_PRIVATE)
        val contraseñaGuardada = prefs.getString("clave", null)

        if (contraseñaGuardada == null) {
            esPrimeraVez = true
            tvPantalla.text = "CREAR CLAVE"
            tvPantalla.textSize = 30f // Achicamos un poco el texto para que quepa
        }

        // Configurar botones numéricos
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
                tvPantalla.textSize = 70f // Volvemos al tamaño grande
            }
        }

        // Botón IGUAL
        findViewById<Button>(R.id.btnIgual).setOnClickListener {
            val claveActual = prefs.getString("clave", null)

            if (esPrimeraVez) {
                // GUARDAR LA CLAVE POR PRIMERA VEZ
                if (entradaActual.length >= 4) {
                    prefs.edit().putString("clave", entradaActual).apply()
                    Toast.makeText(this, "Contraseña Guardada", Toast.LENGTH_SHORT).show()
                    esPrimeraVez = false
                    entradaActual = ""
                    tvPantalla.text = "0"
                } else {
                    Toast.makeText(this, "Mínimo 4 números", Toast.LENGTH_SHORT).show()
                }
            } else {
                // VERIFICAR CLAVE NORMAL
                if (entradaActual == claveActual) {
                    val intent = Intent(this, BovedaActivity::class.java)
                    startActivity(intent)
                }
                entradaActual = ""
                tvPantalla.text = "0"
            }
        }
    }
}
