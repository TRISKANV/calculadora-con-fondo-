package com.tuapp.calculadora

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
// IMPORTANTE: Esta línea conecta el código con tus XML
import com.tuapp.calculadora.R 
import android.widget.ImageButton
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
        // Usamos ImageButton porque es el que pusimos en el XML anterior
        val btnAgregar = findViewById<ImageButton>(R.id.btnAgregarFoto)

        cargarArchivosDesdeCarpeta()

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

        gridView.setOnItemClickListener { _, _, position, _ ->
            val archivo = listaArchivos[position]
            if (archivo.extension.lowercase() == "mp4") {
                val intent = Intent(this, VideoActivity::class.java)
                intent.putExtra("ruta_video", archivo.absolutePath)
                startActivity(intent)
            } else {
                val intent = Intent(this, VisorActivity::class.java)
                intent.putExtra("ruta_imagen", archivo.absolutePath)
                startActivity(intent)
            }
        }
    }

    private fun cargarArchivosDesdeCarpeta() {
        val carpeta = File(getExternalFilesDir(null), "mis_secretos")
        if (!carpeta.exists()) carpeta.mkdirs()

        val archivos = carpeta.listFiles()
        listaArchivos.clear()
        if (archivos != null) {
            listaArchivos.addAll(archivos.filter { it.isFile }.sortedByDescending { it.lastModified() })
        }

        adaptador = FotoAdapter(this, listaArchivos)
        gridView.adapter = adaptador
    }

    private fun guardarArchivoEnCarpetaSecreta(uri: Uri) {
        try {
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

            Toast.makeText(this, "Guardado en la bóveda", Toast.LENGTH_SHORT).show()
            cargarArchivosDesdeCarpeta()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarArchivosDesdeCarpeta()
    }
}
