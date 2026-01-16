package com.tuapp.calculadora

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream

class BovedaActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var adaptador: FotoAdapter
    private val listaArchivos = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boveda)

        gridView = findViewById(R.id.gridViewFotos)
        val btnAgregar = findViewById<FloatingActionButton>(R.id.btnAgregarFoto)

        // 1. Cargar archivos al iniciar
        cargarArchivosDesdeCarpeta()

        // 2. Configurar el seleccionador (ahora acepta imágenes y videos)
        val seleccionarArchivoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    guardarArchivoEnCarpetaSecreta(uri)
                }
            }
        }

        btnAgregar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "*/*"
            val mimeTypes = arrayOf("image/*", "video/*")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            seleccionarArchivoLauncher.launch(intent)
        }

        // 3. Lógica para abrir Visor (Foto) o Reproductor (Video)
        gridView.setOnItemClickListener { _, _, position, _ ->
            val archivo = listaArchivos[position]
            val intent: Intent
            
            if (archivo.extension.lowercase() == "mp4") {
                intent = Intent(this, VideoActivity::class.java)
                intent.putExtra("ruta_video", archivo.absolutePath)
            } else {
                intent = Intent(this, VisorActivity::class.java)
                intent.putExtra("ruta_imagen", archivo.absolutePath)
            }
            startActivity(intent)
        }
    }

    private fun cargarArchivosDesdeCarpeta() {
        val carpeta = File(getExternalFilesDir(null), "mis_secretos")
        if (!carpeta.exists()) carpeta.mkdirs()

        val archivos = carpeta.listFiles()
        listaArchivos.clear()
        if (archivos != null) {
            // Ordenar por los más nuevos primero
            listaArchivos.addAll(archivos.filter { it.isFile }.sortedByDescending { it.lastModified() })
        }

        adaptador = FotoAdapter(this, listaArchivos)
        gridView.adapter = adaptador
    }

    private fun guardarArchivoEnCarpetaSecreta(uri: Uri) {
        try {
            val contentResolver = contentResolver
            val tipoMime = contentResolver.getType(uri)
            val extension = if (tipoMime?.contains("video") == true) "mp4" else "jpg"
            
            val inputStream = contentResolver.openInputStream(uri)
            val carpeta = File(getExternalFilesDir(null), "mis_secretos")
            val nombreArchivo = "FILE_${System.currentTimeMillis()}.$extension"
            val archivoDestino = File(carpeta, nombreArchivo)

            val outputStream = FileOutputStream(archivoDestino)
            inputStream?.copyTo(outputStream)
            
            inputStream?.close()
            outputStream.close()

            Toast.makeText(this, "Archivo guardado en la bóveda", Toast.LENGTH_SHORT).show()
            cargarArchivosDesdeCarpeta()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarArchivosDesdeCarpeta()
    }
}
