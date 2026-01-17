package com.tuapp.calculadora

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

        configurarBotones()
    }

    private fun configurarBotones() {
        val prefs = getSharedPreferences("DatosSecretos", Context.MODE_PRIVATE)

        // Buscamos todos los botones automáticamente en el rootLayout
        val root = findViewById<android.view.ViewGroup>(R.id.rootLayout)
        recursiveConfig(root)

        // Lógica especial para el botón IGUAL
        findViewById<Button>(R.id.btnIgual).setOnClickListener {
            val entrada = tvExpresion.text.toString()
            val passGuardada = prefs.getString("clave", null)

            if (passGuardada == null) {
                // Registro por primera vez
                if (entrada.length >= 4 && !entrada.contains(Regex("[+\\-*×÷]"))) {
                    prefs.edit().putString("clave", entrada).apply()
                    Toast.makeText(this, "Clave guardada: $entrada", Toast.LENGTH_LONG).show()
                    tvExpresion.text = ""
                } else {
                    Toast.makeText(this, "Poné 4 números sin signos", Toast.LENGTH_SHORT).show()
                }
            } else if (entrada == passGuardada) {
                // Acceso a la bóveda
                val intent = Intent(this, BovedaActivity::class.java)
                startActivity(intent)
            } else {
                // Cálculo matemático normal
                ejecutarCalculo()
            }
        }

        // Botón secreto: Mantener presionado % para ayuda (opcional)
        findViewById<Button>(R.id.btnAbrirBovedaInvisible)?.setOnLongClickListener {
            Toast.makeText(this, "Calculadora Científica v1.0", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun recursiveConfig(view: android.view.View) {
        if (view is Button && view.id != R.id.btnIgual) {
            view.setOnClickListener { alPresionarBoton(view.text.toString()) }
        } else if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                recursiveConfig(view.getChildAt(i))
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
                val current = tvExpresion.text.toString()
                if (current.isNotEmpty()) tvExpresion.text = current.dropLast(1)
            }
            "×" -> tvExpresion.append("*")
            "÷" -> tvExpresion.append("/")
            "π" -> tvExpresion.append("3.14159")
            "e" -> tvExpresion.append("2.71828")
            "%" -> tvExpresion.append("/100")
            "sin", "cos", "tan", "log", "ln" -> tvExpresion.append("$valor(")
            else -> tvExpresion.append(valor)
        }
    }

    private fun ejecutarCalculo() {
        try {
            val expresionStr = tvExpresion.text.toString()
                .replace("×", "*")
                .replace("÷", "/")
            
            val expresion = ExpressionBuilder(expresionStr).build()
            val resultado = expresion.evaluate()
            
            val resultLong = resultado.toLong()
            if (resultado == resultLong.toDouble()) {
                tvResultado.text = resultLong.toString()
            } else {
                tvResultado.text = String.format("%.4f", resultado)
            }
        } catch (e: Exception) {
            tvResultado.text = "Error"
        }
    }
}
