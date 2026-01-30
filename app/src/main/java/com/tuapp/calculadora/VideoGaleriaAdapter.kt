package com.tuapp.calculadora

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.LruCache
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
    
    // Caché en memoria para no descifrar la misma miniatura mil veces
    private val memoryCache: LruCache<String, Bitmap> = LruCache(20)

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
        
        // Limpiamos la imagen anterior para que no se vea mal al reciclar
        holder.ivThumbnail.setImageBitmap(null)
        holder.ivThumbnail.setImageResource(android.R.drawable.ic_menu_gallery)

        // Si ya está en caché, la ponemos de una
        val bitmapEnCache = memoryCache.get(file.absolutePath)
        if (bitmapEnCache != null) {
            holder.ivThumbnail.setImageBitmap(bitmapEnCache)
        } else {
            // Si no, la buscamos en segundo plano
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = obtenerMiniaturaCifrada(file, holder.itemView.context.cacheDir)
                if (bitmap != null) {
                    memoryCache.put(file.absolutePath, bitmap)
                    withContext(Dispatchers.Main) {
                        holder.ivThumbnail.setImageBitmap(bitmap)
                    }
                }
            }
        }

        holder.itemView.setOnClickListener { onVideoClick(file) }
        holder.btnBorrar.setOnClickListener { onVideoDelete(position) }
    }

    private fun obtenerMiniaturaCifrada(fileCifrado: File, cacheDir: File): Bitmap? {
        val tempFile = File(cacheDir, "thumb_${System.currentTimeMillis()}.mp4")
        return try {
            val fis = FileInputStream(fileCifrado)
            val fos = FileOutputStream(tempFile)
            cryptoManager.decryptToStream(fis, fos)
            
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(tempFile.absolutePath)
            val bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()
            bitmap
        } catch (e: Exception) {
            null
        } finally {
            if (tempFile.exists()) tempFile.delete()
        }
    }

    override fun getItemCount() = lista.size
}
