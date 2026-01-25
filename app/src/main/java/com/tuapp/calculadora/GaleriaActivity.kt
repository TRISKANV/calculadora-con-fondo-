package com.tuapp.calculadora

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
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
    private lateinit var adapter: GaleriaAdapter
    private var listaFotos = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContentView(R.layout.activity_galeria)

        rvGaleria = findViewById(R.id.rvGaleria)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddFoto)

        rvGaleria.layoutManager = GridLayoutManager(this, 3)
        
        cargarFotosDesdeBoveda()

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    // Importamos la foto (el proceso de copiado)
                    importarFotoABoveda(uri)
                }
            }
        }

        fabAdd.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            launcher.launch(intent)
        }
    }

    private fun cargarFotosDesdeBoveda() {
        val carpetaPrivada = File(getExternalFilesDir(null), "FotosSecretas")
        if (!carpetaPrivada.exists()) carpetaPrivada.mkdirs()
        
        val archivosEnCarpeta = carpetaPrivada.listFiles()?.filter { it.isFile }?.toMutableList() ?: mutableListOf()
        
        // Ordenar por fecha para que la última aparezca primero
        archivosEnCarpeta.sortByDescending { it.lastModified() }
        
        listaFotos.clear()
        listaFotos.addAll(archivosEnCarpeta)
        
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        } else {
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
    }

    private fun importarFotoABoveda(uriOriginal: Uri) {
        try {
            val nombreArchivo = "SECRET_${System.currentTimeMillis()}.jpg"
            val carpetaPrivada = File(getExternalFilesDir(null), "FotosSecretas")
            if (!carpetaPrivada.exists()) carpetaPrivada.mkdirs()
            val archivoDestino = File(carpetaPrivada, nombreArchivo)

            contentResolver.openInputStream(uriOriginal)?.use { inputStream ->
                FileOutputStream(archivoDestino).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // Nota: Borrar la foto original (contentResolver.delete) suele fallar 
            // en versiones nuevas de Android por permisos. El usuario deberá borrarla a mano
            // o usar permisos de MediaStore si quieres hacerlo automático.

            Toast.makeText(this, "Foto protegida", Toast.LENGTH_SHORT).show()
            cargarFotosDesdeBoveda()
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al importar", Toast.LENGTH_LONG).show()
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
                Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ELIMINADO EL onStop PARA EVITAR EL CIERRE AL ELEGIR FOTOS

    override fun onBackPressed() {
        finish() // Cerramos solo cuando el usuario toca atrás
    }
}
