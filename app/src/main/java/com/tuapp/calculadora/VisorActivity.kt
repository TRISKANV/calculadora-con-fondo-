package com.tuapp.calculadora

import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import java.io.File

class VisorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mantenemos la seguridad de capturas
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContentView(R.layout.activity_visor_fotos)

        val viewPager = findViewById<ViewPager2>(R.id.viewPagerFotos)
        val btnCerrar = findViewById<ImageButton>(R.id.btnCerrarVisor)

        // Cargar lista
        val carpetaPrivada = File(getExternalFilesDir(null), "FotosSecretas")
        val listaFotos = carpetaPrivada.listFiles()?.filter { it.isFile }?.toList() ?: emptyList()

        // Obtener posición
        val posicionInicial = intent.getIntExtra("posicion", 0)

        // Adaptador
        val adapter = VisorAdapter(listaFotos)
        viewPager.adapter = adapter
        
        // Ir a la foto seleccionada
        viewPager.setCurrentItem(posicionInicial, false)

        btnCerrar.setOnClickListener { finish() }
    }

    // 
    
    override fun onBackPressed() {
        super.onBackPressed() // 
        finish()
    }
}
