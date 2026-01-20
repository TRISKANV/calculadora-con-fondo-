package com.tuapp.calculadora

import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.VideoView
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReproductorActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private var listaRutas: Array<String>? = null
    private var posicionActual: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reproductor)

        videoView = findViewById(R.id.videoView)
        val btnSiguiente = findViewById<ImageButton>(R.id.btnSiguiente)
        val btnAnterior = findViewById<ImageButton>(R.id.btnAnterior)

        listaRutas = intent.getStringArrayExtra("lista_videos")
        posicionActual = intent.getIntExtra("posicion", 0)

        // Controles de barra de tiempo (Play/Pause/Seek)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        reproducirVideo()

        // 1. Botón Siguiente
        btnSiguiente.setOnClickListener {
            reproducirSiguiente()
        }

        // 2. Botón Anterior
        btnAnterior.setOnClickListener {
            reproducirAnterior()
        }

        // 3. Reproducción continua automática
        videoView.setOnCompletionListener {
            reproducirSiguiente()
        }
    }

    private fun reproducirVideo() {
        val rutas = listaRutas
        if (rutas != null && posicionActual in rutas.indices) {
            val path = rutas[posicionActual]
            videoView.setVideoURI(Uri.parse(path))
            videoView.start()
        } else {
            finish()
        }
    }

    private fun reproducirSiguiente() {
        val rutas = listaRutas
        if (rutas != null && posicionActual < rutas.size - 1) {
            posicionActual++
            reproducirVideo()
        } else {
            Toast.makeText(this, "Fin de la lista", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun reproducirAnterior() {
        if (posicionActual > 0) {
            posicionActual--
            reproducirVideo()
        } else {
            Toast.makeText(this, "Primer video", Toast.LENGTH_SHORT).show()
        }
    }
}
