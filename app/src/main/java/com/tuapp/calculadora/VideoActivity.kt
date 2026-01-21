package com.tuapp.calculadora

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

        // Registro del selector moderno para Videos
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    try {
                        // Solicitar permiso de larga duración para el archivo de video
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        guardarVideoEnBoveda(uri)
                    } catch (e: Exception) {
                        guardarVideoEnBoveda(uri)
                    }
                }
            }
        }

        fabAdd.setOnClickListener {
            // Usamos OPEN_DOCUMENT para tener permisos totales sobre el archivo elegido
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "video/*"
            }
            launcher.launch(intent)
        }
    }

    private fun actualizarLista() {
        val carpeta = File(getExternalFilesDir(null), "VideosSecretos")
        if (!carpeta.exists()) carpeta.mkdirs()
        
        listaVideos = carpeta.listFiles()?.filter { it.isFile }?.toMutableList() ?: mutableListOf()
        
        // Convertimos la lista de archivos a rutas para el Reproductor
        val rutas = listaVideos.map { it.absolutePath }.toTypedArray()

        adapter = VideoAdapter(listaVideos, 
            onVideoClick = { archivo ->
                val posicion = listaVideos.indexOf(archivo)
                val intent = Intent(this, ReproductorActivity::class.java)
                intent.putExtra("lista_videos", rutas)
                intent.putExtra("posicion", posicion)
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
            val carpeta = File(getExternalFilesDir(null), "VideosSecretos")
            if (!carpeta.exists()) carpeta.mkdirs()
            
            val nombreArchivo = "VIDEO_SEC_${System.currentTimeMillis()}.mp4"
            val archivoDestino = File(carpeta, nombreArchivo)

            // COPIAR EL VIDEO
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(archivoDestino).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            // BORRAR EL ORIGINAL (Mover)
            // En Android 11+ el sistema pedirá confirmación al usuario para borrar
            try {
                contentResolver.delete(uri, null, null)
            } catch (e: Exception) {
                // Si falla el borrado automático, se maneja silenciosamente o se avisa
            }

            Toast.makeText(this, "Video ocultado con éxito", Toast.LENGTH_SHORT).show()
            actualizarLista()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: No se pudo mover el video", Toast.LENGTH_LONG).show()
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
