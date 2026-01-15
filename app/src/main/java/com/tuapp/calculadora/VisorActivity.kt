package com.tuapp.calculadora

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class VisorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visor)

        val ivFotoCompleta = findViewById<ImageView>(R.id.ivFotoCompleta)
        
        // Recibimos la ruta del archivo que mandó el Adapter
        val rutaImagen = intent.getStringExtra("ruta_imagen")
        
        if (rutaImagen != null) {
            val file = File(rutaImagen)
            ivFotoCompleta.setImageURI(Uri.fromFile(file))
        }
    }
}
