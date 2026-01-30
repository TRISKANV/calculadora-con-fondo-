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
        
        // SEGURIDAD: Evita capturas de pantalla y grabaciones
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        
        setContentView(R.layout.activity_video)

        // 
        videoView = findViewById(R.id.videoViewFull)
        
        val rutaCifrada = intent.getStringExtra("ruta_video")
        
        if (rutaCifrada != null) {
            prepararYReproducir(rutaCifrada)
        } else {
            Toast.makeText(this, "Ruta de video no encontrada", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun prepararYReproducir(rutaCifrada: String) {
        try {
            val archivoCifrado = File(rutaCifrada)
            
            // 
            archivoTemporal = File(cacheDir, "temp_video_${System.currentTimeMillis()}.mp4")
            
            val fis = FileInputStream(archivoCifrado)
            val fos = FileOutputStream(archivoTemporal)

            //
            cryptoManager.decryptToStream(fis, fos) 

            // 
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            
            videoView.apply {
                setMediaController(mediaController)
                setVideoURI(Uri.fromFile(archivoTemporal))
                
                setOnPreparedListener { 
                    it.start() 
                }
                
                setOnErrorListener { _, _, _ ->
                    Toast.makeText(this@VideoActivity, "Error en el formato del video", Toast.LENGTH_SHORT).show()
                    finish()
                    true
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al descifrar el video", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 
        try {
            archivoTemporal?.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
