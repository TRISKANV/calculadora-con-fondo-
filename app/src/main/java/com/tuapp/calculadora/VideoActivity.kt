package com.tuapp.calculadora

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream

class VideoActivity : AppCompatActivity() {

    private lateinit var rvVideos: RecyclerView
    private lateinit var adapter: VideoAdapter
    private var listaVideos = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video) // Asegúrate que este XML exista en layout

        rvVideos = findViewById(R.id.rvVideos)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddVideo)

        // Configuramos la cuadrícula de videos (2 columnas)
        rvVideos.layoutManager = GridLayoutManager(this, 2)
        
        actualizarLista()

        // Configuración para elegir video de la galería
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    guardarVideoEnBoveda(uri)
                }
            }
        }

        fabAdd.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            launcher.launch(intent)
        }
    }

    private fun actualizarLista() {
        val carpeta = File(getExternalFilesDir(null), "VideosSecretos")
        if (!carpeta.exists()) carpeta.mkdirs()
        
        listaVideos = carpeta.listFiles()?.filter { it.isFile }?.toMutableList() ?: mutableListOf()
        
        adapter = VideoAdapter(listaVideos, 
            onVideoClick = { archivo ->
                // AQUÍ SE CONECTA CON EL REPRODUCTOR
                val intent = Intent(this, ReproductorActivity::class.java)
                intent.putExtra("videoPath", archivo.absolutePath)
                startActivity(intent)
            },
            onVideoDelete = { posicion ->
                eliminarVideo(posicion)
            }
        )
        rvVideos.adapter = adapter
    }

    private fun guardarVideoEnBoveda(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val carpeta = File(getExternalFilesDir(null), "VideosSecretos")
            val archivoDestino = File(carpeta, "VIDEO_${System.currentTimeMillis()}.mp4")

            inputStream?.use { input ->
                FileOutputStream(archivoDestino).use { output ->
                    input.copyTo(output)
                }
            }
            
            // Opcional: Intentar borrar el original si tienes permisos
            // contentResolver.delete(uri, null, null)

            Toast.makeText(this, "Video ocultado con éxito", Toast.LENGTH_SHORT).show()
            actualizarLista()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar video", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarVideo(posicion: Int) {
        val archivo = listaVideos[posicion]
        if (archivo.delete()) {
            listaVideos.removeAt(posicion)
            adapter.notifyItemRemoved(posicion)
            Toast.makeText(this, "Video eliminado", Toast.LENGTH_SHORT).show()
        }
    }
}
