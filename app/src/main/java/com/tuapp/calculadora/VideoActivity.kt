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
        setContentView(R.layout.activity_video) // Revisa que este sea tu XML de lista de videos

        rvVideos = findViewById(R.id.rvVideos)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddVideo)

        rvVideos.layoutManager = GridLayoutManager(this, 2) // 2 columnas porque son videos
        
        cargarVideosDesdeBoveda()

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    importarVideoABoveda(uri)
                }
            }
        }

        fabAdd.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            launcher.launch(intent)
        }
    }

    private fun cargarVideosDesdeBoveda() {
        val carpetaPrivada = File(getExternalFilesDir(null), "VideosSecretos")
        if (!carpetaPrivada.exists()) carpetaPrivada.mkdirs()
        
        listaVideos = carpetaPrivada.listFiles()?.filter { it.isFile }?.toMutableList() ?: mutableListOf()
        
        adapter = VideoAdapter(listaVideos, 
            onVideoClick = { archivo ->
                val intent = Intent(this, ReproductorActivity::class.java)
                intent.putExtra("videoPath", archivo.absolutePath)
                startActivity(intent)
            },
            onVideoDelete = { posicion ->
                borrarVideo(posicion)
            }
        )
        rvVideos.adapter = adapter
    }

    private fun importarVideoABoveda(uriOriginal: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uriOriginal)
            val nombreArchivo = "VIDEO_SEC_${System.currentTimeMillis()}.mp4"
            val carpetaPrivada = File(getExternalFilesDir(null), "VideosSecretos")
            val archivoDestino = File(carpetaPrivada, nombreArchivo)

            val outputStream = FileOutputStream(archivoDestino)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // Intentar borrar el original de la galería pública
            contentResolver.delete(uriOriginal, null, null)

            Toast.makeText(this, "Video guardado", Toast.LENGTH_SHORT).show()
            cargarVideosDesdeBoveda()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun borrarVideo(posicion: Int) {
        if (posicion in listaVideos.indices) {
            val archivo = listaVideos[posicion]
            if (archivo.delete()) {
                listaVideos.removeAt(posicion)
                adapter.notifyItemRemoved(posicion)
                adapter.notifyItemRangeChanged(posicion, listaVideos.size)
            }
        }
    }
}
