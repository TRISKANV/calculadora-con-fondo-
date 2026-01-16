package com.tuapp.calculadora

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import java.io.File

class VisorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visor)

        val viewPager = findViewById<ViewPager2>(R.id.viewPagerFotos)
        val btnCerrar = findViewById<ImageButton>(R.id.btnCerrarVisor)

        // Obtener la ruta de la carpeta secreta
        val carpeta = File(getExternalFilesDir(null), "mis_secretos")
        // Filtrar solo imágenes (no videos)
        val listaFotos = carpeta.listFiles()
            ?.filter { it.extension.lowercase() != "mp4" }
            ?.sortedByDescending { it.lastModified() } ?: emptyList()

        // Obtener cuál foto se tocó en la pantalla anterior
        val rutaSeleccionada = intent.getStringExtra("ruta_imagen")
        val posicionInicial = listaFotos.indexOfFirst { it.absolutePath == rutaSeleccionada }

        // Configurar el adaptador
        val adaptador = VisorPagerAdapter(listaFotos)
        viewPager.adapter = adaptador
        
        // Empezar en la foto que tocaste
        if (posicionInicial != -1) {
            viewPager.setCurrentItem(posicionInicial, false)
        }

        btnCerrar.setOnClickListener { finish() }
    }
}
