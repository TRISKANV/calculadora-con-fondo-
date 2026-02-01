package com.tuapp.calculadora

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class BovedaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // SEGURIDAD: Bloquear capturas de pantalla y ocultar en recientes
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContentView(R.layout.activity_boveda)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val btnAbrirMenu = findViewById<ImageButton>(R.id.btnAbrirMenu)
        val navView = findViewById<NavigationView>(R.id.navigationView)

        // Abrir el menú lateral
        btnAbrirMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        // Configurar acciones del menú lateral
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_fotos -> startActivity(Intent(this, GaleriaActivity::class.java))
                val intent = Intent(this, ReproductorActivity::class.java)
                R.id.nav_internet -> startActivity(Intent(this, NavegadorActivity::class.java))
                R.id.nav_notas -> startActivity(Intent(this, NotasActivity::class.java))
                R.id.nav_descargas -> startActivity(Intent(this, DescargasActivity::class.java))
                R.id.nav_ajustes -> startActivity(Intent(this, AjustesActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }
    }

    /**
     * MEJORA DE SEGURIDAD:
     * Este método detecta cuando el usuario presiona "Home" o el botón de "Recientes".
     * Al hacer esto, cerramos la bóveda automáticamente.
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        finish() 
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            // Regresamos a la calculadora limpiando el historial
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}
