package com.tuapp.calculadora

import android.net.Uri
import android.os.Bundle
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

        // Recuperamos la lista y la posición que mandamos desde VideoActivity
        listaRutas = intent.getStringArrayExtra("lista_videos")
        posicionActual = intent.getIntExtra("posicion", 0)

        // Controles de reproducción (Play/Pausa)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        reproducirVideo()

        // ESTO HACE QUE SE VEAN EN CONTINUACIÓN
        videoView.setOnCompletionListener {
            reproducirSiguiente()
        }
        
        // Si hay un error, intentamos pasar al siguiente
        videoView.setOnErrorListener { _, _, _ ->
            reproducirSiguiente()
            true
        }
    }

    private fun reproducirVideo() {
        val rutas = listaRutas
        if (rutas != null && posicionActual < rutas.size) {
            val path = rutas[posicionActual]
            videoView.setVideoURI(Uri.parse(path))
            videoView.start()
        } else {
            finish() // Si no hay más videos, cerramos el reproductor
        }
    }

    private fun reproducirSiguiente() {
        val rutas = listaRutas
        if (rutas != null && posicionActual < rutas.size - 1) {
            posicionActual++
            reproducirVideo()
            Toast.makeText(this, "Reproduciendo siguiente video", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Fin de la lista", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
