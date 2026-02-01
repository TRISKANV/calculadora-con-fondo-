package com.tuapp.calculadora

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.*
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LockActivity : AppCompatActivity() {

    private var pinIngresado = ""
    // En una fase avanzada, aquí recuperaríamos el PIN de SharedPreferences
    private val PIN_CORRECTO = "1234" 
    
    private lateinit var puntos: List<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // SEGURIDAD: Bloquear capturas de pantalla y ocultar de recientes
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_lock)

        // Referenciamos los 4 puntos indicadores del PIN
        puntos = listOf(
            findViewById(R.id.dot1), 
            findViewById(R.id.dot2),
            findViewById(R.id.dot3), 
            findViewById(R.id.dot4)
        )
    }

    // 
    fun onNumeroClick(view: View) {
        if (pinIngresado.length < 4) {
            val boton = view as Button
            pinIngresado += boton.text.toString()
            
            // 
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            
            actualizarInterfazPuntos()

            if (pinIngresado.length == 4) {
                verificarPin()
            }
        }
    }

    private fun actualizarInterfazPuntos() {
        for (i in puntos.indices) {
            if (i < pinIngresado.length) {
                puntos[i].setBackgroundResource(R.drawable.pin_dot_filled)
                //
                puntos[i].backgroundTintList = null 
            } else {
                puntos[i].setBackgroundResource(R.drawable.pin_dot)
                puntos[i].backgroundTintList = null
            }
        }
    }

    private fun verificarPin() {
        if (pinIngresado == PIN_CORRECTO) {
            // --- EFECTO DE ÉXITO ---
            val animSuccess = AnimationUtils.loadAnimation(this, R.anim.success_blink)
            
            puntos.forEach { dot ->
                // 
                dot.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                dot.startAnimation(animSuccess)
            }

            // 
            Handler(Looper.getMainLooper()).postDelayed({
                finish() 
            }, 600)

        } else {
            // --- EFECTO DE ERROR ---
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(300)
            }

            Toast.makeText(this, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
            
            // 
            pinIngresado = ""
            actualizarInterfazPuntos()
        }
    }

    // 
    fun onBorrarClick(view: View) {
        if (pinIngresado.isNotEmpty()) {
            pinIngresado = pinIngresado.dropLast(1)
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            actualizarInterfazPuntos()
        }
    }

    override fun onBackPressed() {
        // 
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
