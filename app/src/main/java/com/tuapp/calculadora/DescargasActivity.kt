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
        
        // --- SEGURIDAD 1: BLOQUEO DE CAPTURAS ---
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        supportActionBar?.hide()
        setContentView(R.layout.activity_descargas)

        // Inicializar vistas del nuevo XML
        rvDescargas = findViewById(R.id.rvDescargas)
        layoutVacio = findViewById(R.id.layoutVacio)
        
        rvDescargas.layoutManager = LinearLayoutManager(this)

        cargarDescargasPrivadas()
    }

    private fun cargarDescargasPrivadas() {
        // Buscamos en la carpeta privada (Android/data/com.tuapp.calculadora/files/Download)
        val carpetaPrivada = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        listaArchivos = carpetaPrivada?.listFiles()?.toMutableList() ?: mutableListOf()

        // Control de estado vacío (Camuflaje)
        if (listaArchivos.isEmpty()) {
            layoutVacio.visibility = View.VISIBLE
            rvDescargas.visibility = View.GONE
        } else {
            layoutVacio.visibility = View.GONE
            rvDescargas.visibility = View.VISIBLE
            
            // Configurar el adaptador con los clicks
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
            val uri: Uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )
            
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, contentResolver.getType(uri))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No hay app para abrir este archivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoBorrar(posicion: Int) {
        val archivo = listaArchivos[posicion]
        AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Eliminar archivo")
            .setMessage("¿Borrar permanentemente ${archivo.name}?")
            .setPositiveButton("Borrar") { _, _ ->
                if (archivo.delete()) {
                    Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
                    cargarDescargasPrivadas() // Recargar lista
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // --- SEGURIDAD 2: AUTO-CIERRE ---
    override fun onStop() {
        super.onStop()
        finish() // Cierra la actividad al salir para que no quede en segundo plano
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
