package com.tuapp.calculadora

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ReproductorActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar
    private var listaRutas: Array<String>? = null
    private var posicionActual: Int = 0
    private var archivoTemporal: File? = null
    private val cryptoManager = CryptoManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reproductor)

        videoView = findViewById(R.id.videoView)
        progressBar = findViewById(R.id.pbCargandoVideo)
        val btnSiguiente = findViewById<ImageButton>(R.id.btnSiguiente)
        val btnAnterior = findViewById<ImageButton>(R.id.btnAnterior)

        listaRutas = intent.getStringArrayExtra("lista_videos")
        posicionActual = intent.getIntExtra("posicion", 0)

        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        cargarYReproducir()

        btnSiguiente.setOnClickListener { reproducirSiguiente() }
        btnAnterior.setOnClickListener { reproducirAnterior() }
        videoView.setOnCompletionListener { reproducirSiguiente() }
    }

    private fun cargarYReproducir() {
        val rutas = listaRutas
        if (rutas != null && posicionActual in rutas.indices) {
            val pathCifrado = rutas[posicionActual]
            descifrarVideo(pathCifrado)
        } else {
            finish()
        }
    }

    private fun descifrarVideo(pathCifrado: String) {
        progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Borrar temporal anterior si existe
                archivoTemporal?.delete()
                
                val archivoCifrado = File(pathCifrado)
                archivoTemporal = File(cacheDir, "temp_video_${System.currentTimeMillis()}.mp4")
                
                val fis = FileInputStream(archivoCifrado)
                val fos = FileOutputStream(archivoTemporal)
                
                cryptoManager.decryptToStream(fis, fos)
                
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    videoView.setVideoURI(Uri.fromFile(archivoTemporal))
                    videoView.start()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ReproductorActivity, "Error al descifrar video", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun reproducirSiguiente() {
        val rutas = listaRutas
        if (rutas != null && posicionActual < rutas.size - 1) {
            posicionActual++
            cargarYReproducir()
        } else {
            finish()
        }
    }

    private fun reproducirAnterior() {
        if (posicionActual > 0) {
            posicionActual--
            cargarYReproducir()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        archivoTemporal?.delete()
    }
}
