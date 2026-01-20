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

    // 1. Asegúrate de que estas tres líneas estén aquí arriba
    private lateinit var rvGaleria: RecyclerView
    private lateinit var adapter: GaleriaAdapter
    private var listaFotos = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galeria)

        // 2. Aquí es donde se vincula con el XML
        rvGaleria = findViewById(R.id.rvGaleria)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddFoto)

        rvGaleria.layoutManager = GridLayoutManager(this, 3)
        
        cargarFotosDesdeBoveda()

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
        val carpetaPrivada = File(getExternalFilesDir(null), "FotosSecretas")
        if (!carpetaPrivada.exists()) carpetaPrivada.mkdirs()
        
        listaFotos = carpetaPrivada.listFiles()?.filter { it.isFile }?.toMutableList() ?: mutableListOf()
        
        adapter = GaleriaAdapter(listaFotos, 
            onFotoClick = { posicion ->
                val intent = Intent(this, VisorActivity::class.java)
                intent.putExtra("posicion", posicion)
                startActivity(intent)
            },
            onFotoDelete = { posicion ->
                borrarFotoDeBoveda(posicion)
            }
        )
        rvGaleria.adapter = adapter
    }

    private fun importarFotoABoveda(uriOriginal: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uriOriginal)
            val nombreArchivo = "SECRET_${System.currentTimeMillis()}.jpg"
            val carpetaPrivada = File(getExternalFilesDir(null), "FotosSecretas")
            val archivoDestino = File(carpetaPrivada, nombreArchivo)

            val outputStream = FileOutputStream(archivoDestino)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // Borrado de la galería pública
            contentResolver.delete(uriOriginal, null, null)
            Toast.makeText(this, "Foto protegida", Toast.LENGTH_SHORT).show()
            cargarFotosDesdeBoveda()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error al importar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun borrarFotoDeBoveda(posicion: Int) {
        if (posicion !in listaFotos.indices) return
        val archivoABorrar = listaFotos[posicion]
        
        if (archivoABorrar.exists()) {
            if (archivoABorrar.delete()) {
                listaFotos.removeAt(posicion)
                adapter.notifyItemRemoved(posicion)
                adapter.notifyItemRangeChanged(posicion, listaFotos.size)
                Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
