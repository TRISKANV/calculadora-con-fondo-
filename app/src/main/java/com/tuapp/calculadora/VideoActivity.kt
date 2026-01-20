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
        setContentView(R.layout.activity_video)

        rvVideos = findViewById(R.id.rvVideos)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddVideo)

        rvVideos.layoutManager = GridLayoutManager(this, 2)
        
        actualizarLista()

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
                // Buscamos la posición del video que tocamos en la lista
                val posicion = listaVideos.indexOf(archivo)
                // Convertimos la lista de archivos a una lista de rutas (Strings)
                val rutas = listaVideos.map { it.absolutePath }.toTypedArray()

                val intent = Intent(this, ReproductorActivity::class.java)
                intent.putExtra("lista_videos", rutas) // Enviamos toda la lista
                intent.putExtra("posicion", posicion)  // Enviamos el índice actual
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
            
            // ELIMINAR EL ORIGINAL de la galería pública (Mover)
            contentResolver.delete(uri, null, null)

            Toast.makeText(this, "Video movido a la Bóveda", Toast.LENGTH_SHORT).show()
            actualizarLista()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar video", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarVideo(posicion: Int) {
        if (posicion !in listaVideos.indices) return
        val archivo = listaVideos[posicion]
        if (archivo.delete()) {
            listaVideos.removeAt(posicion)
            adapter.notifyItemRemoved(posicion)
            adapter.notifyItemRangeChanged(posicion, listaVideos.size)
            Toast.makeText(this, "Video eliminado", Toast.LENGTH_SHORT).show()
        }
    }
}
