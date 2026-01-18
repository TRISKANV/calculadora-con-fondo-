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

        // --- LÓGICA DE BIENVENIDA ---
        val prefs = getSharedPreferences("DatosSecretos", Context.MODE_PRIVATE)
        val passGuardada = prefs.getString("clave", null)

        // Si no hay clave en la memoria, mostramos el mensaje inicial
        if (passGuardada == null) {
            tvExpresion.text = "Crea tu contraseña"
        }

        val root = findViewById<ViewGroup>(R.id.rootLayout)
        configurarTodosLosBotones(root)
    }

    private fun configurarTodosLosBotones(view: View) {
        val prefs = getSharedPreferences("DatosSecretos", Context.MODE_PRIVATE)

        if (view is Button) {
            val texto = view.text.toString()

            if (texto == "=") {
                view.setOnClickListener {
                    val entrada = tvExpresion.text.toString()
                    val passGuardada = prefs.getString("clave", null)

                    if (passGuardada == null) {
                        // Registro por primera vez
                        if (entrada.length >= 4 && !entrada.contains(Regex("[+/*×÷-]"))) {
                            prefs.edit().putString("clave", entrada).apply()
                            Toast.makeText(this, "✅ CLAVE GUARDADA", Toast.LENGTH_LONG).show()
                            tvExpresion.text = ""
                            tvResultado.text = "0"
                        } else {
                            Toast.makeText(this, "Escribí 4 números y tocá =", Toast.LENGTH_SHORT).show()
                        }
                    } else if (entrada == passGuardada) {
                        // Entrada a la Bóveda
                        val intent = Intent(this, BovedaActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Si no es la clave, es una cuenta normal
                        ejecutarCalculo()
                    }
                }
            } else {
                view.setOnClickListener { alPresionarBoton(texto) }
            }
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                configurarTodosLosBotones(view.getChildAt(i))
            }
        }
    }

    private fun alPresionarBoton(valor: String) {
        // Si el texto de "Crea tu contraseña" está visible, lo borramos al empezar a escribir
        if (tvExpresion.text.toString() == "Crea tu contraseña") {
            tvExpresion.text = ""
        }

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
        var texto = tvExpresion.text.toString()
        if (texto.isEmpty() || texto == "Crea tu contraseña") return
        
        try {
            texto = texto.replace("×", "*").replace("÷", "/")
            val expresion = ExpressionBuilder(texto).build()
            val res = expresion.evaluate()
            val resLong = res.toLong()
            tvResultado.text = if (res == resLong.toDouble()) resLong.toString() else String.format("%.4f", res)
        } catch (e: Exception) {
            tvResultado.text = "Error"
        }
    }
}
