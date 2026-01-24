package com.tuapp.calculadora

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

class DescargasActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var listaArchivos: MutableList<File>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // --- SEGURIDAD 1: BLOQUEO DE CAPTURAS ---
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        supportActionBar?.hide()
        setContentView(R.layout.activity_descargas)

        listView = findViewById(R.id.lvDescargas) // Asegúrate de tener un ListView con este ID en tu XML

        cargarDescargasPrivadas()
    }

    private fun cargarDescargasPrivadas() {
        // Buscamos en la misma carpeta privada donde el Navegador guarda los archivos
        val carpetaPrivada = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        listaArchivos = carpetaPrivada?.listFiles()?.toMutableList() ?: mutableListOf()

        if (listaArchivos.isEmpty()) {
            Toast.makeText(this, "No hay archivos protegidos", Toast.LENGTH_SHORT).show()
        }

        val nombresArchivos = listaArchivos.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nombresArchivos)
        listView.adapter = adapter

        // Al tocar un archivo: Abrirlo
        listView.setOnItemClickListener { _, _, position, _ ->
            abrirArchivoSeguro(listaArchivos[position])
        }

        // Al mantener presionado: Borrarlo
        listView.setOnItemLongClickListener { _, _, position, _ ->
            mostrarDialogoBorrar(position)
            true
        }
    }

    private fun abrirArchivoSeguro(file: File) {
        try {
            // Usamos FileProvider para poder abrir archivos desde la carpeta privada
            val uri: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
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
        AlertDialog.Builder(this)
            .setTitle("Eliminar archivo")
            .setMessage("¿Quieres borrar este archivo permanentemente de la bóveda?")
            .setPositiveButton("Borrar") { _, _ ->
                if (listaArchivos[posicion].delete()) {
                    Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
                    cargarDescargasPrivadas()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // --- SEGURIDAD 2: AUTO-CIERRE ---
    override fun onStop() {
        super.onStop()
        finish()
    }

    override fun onBackPressed() {
        finish()
    }
}
