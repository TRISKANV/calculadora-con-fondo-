package com.tuapp.calculadora

import android.os.Bundle
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BovedaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boveda)

        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)

        btnMenu.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            
            // Agregamos las opciones al menú directamente desde el código
            popup.menu.add("📷 Fotos")
            popup.menu.add("🎥 Videos")
            popup.menu.add("🌐 Internet")
            popup.menu.add("📝 Notas")
            popup.menu.add("⚙️ Ajustes")
            popup.menu.add("❌ Salir")

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "📷 Fotos" -> Toast.makeText(this, "Abriendo Galería...", Toast.LENGTH_SHORT).show()
                    "🎥 Videos" -> Toast.makeText(this, "Abriendo Videos...", Toast.LENGTH_SHORT).show()
                    "🌐 Internet" -> Toast.makeText(this, "Navegador Seguro...", Toast.LENGTH_SHORT).show()
                    "📝 Notas" -> Toast.makeText(this, "Mis Notas...", Toast.LENGTH_SHORT).show()
                    "❌ Salir" -> finish()
                }
                true
            }
            popup.show()
        }
    }
}
