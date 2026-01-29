package com.tuapp.calculadora

import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.VideoView
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class VideoActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private var archivoTemporal: File? = null
    private val cryptoManager = CryptoManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Seguridad ante todo
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_video)

        videoView = findViewById(R.id.videoViewFull)
        val rutaCifrada = intent.getStringExtra("ruta_video") ?: return

        prepararYReproducir(rutaCifrada)
    }

    private fun prepararYReproducir(rutaCifrada: String) {
        try {
            val archivoCifrado = File(rutaCifrada)
            
            // 1. Creamos un archivo temporal en la cache de la app
            // La carpeta cache se borra sola si el sistema necesita espacio
            archivoTemporal = File(cacheDir, "temp_video_${System.currentTimeMillis()}.mp4")
            
            val fis = FileInputStream(archivoCifrado)
            val fos = FileOutputStream(archivoTemporal)

            // 2. Desciframos el video al archivo temporal
            // Nota: En videos muy largos, esto puede tardar un par de segundos
            cryptoManager.decryptToStream(fis, fos) 

            // 3. Reproducimos el temporal
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)
            videoView.setVideoURI(Uri.fromFile(archivoTemporal))
            
            videoView.setOnPreparedListener { videoView.start() }

        } catch (e: Exception) {
            Toast.makeText(this, "Error al reproducir video", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 4. LIMPIEZA DE SENIOR: Borrar el rastro del video descifrado
        archivoTemporal?.delete()
    }
}
