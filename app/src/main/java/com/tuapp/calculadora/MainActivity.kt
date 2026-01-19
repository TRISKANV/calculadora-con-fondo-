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
            "sin", "cos", "tan", "log", "ln" -> tvExpresion.append("$valor(")
            else -> tvExpresion.append(valor)
        }
        
        // Calcular en tiempo real mientras el usuario escribe, sin mostrar errores
        ejecutarCalculo(false)
    }

    private fun procesarIgual() {
        val entrada = tvExpresion.text.toString()
        val prefs = getSharedPreferences("DatosSecretos", Context.MODE_PRIVATE)
        val passGuardada = prefs.getString("clave", null)

        if (passGuardada == null) {
            if (entrada.length >= 4 && !entrada.contains(Regex("[+/*×÷-]"))) {
                prefs.edit().putString("clave", entrada).apply()
                Toast.makeText(this, "✅ CLAVE GUARDADA", Toast.LENGTH_LONG).show()
                tvExpresion.text = ""
                tvResultado.text = "0"
            } else {
                Toast.makeText(this, "Escribí 4 números y tocá =", Toast.LENGTH_SHORT).show()
            }
        } else if (entrada == passGuardada) {
            startActivity(Intent(this, BovedaActivity::class.java))
        } else {
            ejecutarCalculo(true) // Forzar resultado final
            tvExpresion.text = tvResultado.text
        }
    }

    private fun ejecutarCalculo(esFinal: Boolean) {
        var texto = tvExpresion.text.toString()
        if (texto.isEmpty()) return

        try {
            // 1. Limpieza de caracteres visuales
            texto = texto.replace("×", "*").replace("÷", "/")
            
            // 2. AUTO-CIERRE DE PARÉNTESIS (La magia para evitar el error sin(32 )
            val parentesisAbiertos = texto.count { it == '(' }
            val parentesisCerrados = texto.count { it == ')' }
            for (i in 1..(parentesisAbiertos - parentesisCerrados)) {
                texto += ")"
            }

            val expresion = ExpressionBuilder(texto).build()
            val res = expresion.evaluate()
            
            val resLong = res.toLong()
            val resultadoStr = if (res == resLong.toDouble()) resLong.toString() else String.format("%.4f", res)
            
            tvResultado.text = resultadoStr
        } catch (e: Exception) {
            if (esFinal) tvResultado.text = "Error"
            // Si no es final, simplemente no actualizamos el resultado para no molestar al usuario
        }
    }
}
