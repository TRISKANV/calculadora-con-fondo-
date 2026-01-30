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
        
        // RECUPERAMOS LA RUTA
        val rutaCifrada = intent.getStringExtra("ruta_video")
        
        if (!rutaCifrada.isNullOrEmpty()) {
            prepararYReproducir(rutaCifrada)
        } else {
            Toast.makeText(this, "Error: Ruta de video no recibida", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun prepararYReproducir(rutaCifrada: String) {
        // Mostramos el círculo de carga
        progressBar.visibility = View.VISIBLE

        // Usamos Corrutinas para no trabar la pantalla mientras descifra
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val archivoCifrado = File(rutaCifrada)
                archivoTemporal = File(cacheDir, "temp_video_${System.currentTimeMillis()}.mp4")
                
                val fis = FileInputStream(archivoCifrado)
                val fos = FileOutputStream(archivoTemporal)

                // Descifrado (esto puede tardar si el video es pesado)
                cryptoManager.decryptToStream(fis, fos)

                withContext(Dispatchers.Main) {
                    // Ocultamos carga y reproducimos
                    progressBar.visibility = View.GONE
                    configurarReproductor()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@VideoActivity, "Error al procesar video", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun configurarReproductor() {
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        
        videoView.apply {
            setMediaController(mediaController)
            setVideoURI(Uri.fromFile(archivoTemporal))
            setOnPreparedListener { it.start() }
            setOnErrorListener { _, _, _ ->
                Toast.makeText(this@VideoActivity, "Video incompatible", Toast.LENGTH_SHORT).show()
                finish()
                true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        archivoTemporal?.delete()
    }
}
