package com.tuapp.calculadora

import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.VideoView
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class VideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reproductor)

        val videoView = findViewById<VideoView>(R.id.videoView)
        val btnEliminar = findViewById<ImageButton>(R.id.btnEliminarVideo)
        val ruta = intent.getStringExtra("ruta_video")

        if (ruta != null) {
            val archivo = File(ruta)
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)
            videoView.setVideoURI(Uri.fromFile(archivo))
            videoView.start()

            btnEliminar.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Eliminar video")
                    .setMessage("¿Borrar este video permanentemente?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        if (archivo.delete()) {
                            finish()
                        }
                    }
                    .setNegativeButton("Cancelar", null).show()
            }
        }
    }
}
