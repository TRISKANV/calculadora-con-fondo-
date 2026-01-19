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

        val prefs = getSharedPreferences("DatosSecretos", Context.MODE_PRIVATE)
        if (prefs.getString("clave", null) == null) {
            tvExpresion.text = "Crea tu contraseña"
        }

        val root = findViewById<ViewGroup>(R.id.rootLayout)
        configurarTodosLosBotones(root)
    }

    private fun configurarTodosLosBotones(view: View) {
        if (view is Button) {
            view.setOnClickListener {
                val textoBoton = view.text.toString()
                if (textoBoton == "=") {
                    procesarIgual()
                } else {
                    alPresionarBoton(textoBoton)
                }
            }
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                configurarTodosLosBotones(view.getChildAt(i))
            }
        }
    }

    private fun alPresionarBoton(valor: String) {
        val actual = tvExpresion.text.toString()
        
        if (actual == "Crea tu contraseña") {
            tvExpresion.text = ""
        }

        when (valor) {
            "AC" -> {
                tvExpresion.text = ""
                tvResultado.text = "0"
            }
            "DEL" -> {
                if (actual.isNotEmpty()) tvExpresion.text = actual.dropLast(1)
            }
            ".", "+", "-", "×", "÷" -> {
                // REGLA DE ORO: No repetir operadores ni empezar con ellos (excepto el menos)
                if (actual.isNotEmpty()) {
                    val ultimoChar = actual.last().toString()
                    if (ultimoChar == "+" || ultimoChar == "-" || ultimoChar == "×" || ultimoChar == "÷" || ultimoChar == ".") {
                        tvExpresion.text = actual.dropLast(1) + valor.replace("×", "*").replace("÷", "/")
                    } else {
                        tvExpresion.append(valor.replace("×", "*").replace("÷", "/"))
                    }
                } else if (valor == "-") {
                    tvExpresion.append("-")
                }
            }
            "sin", "cos", "tan", "log", "ln" -> tvExpresion.append("$valor(")
            else -> tvExpresion.append(valor)
        }
        
        ejecutarCalculo(false)
    }

  private fun procesarIgual() {
        val entrada = tvExpresion.text.toString()
        val prefs = getSharedPreferences("DatosSecretos", Context.MODE_PRIVATE)
        val passGuardada = prefs.getString("clave", null)

        if (passGuardada == null) {
            // Lógica de creación de clave (solo números)
            if (entrada.length >= 4 && entrada.all { it.isDigit() }) {
                prefs.edit().putString("clave", entrada).apply()
                Toast.makeText(this, "✅ CLAVE GUARDADA", Toast.LENGTH_LONG).show()
                tvExpresion.text = ""
                tvResultado.text = "0"
            } else {
                Toast.makeText(this, "Escribí 4 números y tocá =", Toast.LENGTH_SHORT).show()
            }
        } else if (entrada == passGuardada) {
            // --- MEJORA DE SEGURIDAD 
            tvExpresion.text = ""  // Borramos la clave de la pantalla
            tvResultado.text = "0" // Reiniciamos el resultado
            
            // Entramos a la bóveda
            startActivity(Intent(this, BovedaActivity::class.java))
        } else {
            ejecutarCalculo(true)
            if (tvResultado.text != "Error") {
                tvExpresion.text = tvResultado.text
            }
        }
    }

    private fun ejecutarCalculo(esFinal: Boolean) {
        var texto = tvExpresion.text.toString()
        if (texto.isEmpty()) return

        try {
            texto = texto.replace("×", "*").replace("÷", "/")
            
            // Auto-cierre de paréntesis
            val abiertos = texto.count { it == '(' }
            val cerrados = texto.count { it == ')' }
            for (i in 1..(abiertos - cerrados)) texto += ")"

            val expresion = ExpressionBuilder(texto).build()
            val res = expresion.evaluate()
            
            // Manejo de División por Cero
            if (res.isInfinite() || res.isNaN()) {
                tvResultado.text = "Error"
                return
            }

            val resLong = res.toLong()
            tvResultado.text = if (res == resLong.toDouble()) resLong.toString() else String.format("%.4f", res)
            
        } catch (e: Exception) {
            if (esFinal) tvResultado.text = "Error"
        }
    }
}
