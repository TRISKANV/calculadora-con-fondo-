package com.tuapp.calculadora

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class DescargasActivity : AppCompatActivity() {

    private lateinit var rvDescargas: RecyclerView
    private lateinit var adapter: DescargasAdapter
    private var listaArchivos = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_descargas)

        // Inicializar la vista
        rvDescargas = findViewById(R.id.rvDescargas)
        rvDescargas.layoutManager = LinearLayoutManager(this)

        // Cargar los archivos guardados
        obtenerDescargasSecretas()
    }

    private fun obtenerDescargasSecretas() {
        val carpeta = File(getExternalFilesDir(null), "DescargasSecretas")
        
        // 1. Toque final de seguridad: Crear carpeta y archivo .nomedia
        if (!carpeta.exists()) carpeta.mkdirs()
        val noMedia = File(carpeta, ".nomedia")
        if (!noMedia.exists()) noMedia.createNewFile()

        // 2. Filtrar solo archivos reales (ignorando el .nomedia)
        val archivos = carpeta.listFiles()?.filter { it.isFile && it.name != ".nomedia" }
        
        if (archivos != null) {
            listaArchivos.clear()
            listaArchivos.addAll(archivos)
            
            // Configurar el adaptador con las funciones de Click y Click Largo (Borrar)
            adapter = DescargasAdapter(
                listaArchivos, 
                onClick = { archivo ->
                    abrirArchivo(archivo)
                },
                onLongClick = { posicion ->
                    confirmarEliminacion(posicion)
                }
            )
            rvDescargas.adapter = adapter
        }
    }

    private fun confirmarEliminacion(posicion: Int) {
        val archivo = listaArchivos[posicion]
        
        // Creamos un diálogo elegante para confirmar el borrado
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Eliminar archivo?")
        builder.setMessage("Se borrará permanentemente de la bóveda: \n\n${archivo.name}")
        
        builder.setPositiveButton("Eliminar") { _, _ ->
            try {
                if (archivo.delete()) {
                    listaArchivos.removeAt(posicion)
                    adapter.notifyItemRemoved(posicion)
                    // Notificar cambios en el rango para evitar errores de índice
                    adapter.notifyItemRangeChanged(posicion, listaArchivos.size)
                    Toast.makeText(this, "Archivo eliminado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No se pudo eliminar el archivo", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error al borrar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        
        builder.setNegativeButton("Cancelar", null)
        
        val dialog = builder.create()
        dialog.show()
    }

    private fun abrirArchivo(archivo: File) {
        // Por ahora solo avisamos que se intenta abrir. 
        // Aquí podrías agregar un Intent para ver imágenes o PDFs.
        Toast.makeText(this, "Abriendo: ${archivo.name}", Toast.LENGTH_SHORT).show()
    }
    
    // Si regresas a esta pantalla, refrescamos la lista por si hubo cambios
    override fun onResume() {
        super.onResume()
        obtenerDescargasSecretas()
    }
}
