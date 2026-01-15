package com.tuapp.calculadora

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class BovedaActivity : AppCompatActivity() {

    private val PICK_IMAGE_CODE = 100
    private lateinit var gridView: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boveda)

        gridView = findViewById(R.id.gvGaleria)
        val btnAgregar = findViewById<FloatingActionButton>(R.id.btnAgregar)

        // Al hacer clic en el botón +, se abre la galería del celular
        btnAgregar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_CODE)
        }

        // Cargar las fotos que ya existan al iniciar
        actualizarGaleria()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE) {
            val uri = data?.data
            if (uri != null) {
                guardarFotoEnBoveda(uri)
            }
        }
    }

    private fun guardarFotoEnBoveda(uri: Uri) {
        try {
            // 1. Ubicar la carpeta secreta dentro de la app
            val carpetaSecreta = File(filesDir, "mis_secretos")
            if (!carpetaSecreta.exists()) carpetaSecreta.mkdirs()

            // 2. Crear el archivo de destino con nombre único
            val nombreArchivo = "IMG_${System.currentTimeMillis()}.jpg" 
            val archivoDestino = File(carpetaSecreta, nombreArchivo)

            // 3. Copiar los datos de la galería a la carpeta secreta
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(archivoDestino)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            Toast.makeText(this, "Foto guardada en la bóveda", Toast.LENGTH_SHORT).show()
            
            // 4. Refrescar la pantalla para ver la nueva foto
            actualizarGaleria()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun actualizarGaleria() {
        val carpetaSecreta = File(filesDir, "mis_secretos")
        // Buscamos todos los archivos que hemos guardado
        val archivos = carpetaSecreta.listFiles()?.toList() ?: listOf()
        
        // Le pasamos la lista de archivos al "traductor" (Adapter)
        val adapter = FotoAdapter(this, archivos)
        gridView.adapter = adapter
    }
}
