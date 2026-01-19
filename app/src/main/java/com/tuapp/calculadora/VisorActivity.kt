package com.tuapp.calculadora

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import java.io.File

class VisorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visor_fotos)

        val viewPager = findViewById<ViewPager2>(R.id.viewPagerFotos)
        val btnCerrar = findViewById<ImageButton>(R.id.btnCerrarVisor)

        // 
        val carpetaPrivada = File(getExternalFilesDir(null), "FotosSecretas")
        val listaFotos = carpetaPrivada.listFiles()?.filter { it.isFile }?.toList() ?: emptyList()

        //
        val posicionInicial = intent.getIntExtra("posicion", 0)

        // 
        val adapter = VisorAdapter(listaFotos)
        viewPager.adapter = adapter
        
        //
        viewPager.setCurrentItem(posicionInicial, false)

        btnCerrar.setOnClickListener { finish() }
    }
}
