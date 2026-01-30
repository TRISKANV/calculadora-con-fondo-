package com.tuapp.calculadora

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class DescargasActivity : AppCompatActivity() {

    private lateinit var rvDescargas: RecyclerView
    private lateinit var layoutVacio: LinearLayout
    private lateinit var adapter: DescargasAdapter
    private var listaArchivos = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // SEGURIDAD: Bloquear capturas y ocultar de la vista de recientes
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        supportActionBar?.hide()
        setContentView(R.layout.activity_descargas)

        rvDescargas = findViewById(R.id.rvDescargas)
        layoutVacio = findViewById(R.id.layoutVacio)
        
        rvDescargas.layoutManager = LinearLayoutManager(this)

        cargarDescargasPrivadas()
    }

    /**
     * BLINDAJE DE SEGURIDAD:
     * Si el usuario sale de la app presionando Home o Recientes,
     * cerramos la actividad para proteger el contenido.
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        finish()
    }

    private fun cargarDescargasPrivadas() {
        val carpetaPrivada = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        listaArchivos = carpetaPrivada?.listFiles()?.toMutableList() ?: mutableListOf()

        if (listaArchivos.isEmpty()) {
            layoutVacio.visibility = View.VISIBLE
            rvDescargas.visibility = View.GONE
        } else {
            layoutVacio.visibility = View.GONE
            rvDescargas.visibility = View.VISIBLE
            
            adapter = DescargasAdapter(
                listaArchivos,
                onClick = { archivo -> abrirArchivoSeguro(archivo) },
                onLongClick = { posicion -> mostrarDialogoBorrar(posicion) }
            )
            rvDescargas.adapter = adapter
        }
    }

    private fun abrirArchivoSeguro(file: File) {
        try {
            // Generamos la URI mediante el FileProvider definido en el Manifest
            val uri: Uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )
            
            // Determinamos el tipo de archivo (PDF, DOCX, etc.)
            val mimeType = contentResolver.getType(uri) ?: "*/*"
            
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                // Usamos FLAG_ACTIVITY_NO_HISTORY para que la app que abre el archivo
                // tampoco lo guarde en su historial reciente de forma insegura
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No hay una aplicación para abrir este archivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoBorrar(posicion: Int) {
        val archivo = listaArchivos[posicion]
        AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Eliminar archivo")
            .setMessage("¿Deseas borrar permanentemente ${archivo.name}?")
            .setPositiveButton("Borrar") { _, _ ->
                if (archivo.delete()) {
                    Toast.makeText(this, "Archivo eliminado", Toast.LENGTH_SHORT).show()
                    cargarDescargasPrivadas() 
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onBackPressed() {
        // Al volver atrás, terminamos la actividad para regresar al menú de la bóveda
        super.onBackPressed()
        finish()
    }
}
