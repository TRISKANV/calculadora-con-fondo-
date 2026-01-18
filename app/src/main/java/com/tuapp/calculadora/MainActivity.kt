package com.tuapp.calculadora

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var tvExpresion: TextView
    private lateinit var tvResultado: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvExpresion = findViewById(R.id.tvExpresion)
        tvResultado = findViewById(R.id.tvResultado)

        // IMPORTANTE: Primero configuramos todos los botones EXCEPTO el igual
        val root = findViewById<ViewGroup>(R.id.rootLayout)
        configurarBotonesSimples(root)

        // Luego configuramos el botón IGUAL con su lógica de contraseña
        configurarBotonIgual()
    }

    private fun configurarBotonesSimples(view: View) {
        if (view is Button) {
            // Solo si NO es el botón igual, le asignamos que escriba texto
            if (view.id != R.id.btnIgual) {
                view.setOnClickListener { 
                    val botonTexto = view.text.toString()
                    alPresionarBoton(botonTexto)
                }
            }
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                configurarBotonesSimples(view.getChildAt(i))
            }
        }
    }

    private fun configurarBotonIgual() {
        val btnIgual = findViewById<Button>(R.id.btnIgual)
        val prefs = getSharedPreferences("DatosSecretos", Context.MODE_PRIVATE)

        btnIgual?.setOnClickListener {
            val entrada = tvExpresion.text.toString()
            val passGuardada = prefs.getString("clave", null)

            if (passGuardada == null) {
                // PRIMERA VEZ: REGISTRO
                if (entrada.length >= 4 && !entrada.contains(Regex("[+/*×÷-]"))) {
                    prefs.edit().putString("clave", entrada).apply()
                    Toast.makeText(this, "✅ CLAVE CONFIGURADA: $entrada", Toast.LENGTH_LONG).show()
                    tvExpresion.text = ""
                    tvResultado.text = "0"
                } else {
                    Toast.makeText(this, "Escribí 4 números (sin signos) y dale a =", Toast.LENGTH_SHORT).show()
                }
            } else if (entrada == passGuardada) {
                // ACCESO A BÓVEDA
                val intent = Intent(this, BovedaActivity::class.java)
                startActivity(intent)
            } else {
                // CÁLCULO NORMAL
                ejecutarCalculo()
            }
        }
    }

    private fun alPresionarBoton(valor: String) {
        when (valor) {
            "AC" -> {
                tvExpresion.text = ""
                tvResultado.text = "0"
            }
            "DEL" -> {
                val s = tvExpresion.text.toString()
                if (s.isNotEmpty()) tvExpresion.text = s.dropLast(1)
            }
            "×" -> tvExpresion.append("*")
            "÷" -> tvExpresion.append("/")
            "π" -> tvExpresion.append("3.14159")
            "e" -> tvExpresion.append("2.71828")
            "sin", "cos", "tan", "log", "ln" -> tvExpresion.append("$valor(")
            else -> tvExpresion.append(valor)
        }
    }

    private fun ejecutarCalculo() {
        val texto = tvExpresion.text.toString()
        if (texto.isEmpty()) return
        
        try {
            val expresion = ExpressionBuilder(texto.replace("×", "*").replace("÷", "/")).build()
            val res = expresion.evaluate()
            val resLong = res.toLong()
            if (res == resLong.toDouble()) {
                tvResultado.text = resLong.toString()
            } else {
                tvResultado.text = String.format("%.4f", res)
            }
        } catch (e: Exception) {
            tvResultado.text = "Error"
        }
    }
}
