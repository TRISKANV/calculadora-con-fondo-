package com.tuapp.calculadora

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class BovedaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boveda)

        // 1. Referencias a los componentes de la interfaz
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val btnAbrirMenu = findViewById<ImageButton>(R.id.btnAbrirMenu)
        val navView = findViewById<NavigationView>(R.id.navigationView)

        // 2. Configurar el botón para abrir el menú lateral (desde la derecha)
        btnAbrirMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        // 3. Configurar las acciones del menú lateral
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_fotos -> {
                    Toast.makeText(this, "Próximamente: Galería Privada", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_videos -> {
                    Toast.makeText(this, "Próximamente: Videos Ocultos", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_internet -> {
                    // ABRE EL NAVEGADOR PROFESIONAL
                    val intent = Intent(this, NavegadorActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_notas -> {
                    // ABRE LA SECCIÓN DE NOTAS ENCRIPTADAS
                    val intent = Intent(this, NotasActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_ajustes -> {
                    Toast.makeText(this, "Ajustes de Seguridad", Toast.LENGTH_SHORT).show()
                }
            }
            // Cerrar el menú después de seleccionar una opción
            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }
    }

    // Lógica para que el botón físico "Atrás" cierre el menú antes de salir de la pantalla
    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}
