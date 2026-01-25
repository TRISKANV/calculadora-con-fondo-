package com.tuapp.calculadora

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
        
        // --- SEGURIDAD: Evita capturas y oculte el teclado en multitarea ---
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContentView(R.layout.activity_main)

        tvExpresion = findViewById(R.id.tvExpresion)
        tvResultado = findViewById(R.id.tvResultado)

        val prefs = getSharedPreferences("DatosSecretos", Context.MODE_PRIVATE)
        
        // Camuflaje inicial: Si no hay clave, no decimos "Crea contraseña" (es delatador)
        // Decimos algo como "0" o simplemente nada.
        if (prefs.getString("clave", null) == null) {
            tvExpresion.text = "" 
            Toast.makeText(this, "Bienvenido: Configura tu PIN y presiona =", Toast.LENGTH_LONG).show()
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
        
        when (valor) {
            "AC" -> {
                tvExpresion.text = ""
                tvResultado.text = "0"
            }
            "DEL" -> {
                if (actual.isNotEmpty()) tvExpresion.text = actual.dropLast(1)
            }
            ".", "+", "-", "×", "÷" -> {
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
            // Creación inicial
            if (entrada.length >= 4 && entrada.all { it.isDigit() }) {
                prefs.edit().putString("clave", entrada).apply()
                Toast.makeText(this, "✅ PIN CONFIGURADO", Toast.LENGTH_SHORT).show()
                tvExpresion.text = ""
                tvResultado.text = "0"
            } else {
                Toast.makeText(this, "Ingresa un PIN de 4 dígitos", Toast.LENGTH_SHORT).show()
            }
        } else if (entrada == passGuardada) {
            // --- ACCESO A BÓVEDA ---
            tvExpresion.text = ""  
            tvResultado.text = "0" 
            
            val intent = Intent(this, BovedaActivity::class.java)
            // FLAG_ACTIVITY_CLEAR_TOP evita que se queden actividades colgadas
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            // Es una operación matemática normal
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
            
            val abiertos = texto.count { it == '(' }
            val cerrados = texto.count { it == ')' }
            for (i in 1..(abiertos - cerrados)) texto += ")"

            val expresion = ExpressionBuilder(texto).build()
            val res = expresion.evaluate()
            
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
