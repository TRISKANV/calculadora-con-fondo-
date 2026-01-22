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

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val btnAbrirMenu = findViewById<ImageButton>(R.id.btnAbrirMenu)
        val navView = findViewById<NavigationView>(R.id.navigationView)

        btnAbrirMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_fotos -> {
                    startActivity(Intent(this, GaleriaActivity::class.java))
                }
                R.id.nav_videos -> {
                    startActivity(Intent(this, VideoActivity::class.java))
                }
                R.id.nav_internet -> {
                    startActivity(Intent(this, NavegadorActivity::class.java))
                }
                R.id.nav_notas -> {
                    startActivity(Intent(this, NotasActivity::class.java))
                }
                // --- NUEVA OPCIÓN DE DESCARGAS ---
                R.id.nav_descargas -> {
                    startActivity(Intent(this, DescargasActivity::class.java))
                }
                // ---------------------------------
                // Busca este bloque dentro de BovedaActivity.kt
R.id.nav_ajustes -> {
    val intent = Intent(this, AjustesActivity::class.java)
    startActivity(intent)
}
            }
            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}
