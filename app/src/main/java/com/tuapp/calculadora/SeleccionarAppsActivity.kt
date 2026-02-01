package com.tuapp.calculadora

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        // 1. Verificar si el permiso de accesibilidad está activo
        if (!estaServicioAccesibilidadActivado(this)) {
            mostrarDialogoPermiso()
        }

        cargarApps()

        btnGuardar.setOnClickListener {
            guardarConfiguracion()
            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
            finish() 
        }
    }

    private fun cargarApps() {
        val pm = packageManager
        val packs = pm.getInstalledPackages(0)
        val prefs = getSharedPreferences("AppsBloqueadasPrefs", Context.MODE_PRIVATE)
        val setBloqueadas = prefs.getStringSet("lista_bloqueadas", setOf()) ?: setOf()

        for (p in packs) {
            val launchIntent = pm.getLaunchIntentForPackage(p.packageName)
            // Filtramos para no mostrar nuestra propia calculadora en la lista
            if (launchIntent != null && p.packageName != packageName) { 
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

    // --- LÓGICA DE ACCESIBILIDAD ---

    private fun estaServicioAccesibilidadActivado(context: Context): Boolean {
        val serviceId = "${context.packageName}/${AppLockService::class.java.canonicalName}"
        val accessibilityEnabled = try {
            Settings.Secure.getInt(context.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Exception) { 0 }

        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return settingValue?.contains(serviceId) == true
        }
        return false
    }

    private fun mostrarDialogoPermiso() {
        AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
            .setTitle("Permiso Necesario")
            .setMessage("Para que el bloqueo de aplicaciones funcione, debes activar el Servicio de Accesibilidad de 'Calculadora Científica'.")
            .setPositiveButton("IR A ACTIVAR") { _, _ ->
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
                Toast.makeText(this, "Busca la app en 'Servicios instalados'", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("CANCELAR", null)
            .setCancelable(false)
            .show()
    }
}
