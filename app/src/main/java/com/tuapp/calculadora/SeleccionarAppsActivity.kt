package com.tuapp.calculadora

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SeleccionarAppsActivity : AppCompatActivity() {

    private lateinit var rvApps: RecyclerView
    private lateinit var btnGuardar: Button
    private var listaApps = mutableListOf<AppInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_apps)

        rvApps = findViewById(R.id.rvApps)
        btnGuardar = findViewById(R.id.btnGuardarApps)

        cargarApps()

        btnGuardar.setOnClickListener {
            guardarConfiguracion()
            finish() // Volver atrás al terminar
        }
    }

    private fun cargarApps() {
        val pm = packageManager
        val packs = pm.getInstalledPackages(0)
        val prefs = getSharedPreferences("AppsBloqueadasPrefs", Context.MODE_PRIVATE)
        val setBloqueadas = prefs.getStringSet("lista_bloqueadas", setOf()) ?: setOf()

        for (p in packs) {
            val launchIntent = pm.getLaunchIntentForPackage(p.packageName)
            if (launchIntent != null) { // Solo apps que se pueden abrir
                val nombre = p.applicationInfo.loadLabel(pm).toString()
                val icono = p.applicationInfo.loadIcon(pm)
                val isLocked = setBloqueadas.contains(p.packageName)
                
                listaApps.add(AppInfo(nombre, p.packageName, icono, isLocked))
            }
        }

        listaApps.sortBy { it.nombre }
        rvApps.layoutManager = LinearLayoutManager(this)
        rvApps.adapter = AppAdapter(listaApps)
    }

    private fun guardarConfiguracion() {
        val prefs = getSharedPreferences("AppsBloqueadasPrefs", Context.MODE_PRIVATE)
        val setParaGuardar = listaApps.filter { it.estaBloqueada }.map { it.packageName }.toSet()
        
        prefs.edit().putStringSet("lista_bloqueadas", setParaGuardar).apply()
    }
}
