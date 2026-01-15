package com.tuapp.calculadora

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class BovedaActivity : AppCompatActivity() {

    private val PICK_IMAGE_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boveda)

        val btnAgregar = findViewById<FloatingActionButton>(R.id.btnAgregar)

        btnAgregar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_CODE)
        }
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
            // Creamos una carpeta llamada "mis_secretos" dentro de la app
            val carpetaSecreta = File(filesDir, "mis_secretos")
            if (!carpetaSecreta.exists()) carpetaSecreta.mkdirs()

            // Nombre único para la foto usando el tiempo actual
            val nombreArchivo = "IMG_${System.currentTimeMillis()}.dat" 
            val archivoDestino = File(carpetaSecreta, nombreArchivo)

            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(archivoDestino)

            // Copiamos los datos del archivo
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            Toast.makeText(this, "Foto guardada en la bóveda", Toast.LENGTH_SHORT).show()
            
            // Aquí llamarías a una función para refrescar la cuadrícula y ver la foto
            actualizarGaleria()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun actualizarGaleria() {
        // Por ahora, solo listaremos los archivos en la consola para saber que están ahí
        val carpetaSecreta = File(filesDir, "mis_secretos")
        val archivos = carpetaSecreta.listFiles()
        println("Archivos en bóveda: ${archivos?.size ?: 0}")
    }
}
