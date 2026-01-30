package com.tuapp.calculadora

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class VideoGaleriaAdapter(
    private val lista: List<File>,
    private val onVideoClick: (File) -> Unit,
    private val onVideoDelete: (Int) -> Unit
) : RecyclerView.Adapter<VideoGaleriaAdapter.ViewHolder>() {

    private val cryptoManager = CryptoManager()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivThumbnail: ImageView = view.findViewById(R.id.ivThumbnail)
        val btnBorrar: ImageButton = view.findViewById(R.id.btnBorrarFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = lista[position]
        
        // Icono temporal mientras carga
        holder.ivThumbnail.setImageResource(android.R.drawable.ic_menu_gallery)
        
        // Cargamos la miniatura en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = obtenerMiniaturaCifrada(file, holder.itemView.context.cacheDir)
            withContext(Dispatchers.Main) {
                if (bitmap != null) {
                    holder.ivThumbnail.setImageBitmap(bitmap)
                }
            }
        }

        holder.itemView.setOnClickListener { onVideoClick(file) }
        holder.btnBorrar.setOnClickListener { onVideoDelete(position) }
    }

    // Función mágica para sacar miniatura de un video cifrado
    private fun obtenerMiniaturaCifrada(fileCifrado: File, cacheDir: File): Bitmap? {
        val tempFile = File(cacheDir, "thumb_${fileCifrado.name}.mp4")
        return try {
            val fis = FileInputStream(fileCifrado)
            val fos = FileOutputStream(tempFile)
            
            // Desciframos el video (solo lo necesario para la miniatura)
            cryptoManager.decryptToStream(fis, fos)
            
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(tempFile.absolutePath)
            // Capturamos el frame en el segundo 1
            val bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            tempFile.delete() 
        }
    }

    override fun getItemCount() = lista.size
}
