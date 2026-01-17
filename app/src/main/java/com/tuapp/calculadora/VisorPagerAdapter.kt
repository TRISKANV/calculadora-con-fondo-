package com.tuapp.calculadora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import java.io.File

class VisorPagerAdapter(private val listaFotos: List<File>) :
    RecyclerView.Adapter<VisorPagerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Aquí cambiamos el tipo a PhotoView
        val imageView: PhotoView = view.findViewById(R.id.imgVisorFull)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_visor_foto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.imageView.context)
            .load(listaFotos[position])
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = listaFotos.size
}
