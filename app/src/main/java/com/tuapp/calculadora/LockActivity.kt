package com.tuapp.calculadora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class LockActivity : AppCompatActivity() {

    private var pinIngresado = ""
    // Aquí podrías recuperar el PIN real de SharedPreferences, por ahora usemos "1234"
    private val PIN_CORRECTO = "1234" 
    
    private lateinit var puntos: List<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // SEGURIDAD: Bloquear capturas y ocultar de recientes
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_lock)

        // Referenciamos los 4 círculos del XML
        puntos = listOf(
            findViewById(R.id.dot1), 
            findViewById(R.id.dot2),
            findViewById(R.id.dot3), 
            findViewById(R.id.dot4)
        )
    }

    // Vincula este método a los botones del 0 al 9 en tu XML usando android:onClick="onNumeroClick"
    fun onNumeroClick(view: View) {
        if (pinIngresado.length < 4) {
            val boton = view as Button
            pinIngresado += boton.text.toString()
            actualizarInterfazPuntos()

            if (pinIngresado.length == 4) {
                verificarPin()
            }
        }
    }

    private fun actualizarInterfazPuntos() {
        for (i in puntos.indices) {
            if (i < pinIngresado.length) {
                // 
                puntos[i].setBackgroundResource(R.drawable.pin_dot_filled) 
            } else {
                // 
                puntos[i].setBackgroundResource(R.drawable.pin_dot)
            }
        }
    }

    private fun verificarPin() {
        if (pinIngresado == PIN_CORRECTO) {
            //
            finish() 
        } else {
            // 
            Toast.makeText(this, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
            pinIngresado = ""
            actualizarInterfazPuntos()
        }
    }

    // 
    fun onBorrarClick(view: View) {
        if (pinIngresado.isNotEmpty()) {
            pinIngresado = pinIngresado.dropLast(1)
            actualizarInterfazPuntos()
        }
    }

    override fun onBackPressed() {
        // 
        // 
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
