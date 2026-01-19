package com.tuapp.calculadora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class VideoAdapter(
    private var listaVideos: MutableList<File>,
    private val onVideoClick: (File) -> Unit,
    private val onVideoDelete: (Int) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Asegúrate de que estos IDs coincidan con tu item_video.xml
        val ivThumbnail: ImageView = view.findViewById(R.id.ivThumbnail)
        val btnBorrar: ImageButton = view.findViewById(R.id.btnBorrarFoto) 
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoFile = listaVideos[position]

        // Glide genera la miniatura del video automáticamente
        Glide.with(holder.itemView.context)
            .load(videoFile)
            .centerCrop()
            .into(holder.ivThumbnail)

        holder.itemView.setOnClickListener { onVideoClick(videoFile) }
        
        holder.btnBorrar.setOnClickListener { onVideoDelete(holder.adapterPosition) }
    }

    override fun getItemCount() = listaVideos.size
}
