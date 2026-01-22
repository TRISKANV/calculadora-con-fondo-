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

        findViewById<LinearLayout>(R.id.btnIconoCranio).setOnClickListener {
            activarIcono("AliasCranio")
        }

        findViewById<LinearLayout>(R.id.btnIconoClima).setOnClickListener {
            activarIcono("AliasClima")
        }
    }

    private fun activarIcono(aliasName: String) {
        val aliases = listOf("AliasCranio", "AliasClima", "AliasClassic")
        val packageManager = packageManager

        aliases.forEach { alias ->
            val state = if (alias == aliasName) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }

            packageManager.setComponentEnabledSetting(
                ComponentName(this, "com.tuapp.calculadora.$alias"),
                state,
                PackageManager.DONT_KILL_APP
            )
        }

        Toast.makeText(this, "Cerrando app para aplicar cambios...", Toast.LENGTH_LONG).show()
        
        // Es necesario cerrar la app para que el sistema refresque el icono
        finishAffinity()
    }
}
