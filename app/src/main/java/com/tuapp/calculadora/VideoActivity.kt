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
        
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_video)

        videoView = findViewById(R.id.videoViewFull)
        progressBar = findViewById(R.id.pbCargandoVideo)
        
        // Obtenemos la ruta que mandamos desde VideoGaleriaActivity
        val rutaCifrada = intent.getStringExtra("ruta_video")
        
        if (!rutaCifrada.isNullOrEmpty()) {
            descifrarYReproducir(rutaCifrada)
        } else {
            Toast.makeText(this, "Error: Ruta no encontrada", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun descifrarYReproducir(ruta: String) {
        progressBar.visibility = View.VISIBLE // Mostramos el círculo

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val archivoCifrado = File(ruta)
                archivoTemporal = File(cacheDir, "temp_${System.currentTimeMillis()}.mp4")
                
                val fis = FileInputStream(archivoCifrado)
                val fos = FileOutputStream(archivoTemporal)

                // Descifrado pesado en hilo secundario
                cryptoManager.decryptToStream(fis, fos)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE // Ocultamos carga
                    iniciarPlayer()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@VideoActivity, "Error al descifrar", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun iniciarPlayer() {
        val mc = MediaController(this)
        mc.setAnchorView(videoView)
        videoView.setMediaController(mc)
        videoView.setVideoURI(Uri.fromFile(archivoTemporal))
        videoView.setOnPreparedListener { it.start() }
    }

    override fun onDestroy() {
        super.onDestroy()
        archivoTemporal?.delete() // Seguridad: borramos el video descifrado
    }
}
