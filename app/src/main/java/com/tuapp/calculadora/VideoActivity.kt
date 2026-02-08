package com.triskanv.calculadora

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
    private var archivoAReproducir: File? = null
    private var esTemporal: Boolean = false
    private val cryptoManager = CryptoManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE, 
            WindowManager.LayoutParams.FLAG_SECURE
        )
        
        setContentView(R.layout.activity_video)

        videoView = findViewById(R.id.videoViewFull)
        progressBar = findViewById(R.id.pbCargandoVideo)
        
        val rutaOriginal = intent.getStringExtra("ruta_video")
        
        if (!rutaOriginal.isNullOrEmpty()) {
            gestionarOrigenVideo(rutaOriginal)
        } else {
            Toast.makeText(this, "Error: Ruta no encontrada", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /**
     * D
     */
    private fun gestionarOrigenVideo(ruta: String) {
        val archivo = File(ruta)
        
        if (archivo.name.lowercase().endsWith(".enc")) {
            // Caso 1: Archivo Cifrado
            descifrarYReproducir(archivo)
        } else {
            // 
            archivoAReproducir = archivo
            esTemporal = false
            iniciarPlayer()
        }
    }

    private fun descifrarYReproducir(archivoCifrado: File) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Creamos el temporal en cache
                val tempFile = File(cacheDir, "temp_v_${System.currentTimeMillis()}.mp4")
                
                val fis = FileInputStream(archivoCifrado)
                val fos = FileOutputStream(tempFile)

                cryptoManager.decryptToStream(fis, fos)

                withContext(Dispatchers.Main) {
                    archivoAReproducir = tempFile
                    esTemporal = true
                    progressBar.visibility = View.GONE
                    iniciarPlayer()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@VideoActivity, "Error al descifrar video", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun iniciarPlayer() {
        val mc = MediaController(this)
        mc.setAnchorView(videoView)
        videoView.setMediaController(mc)
        
        archivoAReproducir?.let {
            videoView.setVideoURI(Uri.fromFile(it))
        }
        
        videoView.setOnPreparedListener { mp ->
            mp.start()
        }

        videoView.setOnCompletionListener {
            finish()
        }
        
        videoView.setOnErrorListener { _, _, _ ->
            Toast.makeText(this, "Error: Formato de video no soportado", Toast.LENGTH_SHORT).show()
            finish()
            true
        }
    }

    private fun limpiarTemporal() {
        if (esTemporal) {
            try {
                archivoAReproducir?.let {
                    if (it.exists()) it.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // f
    }

    override fun onDestroy() {
        videoView.stopPlayback()
        limpiarTemporal()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
