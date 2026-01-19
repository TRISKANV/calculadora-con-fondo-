package com.tuapp.calculadora

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream

class GaleriaActivity : AppCompatActivity() {

    private lateinit var rvGaleria: RecyclerView
    private var listaFotos = mutableListOf<File>()
    private lateinit var adapter: GaleriaAdapter // 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galeria)

        rvGaleria = findViewById(R.id.rvGaleria)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddFoto)

        // 
        rvGaleria.layoutManager = GridLayoutManager(this, 3)
        
        cargarFotosDesdeBoveda()

        // 
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    importarFotoABoveda(uri)
                }
            }
        }

        fabAdd.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            launcher.launch(intent)
        }
    }

    private fun cargarFotosDesdeBoveda() {
        val directorio = File(getExternalFilesDir(null), "FotosSecretas")
        if (!directorio.exists()) directorio.mkdirs()
        
        listaFotos = directorio.listFiles()?.toMutableList() ?: mutableListOf()
        //
    }

    private fun importarFotoABoveda(uriOriginal: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uriOriginal)
            val nombreArchivo = "HIDDEN_${System.currentTimeMillis()}.jpg"
            val carpetaPrivada = File(getExternalFilesDir(null), "FotosSecretas")
            if (!carpetaPrivada.exists()) carpetaPrivada.mkdirs()

            val archivoDestino = File(carpetaPrivada, nombreArchivo)

            // 1. Copiamos la foto a nuestra carpeta secreta
            val outputStream = FileOutputStream(archivoDestino)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            //  Intentamos borrar la original (Android pedirá permiso)
            
            contentResolver.delete(uriOriginal, null, null)

            Toast.makeText(this, "Foto movida a la bóveda", Toast.LENGTH_SHORT).show()
            cargarFotosDesdeBoveda()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error al importar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
