package com.tuapp.calculadora

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.VideoView
import android.widget.MediaController
import android.widget.Toast
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class VideoActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar
    private var archivoTemporal: File? = null
    private val cryptoManager = CryptoManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // SEGURIDAD: Bloquear capturas y ocultar en la multitarea
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE, 
            WindowManager.LayoutParams.FLAG_SECURE
        )
        
        setContentView(R.layout.activity_video)

        videoView = findViewById(R.id.videoViewFull)
        progressBar = findViewById(R.id.pbCargandoVideo)
        
        val rutaCifrada = intent.getStringExtra("ruta_video")
        
        if (!rutaCifrada.isNullOrEmpty()) {
            descifrarYReproducir(rutaCifrada)
        } else {
            Toast.makeText(this, "Error: Ruta no encontrada", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /**
     * BLINDAJE CRÍTICO:
     * Si el usuario sale de la app (Home o Recientes), matamos la actividad.
     * Esto activa automáticamente el onDestroy() que borra el video temporal.
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        finish() 
    }

    private fun descifrarYReproducir(ruta: String) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val archivoCifrado = File(ruta)
                // Usamos un prefijo único para evitar conflictos
                archivoTemporal = File(cacheDir, "temp_vault_${System.currentTimeMillis()}.mp4")
                
                val fis = FileInputStream(archivoCifrado)
                val fos = FileOutputStream(archivoTemporal)

                // Descifrado AES-256 en hilo secundario
                cryptoManager.decryptToStream(fis, fos)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    iniciarPlayer()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@VideoActivity, "Error de seguridad al procesar", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun iniciarPlayer() {
        val mc = MediaController(this)
        mc.setAnchorView(videoView)
        videoView.setMediaController(mc)
        
        // Cargamos el archivo temporal que ahora es un MP4 estándar
        videoView.setVideoURI(Uri.fromFile(archivoTemporal))
        
        videoView.setOnPreparedListener { mp ->
            mp.start()
        }

        // Si el video termina solo, también borramos el temporal por seguridad
        videoView.setOnCompletionListener {
            limpiarTemporal()
            finish()
        }
    }

    private fun limpiarTemporal() {
        try {
            if (archivoTemporal?.exists() == true) {
                archivoTemporal?.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        // Aseguramos que el video deje de sonar y se borre el rastro
        videoView.stopPlayback()
        limpiarTemporal()
        super.onDestroy()
    }

    override fun onBackPressed() {
        // Al salir con el botón atrás, cerramos todo limpiamente
        super.onBackPressed()
        finish()
    }
}
