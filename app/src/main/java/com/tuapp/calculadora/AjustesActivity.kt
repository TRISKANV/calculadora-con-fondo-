package com.tuapp.calculadora

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AjustesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        val btnCranio = findViewById<LinearLayout>(R.id.btnIconoCranio)
        val btnClima = findViewById<LinearLayout>(R.id.btnIconoClima)
        val btnClassic = findViewById<LinearLayout>(R.id.btnIconoClassic)

        btnCranio.setOnClickListener { cambiarIcono("AliasCranio") }
        btnClima.setOnClickListener { cambiarIcono("AliasClima") }
        btnClassic.setOnClickListener { cambiarIcono("AliasClassic") }
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
            Toast.makeText(this, "Icono cambiado. La app se actualizará en unos segundos.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
