package com.tuapp.calculadora

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
    private val cryptoManager = CryptoManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // SEGURIDAD: Bloqueo de capturas y ocultar en multitarea
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
    }

    /**
     * BLINDAJE DE SEGURIDAD:
     * Si el usuario sale de la app, cerramos el reproductor.
     * Esto dispara onDestroy() y borra el video descifrado de la caché.
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        finish()
    }

    private fun cargarYReproducir() {
        val rutas = listaRutas
        if (rutas != null && posicionActual in rutas.indices) {
            val pathCifrado = rutas[posicionActual]
            descifrarVideo(pathCifrado)
        } else {
            Toast.makeText(this, "Fin de la lista", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun descifrarVideo(pathCifrado: String) {
        progressBar.visibility = View.VISIBLE
        videoView.stopPlayback() 

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Limpieza inmediata del temporal anterior antes de crear uno nuevo
                borrarArchivoTemporal()
                
                val archivoCifrado = File(pathCifrado)
                archivoTemporal = File(cacheDir, "vault_stream_${System.currentTimeMillis()}.mp4")
                
                val fis = FileInputStream(archivoCifrado)
                val fos = FileOutputStream(archivoTemporal)
                
                // DESCIFRADO AES
                cryptoManager.decryptToStream(fis, fos)
                
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (archivoTemporal?.exists() == true) {
                        videoView.setVideoURI(Uri.fromFile(archivoTemporal))
                        videoView.start()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ReproductorActivity, "Error de seguridad", Toast.LENGTH_SHORT).show()
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
        try {
            archivoTemporal?.let {
                if (it.exists()) it.delete()
            }
        } catch (e: Exception) { e.printStackTrace() }
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
