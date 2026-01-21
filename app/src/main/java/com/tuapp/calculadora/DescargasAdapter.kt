package com.tuapp.calculadora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class DescargasAdapter(
    private val archivos: List<File>,
    private val onClick: (File) -> Unit
) : RecyclerView.Adapter<DescargasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreArchivo)
        val icono: ImageView = view.findViewById(R.id.ivIconoArchivo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_descarga, parent, false) // Necesitarás crear este pequeño XML
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val archivo = archivos[position]
        holder.nombre.text = archivo.name
        
        // Cambiar icono según extensión
        if (archivo.name.endsWith(".apk")) {
            holder.icono.setImageResource(android.R.drawable.sym_def_app_icon)
        } else {
            holder.icono.setImageResource(android.R.drawable.ic_menu_save)
        }

        holder.itemView.setOnClickListener { onClick(archivo) }
    }

    override fun getItemCount() = archivos.size
}
