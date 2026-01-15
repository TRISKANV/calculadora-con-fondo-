package com.tuapp.calculadora

import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class VisorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visor)

        val ivFotoCompleta = findViewById<ImageView>(R.id.ivFotoCompleta)
        val btnEliminar = findViewById<ImageButton>(R.id.btnEliminar)
        
        val rutaImagen = intent.getStringExtra("ruta_imagen")
        
        if (rutaImagen != null) {
            val archivo = File(rutaImagen)
            ivFotoCompleta.setImageURI(Uri.fromFile(archivo))

            btnEliminar.setOnClickListener {
                // Mostrar una alerta de confirmación
                AlertDialog.Builder(this)
                    .setTitle("Eliminar foto")
                    .setMessage("¿Estás seguro de que quieres borrar esta foto de la bóveda?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        if (archivo.exists()) {
                            val eliminado = archivo.delete()
                            if (eliminado) {
                                Toast.makeText(this, "Foto eliminada", Toast.LENGTH_SHORT).show()
                                finish() // Cerramos el visor y volvemos a la galería
                            } else {
                                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }
}
