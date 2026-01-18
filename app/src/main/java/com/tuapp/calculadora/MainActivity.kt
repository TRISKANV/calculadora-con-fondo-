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

        // Buscamos el contenedor raíz
        val root = findViewById<ViewGroup>(R.id.rootLayout)
        
        // Configuramos TODOS los botones
        configurarTodosLosBotones(root)
    }

    private fun configurarTodosLosBotones(view: View) {
        val prefs = getSharedPreferences("DatosSecretos", Context.MODE_PRIVATE)

        if (view is Button) {
            val texto = view.text.toString()

            if (texto == "=") {
                // LÓGICA DE SEGURIDAD / IGUAL
                view.setOnClickListener {
                    val entrada = tvExpresion.text.toString()
                    val passGuardada = prefs.getString("clave", null)

                    if (passGuardada == null) {
                        // REGISTRO
                        if (entrada.length >= 4 && !entrada.contains(Regex("[+/*×÷-]"))) {
                            prefs.edit().putString("clave", entrada).apply()
                            Toast.makeText(this, "✅ CLAVE GUARDADA: $entrada", Toast.LENGTH_LONG).show()
                            tvExpresion.text = ""
                            tvResultado.text = "0"
                        } else {
                            Toast.makeText(this, "Poné 4 números y tocá =", Toast.LENGTH_SHORT).show()
                        }
                    } else if (entrada == passGuardada) {
                        // ACCESO
                        val intent = Intent(this, BovedaActivity::class.java)
                        startActivity(intent)
                    } else {
                        // CÁLCULO
                        ejecutarCalculo()
                    }
                }
            } else {
                // BOTONES NORMALES (AC, DEL, números, etc)
                view.setOnClickListener { alPresionarBoton(texto) }
            }
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                configurarTodosLosBotones(view.getChildAt(i))
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
    var texto = tvExpresion.text.toString()
    if (texto.isEmpty()) return
    
    try {
        
        texto = texto.replace("×", "*").replace("÷", "/")

       
        val parentesisAbiertos = texto.count { it == '(' }
        val parentesisCerrados = texto.count { it == ')' }
        val faltantes = parentesisAbiertos - parentesisCerrados
        
        
        if (faltantes > 0) {
            for (i in 1..faltantes) {
                texto += ")"
            }
        }
        // -------------------------------------------------------------

        val expresion = ExpressionBuilder(texto).build()
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
