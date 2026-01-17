package com.tuapp.calculadora

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
    private var ultimaExpresion: String = ""
    
    // TU CLAVE SECRETA (Cambiála por la que quieras)
    private val CLAVE_SECRETA = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvExpresion = findViewById(R.id.tvExpresion)
        tvResultado = findViewById(R.id.tvResultado)

        // Buscamos todos los botones y les asignamos lógica
        configurarBotones()
    }

    private fun configurarBotones() {
        val IDsBotones = listOf(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".",
            "sin", "cos", "tan", "log", "ln", "π", "e", "(", ")", "^", "√", "!",
            "+", "-", "×", "÷", "AC", "DEL", "%"
        )

        // Iteramos por todos los botones para no escribir uno por uno
        val rootLayout = findViewById<android.widget.LinearLayout>(R.id.rootLayout) // Asegurate que el layout principal tenga este ID o usá findViewById en el contenedor de botones
        
        // Forma simple: buscamos por el texto del botón
        val botones = mAllButtons() 
        botones.forEach { btn ->
            btn.setOnClickListener {
                alPresionarBoton(btn.text.toString())
            }
        }

        // EL TRUCO: Si mantienen presionado el botón "%"
        findViewById<Button>(R.id.btnAbrirBovedaInvisible)?.setOnLongClickListener {
            Toast.makeText(this, "Modo Administrador", Toast.LENGTH_SHORT).show()
            true
        }

        // BOTÓN IGUAL (El que dispara la magia)
        findViewById<Button>(R.id.btnIgual).setOnClickListener {
            val contenido = tvExpresion.text.toString()
            
            if (contenido == CLAVE_SECRETA) {
                val intent = Intent(this, BovedaActivity::class.java)
                startActivity(intent)
            } else {
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
                val current = tvExpresion.text.toString()
                if (current.isNotEmpty()) tvExpresion.text = current.dropLast(1)
            }
            "×" -> tvExpresion.append("*")
            "÷" -> tvExpresion.append("/")
            "π" -> tvExpresion.append("3.14159")
            "e" -> tvExpresion.append("2.71828")
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
                tvResultado.text = resultado.toString()
            }
        } catch (e: Exception) {
            tvResultado.text = "Error"
        }
    }

    // Función auxiliar para obtener todos los botones del layout
    private fun mAllButtons(): List<Button> {
        val list = mutableListOf<Button>()
        val viewGroup = findViewById<android.view.ViewGroup>(android.R.id.content)
        fun findButtons(view: android.view.View) {
            if (view is Button) list.add(view)
            else if (view is android.view.ViewGroup) {
                for (i in 0 until view.childCount) findButtons(view.getChildAt(i))
            }
        }
        findButtons(viewGroup)
        return list
    }
}
// ... (tus otros imports)
import android.content.Context

class MainActivity : AppCompatActivity() {
    // ... (tus variables de TextView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // ... (inicializar tvExpresion y tvResultado)

        val prefs = getSharedPreferences("DatosSecretos", Context.MODE_PRIVATE)

        findViewById<Button>(R.id.btnIgual).setOnClickListener {
            val entrada = tvExpresion.text.toString()
            val passGuardada = prefs.getString("clave", null)

            if (passGuardada == null) {
                // CASO 1: REGISTRO (Primera vez)
                if (entrada.length >= 4) { // Ponemos un mínimo de 4 números
                    prefs.edit().putString("clave", entrada).apply()
                    Toast.makeText(this, "Contraseña guardada: $entrada", Toast.LENGTH_LONG).show()
                    tvExpresion.text = ""
                } else {
                    Toast.makeText(this, "Escribí al menos 4 números para tu clave", Toast.LENGTH_SHORT).show()
                }
            } else if (entrada == passGuardada) {
                // CASO 2: ACCESO
                val intent = Intent(this, BovedaActivity::class.java)
                startActivity(intent)
            } else {
                // CASO 3: ES UNA OPERACIÓN MATEMÁTICA
                ejecutarCalculo()
            }
        }
    }
    
    // ... (el resto de tus funciones: configurarBotones, ejecutarCalculo, etc.)
}
