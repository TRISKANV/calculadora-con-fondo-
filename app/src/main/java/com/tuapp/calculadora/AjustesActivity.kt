package com.tuapp.calculadora

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AjustesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // SEGURIDAD: Bloquear capturas de pantalla y ocultar en recientes
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        
        setContentView(R.layout.activity_ajustes)

        val btnCranio = findViewById<LinearLayout>(R.id.btnIconoCranio)
        val btnClima = findViewById<LinearLayout>(R.id.btnIconoClima)
        val btnClassic = findViewById<LinearLayout>(R.id.btnIconoClassic)

        btnCranio.setOnClickListener { cambiarIcono("AliasCranio") }
        btnClima.setOnClickListener { cambiarIcono("AliasClima") }
        btnClassic.setOnClickListener { cambiarIcono("AliasClassic") }
    }

    /**
     * BLINDAJE: Si el usuario sale de Ajustes (Home o Recientes), 
     * cerramos la actividad para que tenga que volver a loguearse.
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        finish()
    }

    private fun cambiarIcono(nombreAlias: String) {
        val paquete = packageName
        val componentes = listOf(
            "$paquete.AliasCranio",
            "$paquete.AliasClima",
            "$paquete.AliasClassic"
        )

        try {
            componentes.forEach { nombreFull ->
                val nuevoEstado = if (nombreFull == "$paquete.$nombreAlias") {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                }

                packageManager.setComponentEnabledSetting(
                    ComponentName(this, nombreFull),
                    nuevoEstado,
                    PackageManager.DONT_KILL_APP
                )
            }
            
            // Explicación al usuario: El sistema tarda unos segundos en refrescar el icono
            Toast.makeText(this, "Icono cambiado. El sistema lo actualizará pronto.", Toast.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        // Al volver atrás, nos aseguramos de terminar la actividad
        super.onBackPressed()
        finish()
    }
}
