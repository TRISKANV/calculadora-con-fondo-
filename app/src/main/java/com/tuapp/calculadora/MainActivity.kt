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
        // 1. Inflamos la vista primero
        setContentView(R.layout.activity_main)

        // 2. Referenciamos los TextViews (Asegurate que estos IDs existan en el XML)
        tvExpresion = findViewById(R.id.tvExpresion)
        tvResultado = findViewById(R.id.tvResultado)

        // 3. Configuramos los botones con un Try/Catch para que no se congele si algo falla
        try {
            configurarBotones()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar botones", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarBotones() {
        val prefs = getSharedPreferences("DatosSecretos", Context.MODE_PRIVATE)

        // Buscamos el contenedor raíz (rootLayout)
        val root = findViewById<ViewGroup>(R.id.rootLayout)
        
        // Función para recorrer todos los botones y asignarles el click
        asignarClicksRecursivo(root)

        // Lógica específica para el botón IGUAL (ID: btnIgual)
        findViewById<Button>(R.id.btnIgual)?.setOnClickListener {
            val entrada = tvExpresion.text.toString()
            val passGuardada = prefs.getString("clave", null)

            if (passGuardada == null) {
                // Registro (mínimo 4 números)
                if (entrada.length >= 4 && !entrada.contains(Regex("[+/*-]"))) {
                    prefs.edit().putString("clave", entrada).apply()
                    Toast.makeText(this, "Clave Guardada", Toast.LENGTH_LONG).show()
                    tvExpresion.text = ""
                } else {
                    Toast.makeText(this, "Escribí 4 números para tu clave", Toast.LENGTH_SHORT).show()
                }
            } else if (entrada == passGuardada) {
                // Abrir Bóveda
                try {
                    val intent = Intent(this, BovedaActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: No se encontró la Bóveda", Toast.LENGTH_SHORT).show()
                }
            } else {
                ejecutarCalculo()
            }
        }
    }

    private fun asignarClicksRecursivo(view: View) {
        if (view is Button) {
            // Evitamos que el botón IGUAL se configure aquí para no pisar su lógica especial
            if (view.id != R.id.btnIgual) {
                view.setOnClickListener { alPresionarBoton(view.text.toString()) }
            }
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                asignarClicksRecursivo(view.getChildAt(i))
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
