package com.tuapp.calculadora

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class LockActivity : AppCompatActivity() {

    private var pinIngresado = ""
    private val PIN_CORRECTO = "1234" // 
    private lateinit var puntos: List<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_lock)

        // Inicializamos los circulitos del PIN
        puntos = listOf(
            findViewById(R.id.dot1), findViewById(R.id.dot2),
            findViewById(R.id.dot3), findViewById(R.id.dot4)
        )
    }

    // 
    fun onNumeroClick(view: View) {
        if (pinIngresado.length < 4) {
            val boton = view as Button
            pinIngresado += boton.text.toString()
            actualizarPuntos()

            if (pinIngresado.length == 4) {
                verificarPin()
            }
        }
    }

    private fun actualizarPuntos() {
        for (i in puntos.indices) {
            if (i < pinIngresado.length) {
                puntos[i].backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.white)
            } else {
                puntos[i].backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }
    }

    private fun verificarPin() {
        if (pinIngresado == PIN_CORRECTO) {
            finish() //
        } else {
            Toast.makeText(this, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
            pinIngresado = ""
            actualizarPuntos()
        }
    }

    override fun onBackPressed() {
        // a
        // 
        val startMain = android.content.Intent(android.content.Intent.ACTION_MAIN)
        startMain.addCategory(android.content.Intent.CATEGORY_HOME)
        startActivity(startMain)
    }
}
