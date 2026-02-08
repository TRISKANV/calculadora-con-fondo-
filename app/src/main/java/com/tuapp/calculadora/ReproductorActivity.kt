package com.triskanv.calculadora

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
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
    private var esArchivoTemporal: Boolean = false
    private val cryptoManager = CryptoManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        
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

        // Manejo de errores en el VideoView
        videoView.setOnErrorListener { _, _, _ ->
            Toast.makeText(this, "Error al reproducir este video", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // finish() // Comentado para evitar cierres molestos
    }

    private fun cargarYReproducir() {
        val rutas = listaRutas
        if (rutas != null && posicionActual in rutas.indices) {
            val path = rutas[posicionActual]
            val archivo = File(path)

            if (archivo.name.lowercase().endsWith(".enc")) {
                descifrarVideo(path)
            } else {
                reproducirDirecto(archivo)
            }
        } else {
            Toast.makeText(this, "Fin de la lista", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun reproducirDirecto(archivo: File) {
        borrarArchivoTemporal()
        esArchivoTemporal = false
        videoView.stopPlayback()
        videoView.setVideoURI(Uri.fromFile(archivo))
        videoView.start()
    }

    private fun descifrarVideo(pathCifrado: String) {
        progressBar.visibility = View.VISIBLE
        videoView.stopPlayback() 

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                borrarArchivoTemporal()
                
                val archivoCifrado = File(pathCifrado)
                val tempFile = File(cacheDir, "v_stream_${System.currentTimeMillis()}.mp4")
                
                val fis = FileInputStream(archivoCifrado)
                val fos = FileOutputStream(tempFile)
                
                cryptoManager.decryptToStream(fis, fos)
                
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (tempFile.exists()) {
                        archivoTemporal = tempFile
                        esArchivoTemporal = true
                        videoView.setVideoURI(Uri.fromFile(tempFile))
                        videoView.start()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ReproductorActivity, "Error de descifrado", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Es el último video", Toast.LENGTH_SHORT).show()
        }
    }

    private fun reproducirAnterior() {
        if (posicionActual > 0) {
            posicionActual--
            cargarYReproducir()
        } else {
            Toast.makeText(this, "Es el primer video", Toast.LENGTH_SHORT).show()
        }
    }

    private fun borrarArchivoTemporal() {
        if (esArchivoTemporal) {
            try {
                archivoTemporal?.let {
                    if (it.exists()) it.delete()
                }
            } catch (e: Exception) { e.printStackTrace() }
            esArchivoTemporal = false
        }
    }

    override fun onDestroy() {
        videoView.stopPlayback()
        borrarArchivoTemporal()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
