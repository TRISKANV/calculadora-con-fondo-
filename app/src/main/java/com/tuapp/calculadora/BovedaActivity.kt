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
            // ELIMINADO EL TOAST:
            val intent = Intent(this, GaleriaActivity::class.java)
            startActivity(intent)
        }
        R.id.nav_videos -> {
            // ELIMINADO EL TOAST: 
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }
        R.id.nav_internet -> {
            val intent = Intent(this, NavegadorActivity::class.java)
            startActivity(intent)
        }
        R.id.nav_notas -> {
            // ESTO abrirá NotasActivity
            val intent = Intent(this, NotasActivity::class.java)
            startActivity(intent)
        }
        R.id.nav_ajustes -> {
            Toast.makeText(this, "Ajustes de Seguridad", Toast.LENGTH_SHORT).show()
        }
    }
    drawerLayout.closeDrawer(GravityCompat.END)
    true
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
