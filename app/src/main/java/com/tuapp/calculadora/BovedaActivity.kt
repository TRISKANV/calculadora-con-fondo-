package com.tuapp.calculadora

import android.os.Bundle
import android.view.GravityCorner
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.GravityCompat

class BovedaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boveda)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val btnAbrirMenu = findViewById<ImageButton>(R.id.btnAbrirMenu)
        val navView = findViewById<NavigationView>(R.id.navigationView)

        // 
        btnAbrirMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        // 
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_fotos -> Toast.makeText(this, "Fotos", Toast.LENGTH_SHORT).show()
                R.id.nav_videos -> Toast.makeText(this, "Videos", Toast.LENGTH_SHORT).show()
                R.id.nav_internet -> Toast.makeText(this, "Internet", Toast.LENGTH_SHORT).show()
                R.id.nav_notas -> Toast.makeText(this, "Notas", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }
    }
}
