package com.tuapp.calculadora

import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class ReproductorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reproductor)

        val videoView = findViewById<VideoView>(R.id.videoViewPrincipal)
        val btnCerrar = findViewById<ImageButton>(R.id.btnCerrarReproductor)
        
        // Recibir la ruta del video que enviamos desde VideoActivity
        val path = intent.getStringExtra("videoPath")

        if (path != null) {
            val uri = Uri.parse(path)
            videoView.setVideoURI(uri)

            // Controles de reproducción (Play, Pausa, barra de tiempo)
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)

            videoView.start() // Empieza a reproducir automáticamente
        }

        btnCerrar.setOnClickListener {
            finish() // Cierra el reproductor y vuelve a la lista
        }
    }
}
