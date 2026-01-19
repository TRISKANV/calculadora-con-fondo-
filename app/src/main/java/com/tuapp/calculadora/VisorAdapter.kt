package com.tuapp.calculadora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.chrisbanes.photoview.PhotoView
import java.io.File

class VisorAdapter(private val listaFotos: List<File>) : RecyclerView.Adapter<VisorAdapter.VisorViewHolder>() {

    class VisorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photoView: PhotoView = view.findViewById(R.id.photoViewFull)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisorViewHolder {
        // Creamos un diseño simple para cada página del visor
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_visor_foto, parent, false)
        return VisorViewHolder(view)
    }

    override fun onBindViewHolder(holder: VisorViewHolder, position: Int) {
        val fotoFile = listaFotos[position]
        holder.photoView.setImageURI(android.net.Uri.fromFile(fotoFile))
    }

    override fun getItemCount() = listaFotos.size
}
