package com.tuapp.calculadora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class VideoGaleriaAdapter(
    private val lista: List<File>,
    private val onVideoClick: (File) -> Unit,
    private val onVideoDelete: (Int) -> Unit
) : RecyclerView.Adapter<VideoGaleriaAdapter.ViewHolder>() {

    // 
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
        
        // 
        holder.ivThumbnail.setImageResource(android.R.drawable.presence_video_online)
        
        holder.itemView.setOnClickListener { onVideoClick(file) }
        holder.btnBorrar.setOnClickListener { onVideoDelete(position) }
    }

    override fun getItemCount() = lista.size
}
