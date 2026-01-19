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
        
        val videoPath = intent.getStringExtra("videoPath")

        if (videoPath != null) {
            val uri = Uri.parse(videoPath)
            videoView.setVideoURI(uri)

            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)

            videoView.start()
        }

        btnCerrar?.setOnClickListener { finish() }
    }
}
