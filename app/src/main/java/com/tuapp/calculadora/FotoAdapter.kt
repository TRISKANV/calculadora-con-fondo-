package com.tuapp.calculadora

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

class FotoAdapter(private val context: Context, private val listaArchivos: List<File>) : BaseAdapter() {

    override fun getCount(): Int = listaArchivos.size

    override fun getItem(position: Int): Any = listaArchivos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_foto, parent, false)
        
        val imageView = view.findViewById<ImageView>(R.id.imgMiniatura)
        val iconoPlay = view.findViewById<ImageView>(R.id.imgPlayIcon) // Necesitaremos agregar esto al XML
        val archivo = listaArchivos[position]

        // 1. Cargar la miniatura (Glide detecta automáticamente si es video o foto)
        Glide.with(context)
            .load(archivo)
            .centerCrop()
            .into(imageView)

        // 2. Mostrar el icono de Play solo si es un video .mp4
        if (archivo.extension.lowercase() == "mp4") {
            iconoPlay.visibility = View.VISIBLE
        } else {
            iconoPlay.visibility = View.GONE
        }

        return view
    }
}
